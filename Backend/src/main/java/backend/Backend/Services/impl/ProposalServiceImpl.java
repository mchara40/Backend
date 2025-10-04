package backend.Backend.Services.impl;

import backend.Backend.DTOS.ProposalDTO;
import backend.Backend.DTOS.ProposalProductDTO;
import backend.Backend.Entities.Invoices;
import backend.Backend.Entities.Products;
import backend.Backend.Entities.Proposal;
import backend.Backend.Entities.ProposalProduct;
import backend.Backend.Repositories.ProductsRepository;
import backend.Backend.Repositories.ProposalProductRepository;
import backend.Backend.Repositories.ProposalRepository;
import backend.Backend.Services.ProposalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProposalServiceImpl implements ProposalService {
    private ProductsRepository productsRepository;
    private ProposalRepository proposalRepository;
    private ProposalProductRepository proposalProductRepository;
    private ObjectMapper objectMapper;

    @Override
    public List<ProposalDTO> getAllProposals() {
        return proposalRepository.findAll()
                .stream()
                .map(proposal -> {
                    List<ProposalProductDTO> products = proposalProductRepository.findAllByProposal(proposal)
                            .stream()
                            .map(pp -> new ProposalProductDTO(pp.getProposalProductId(),pp.getProducts().getName(),pp.getProducts().getProductsCategories().getName(),pp.getProducts().getId(), pp.getProposal().getId(),pp.getTotalProductPrice(),pp.getDiscountProduct(),pp.getQuantity(), pp.getProductDescription(),pp.getCustomPrice()))
                            .collect(Collectors.toList());
                    return new ProposalDTO(proposal.getId(), proposal.getName(), proposal.getCode(), proposal.getDescription(),proposal.getTotalAmount(), products,proposal.getFileName(),proposal.getFile(),proposal.getDiscountProposalAmount(), proposal.getDiscountProposalText(), proposal.getDiscountStatus(),proposal.getUpdateDate(),proposal.getClientId(),proposal.getTotalAmountWithoutDiscount());
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProposalDTO createProposal(String proposalJson, MultipartFile file) throws IOException {
        ProposalDTO proposalDTO = parseProposalJson(proposalJson);
        Proposal proposal = createProposalEntity(proposalDTO, file);
        Proposal savedProposal = proposalRepository.save(proposal);
        saveProposalProducts(proposalDTO, savedProposal);

        return proposalDTO;
    }
    @Override
    public String generateNextProposalCode() {
        String lastCode = proposalRepository.findTopByOrderByCodeDesc()
                .map(Proposal::getCode)
                .orElse("ΠΡ-000");

        // Extract the numeric part
        int numberPart = Integer.parseInt(lastCode.substring(3));

        // Increment and format with leading zeros
        numberPart++;
        return String.format("ΠΡ-%03d", numberPart);
    }
    private ProposalDTO parseProposalJson(String proposalJson) throws IOException {
        return objectMapper.readValue(proposalJson, ProposalDTO.class);
    }

    private Proposal createProposalEntity(ProposalDTO proposalDTO, MultipartFile file) throws IOException {
        Proposal proposal = new Proposal();
        proposal.setName(proposalDTO.getName());
        if(proposalDTO.getCode() == null || proposalDTO.getCode().isEmpty()) {
            proposal.setCode(generateNextProposalCode());
        } else {
            proposal.setCode(proposalDTO.getCode());
        }
        proposal.setDescription(proposalDTO.getDescription());
        proposal.setTotalAmount(proposalDTO.getTotalAmount());
        proposal.setTotalAmountWithoutDiscount(proposalDTO.getTotalAmountWithoutDiscount());
        proposal.setDiscountProposalAmount(proposalDTO.getProposalDiscountAmount());
        proposal.setDiscountProposalText(proposalDTO.getProposalDiscountText());
        proposal.setDiscountStatus(proposalDTO.getDiscountStatus());
        proposal.setClientId(proposalDTO.getClientId());
        if (file != null && !file.isEmpty()) {
            byte[] fileBytes = file.getBytes();
            proposal.setFile(fileBytes);
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                proposal.setFileName(originalFilename.trim());
            }
        }

        return proposal;
    }

    private void saveProposalProducts(ProposalDTO proposalDTO, Proposal savedProposal) {
        proposalDTO.getProducts().forEach(dto -> {
            Products product = findProductById(dto.getProductId());
            ProposalProduct proposalProduct = createProposalProduct(dto, savedProposal, product);
            proposalProductRepository.save(proposalProduct);
        });
    }
    private Products findProductById(Long productId) {
        return productsRepository.findById(productId)
                .orElseThrow(() -> new  RuntimeException("Product not found with id: " + productId));
    }

    private ProposalProduct createProposalProduct(ProposalProductDTO dto, Proposal proposal, Products product) {
        ProposalProduct proposalProduct = new ProposalProduct();
        proposalProduct.setProposalId(proposal.getId());
        proposalProduct.setProductId(product.getId());
        proposalProduct.setProposal(proposal);
        proposalProduct.setProducts(product);
        proposalProduct.setTotalProductPrice(dto.getTotalProductPrice());
        proposalProduct.setDiscountProduct(dto.getDiscount());
        proposalProduct.setQuantity(dto.getQuantity());
        proposalProduct.setCustomPrice(dto.getCustomPrice());
        if(dto.getProductDescription()!=null){
            String description = new String(
                    dto.getProductDescription().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8
            );
            proposalProduct.setProductDescription(description);
        }

        return proposalProduct;
    }
//    @Override
//    public ProposalDTO updateProposal(Long proposalId, ProposalDTO proposalDTO) {
//        Proposal proposal = proposalRepository.findById(proposalId)
//                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));
//
//        proposal.setName(proposalDTO.getName());
//        proposal.setCode(proposalDTO.getCode());
//        proposal.setDescription(proposalDTO.getDescription());
//        proposal.setTotalAmount(proposalDTO.getTotalAmount());
//        Proposal updatedProposal = proposalRepository.save(proposal);
//        // Clear existing products associated with the proposal
//        proposalProductRepository.deleteAllByProposalById(proposal.getId());
//        proposalDTO.getProducts().forEach(dto->{
//            Products products = productsRepository.findById(dto.getProductId()).orElseThrow(()->new RuntimeException("Product not found"));
//            ProposalProduct proposalProduct = new ProposalProduct();
//            proposalProduct.setProposalId(updatedProposal.getId());
//            proposalProduct.setProductId(products.getId());
//            proposalProduct.setProposal(updatedProposal);
//            proposalProduct.setProducts(products);
//            proposalProduct.setTotalProductPrice(dto.getTotalProductPrice());
//            proposalProduct.setDiscountProduct(dto.getDiscount());
//            proposalProduct.setQuantity(dto.getQuantity());
//            proposalProductRepository.save(proposalProduct);
//        });
//        return getProposalById(proposalId);
//
//    }

    @Override
    public ProposalDTO updateProposal(Long proposalId, String proposalJson, MultipartFile file) throws IOException {
        // Parse the JSON string to ProposalDTO
        ProposalDTO proposalDTO = objectMapper.readValue(proposalJson, ProposalDTO.class);

        // Find existing proposal
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));

        // Update basic properties
        proposal.setName(proposalDTO.getName());
        proposal.setCode(proposalDTO.getCode());
        proposal.setDescription(proposalDTO.getDescription());
        proposal.setTotalAmount(proposalDTO.getTotalAmount());
        proposal.setTotalAmountWithoutDiscount(proposalDTO.getTotalAmountWithoutDiscount());
        proposal.setDiscountProposalAmount(proposalDTO.getProposalDiscountAmount());
        proposal.setDiscountProposalText(proposalDTO.getProposalDiscountText());
        proposal.setDiscountStatus(proposalDTO.getDiscountStatus());
        proposal.setUpdateDate(LocalDateTime.now());
        // Update file if provided
        if (file != null && !file.isEmpty()) {
            byte[] fileBytes = file.getBytes();
            proposal.setFile(fileBytes);
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                proposal.setFileName(originalFilename.trim());
            }
        }

        Proposal updatedProposal = proposalRepository.save(proposal);

        // Clear existing products associated with the proposal
        proposalProductRepository.deleteAllByProposalById(proposal.getId());

        // Save new products
        proposalDTO.getProducts().forEach(dto -> {
            Products products = productsRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            ProposalProduct proposalProduct = new ProposalProduct();
            proposalProduct.setProposalId(updatedProposal.getId());
            proposalProduct.setProductId(products.getId());
            proposalProduct.setProposal(updatedProposal);
            proposalProduct.setProducts(products);
            proposalProduct.setTotalProductPrice(dto.getTotalProductPrice());
            proposalProduct.setDiscountProduct(dto.getDiscount());
            proposalProduct.setQuantity(dto.getQuantity());
            if(dto.getProductDescription()!=null){
                String description = new String(
                        dto.getProductDescription().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );
                proposalProduct.setProductDescription(description);
            }
            proposalProductRepository.save(proposalProduct);
        });

        return getProposalById(proposalId);
    }

    @Override
    public void deleteProposal(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with ID: " + proposalId));
        proposalProductRepository.deleteAllByProposalById(proposal.getId());
        proposalRepository.delete(proposal);

    }

    @Override
    public ProposalDTO getProposalById(Long Id) {
        Proposal proposal = proposalRepository.findById(Id).orElseThrow(()->new RuntimeException("Proposal not found"));
        List<ProposalProductDTO> products = proposalProductRepository.findAllByProposal(proposal)
                .stream()
                .map(pp -> new ProposalProductDTO(pp.getProposalProductId(),pp.getProducts().getName(),pp.getProducts().getProductsCategories().getName(),pp.getProducts().getId(), pp.getProposal().getId(),pp.getTotalProductPrice(),pp.getDiscountProduct(),pp.getQuantity(), pp.getProductDescription(),pp.getCustomPrice()))
                .collect(Collectors.toList());
        return new ProposalDTO(proposal.getId(), proposal.getName(), proposal.getCode(), proposal.getDescription(),proposal.getTotalAmount(), products,proposal.getFileName(),proposal.getFile(),proposal.getDiscountProposalAmount(), proposal.getDiscountProposalText(), proposal.getDiscountStatus(),proposal.getUpdateDate(),proposal.getClientId(),proposal.getTotalAmountWithoutDiscount());
    }

    @Override
    public List<ProposalDTO> getProposalsByClientId(Long clientId) {
        List<Proposal> proposals = proposalRepository.findAllByClientId(clientId);
        return proposals.stream()
                .map(proposal -> {
                    List<ProposalProductDTO> products = proposalProductRepository.findAllByProposal(proposal)
                            .stream()
                            .map(pp -> new ProposalProductDTO(pp.getProposalProductId(),pp.getProducts().getName(),pp.getProducts().getProductsCategories().getName(),pp.getProducts().getId(), pp.getProposal().getId(),pp.getTotalProductPrice(),pp.getDiscountProduct(),pp.getQuantity(), pp.getProductDescription(),pp.getCustomPrice()))
                            .collect(Collectors.toList());
                    return new ProposalDTO(proposal.getId(), proposal.getName(), proposal.getCode(), proposal.getDescription(),proposal.getTotalAmount(), products,proposal.getFileName(),proposal.getFile(),proposal.getDiscountProposalAmount(), proposal.getDiscountProposalText(), proposal.getDiscountStatus(),proposal.getUpdateDate(),proposal.getClientId(),proposal.getTotalAmountWithoutDiscount());
                })
                .collect(Collectors.toList());
    }



    @Override
    public byte[] getProposalFile(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + proposalId));

        if (proposal.getFile() == null) {
            throw new RuntimeException("No file found for proposal with id: " + proposalId);
        }

        return proposal.getFile();
    }

    @Override
    public String getProposalFileName(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + proposalId));

        if (proposal.getFileName() == null) {
            throw new RuntimeException("No filename found for proposal with id: " + proposalId);
        }

        return proposal.getFileName();
    }

    @Override
    public ProposalDTO getProposalByCode(String code) {
        Proposal proposal = proposalRepository.findByCode(code);

        List<ProposalProductDTO> products = proposalProductRepository.findAllByProposal(proposal)
                .stream()
                .map(pp -> new ProposalProductDTO(pp.getProposalProductId(),pp.getProducts().getName(),pp.getProducts().getProductsCategories().getName(),pp.getProducts().getId(), pp.getProposal().getId(),pp.getTotalProductPrice(),pp.getDiscountProduct(),pp.getQuantity(), pp.getProductDescription(),pp.getCustomPrice()))
                .collect(Collectors.toList());

        return new ProposalDTO(proposal.getId(), proposal.getName(), proposal.getCode(), proposal.getDescription(),proposal.getTotalAmount(), products,proposal.getFileName(),proposal.getFile(),proposal.getDiscountProposalAmount(), proposal.getDiscountProposalText(), proposal.getDiscountStatus(),proposal.getUpdateDate(),proposal.getClientId(),proposal.getTotalAmountWithoutDiscount());
    }

}

