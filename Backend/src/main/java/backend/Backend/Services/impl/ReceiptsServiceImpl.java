package backend.Backend.Services.impl;


import backend.Backend.DTOS.ProposalDTO;
import backend.Backend.DTOS.ReceiptsDTO;
import backend.Backend.Entities.PaymentStatus;
import backend.Backend.Entities.Proposal;
import backend.Backend.Entities.Receipts;
import backend.Backend.Repositories.ReceiptsRepository;
import backend.Backend.Services.ReceiptsServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReceiptsServiceImpl implements ReceiptsServiceInterface {

    private ReceiptsRepository receiptsRepository;
    private ObjectMapper objectMapper;
    @Override
    public List<ReceiptsDTO> getAllReceipts() {
        return receiptsRepository.findAll().stream()
                .map(r->new ReceiptsDTO(
                        r.getId(),
                        r.getInvoiceId(),
                        r.getReceiptCode(),
                        r.getDescription(),
                        r.getUpdateDate(),
                        r.getFileName(),
                        r.getFile(),
                        r.getTotalPaidAmount(),
                        r.getClientId(),
                        r.getPaymentStatus(),
                        r.getChequeNumber(),
                        r.getInvoices().getCustomerName()
                )).collect(Collectors.toList());
    }

    @Override
    public ReceiptsDTO createReceipt(String receiptJson, MultipartFile file) throws IOException {
        ReceiptsDTO receiptsDTO = parseReceiptJson(receiptJson);
        Receipts receipts = createReceiptEntity(receiptsDTO, file);
        Receipts savedReceipts = receiptsRepository.save(receipts);
        return receiptsDTO;

    }

    @Override
    public String generateNextReceiptCode() {
        String lastCode = receiptsRepository.findTopByOrderByReceiptCodeDesc()
                .map(Receipts::getReceiptCode)
                .orElse("R000");

        // Extract the numeric part
        int numberPart = Integer.parseInt(lastCode.substring(1));

        // Increment and format with leading zeros
        numberPart++;
        return String.format("R%03d", numberPart);
    }

    @Override
    public byte[] getReceiptFile(Long receiptId) {
        Receipts receipts = receiptsRepository.findById(receiptId).orElseThrow();

        if(receipts.getFile() == null){
            throw new RuntimeException("No file found for proposal with id: " + receiptId);

        }
        return receipts.getFile();
    }

    @Override
    public String getReceiptlFileName(Long receiptId) {
        Receipts receipts = receiptsRepository.findById(receiptId).orElseThrow();

        if(receipts.getFileName() == null){
            throw new RuntimeException("No file found for proposal with id: " + receiptId);

        }
        return receipts.getFileName();
    }

    @Override
    public List<ReceiptsDTO> getAllReceiptsByInvoiceId(Long invoiceId) {
        return receiptsRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(r -> new ReceiptsDTO(
                        r.getId(),
                        r.getInvoiceId(),
                        r.getReceiptCode(),
                        r.getDescription(),
                        r.getUpdateDate(),
                        r.getFileName(),
                        r.getFile(),
                        r.getTotalPaidAmount(),
                        r.getClientId(),
                        r.getPaymentStatus(),
                        r.getChequeNumber(),
                        r.getInvoices().getCustomerName()
                ))
                .collect(Collectors.toList());
    }


    private ReceiptsDTO  parseReceiptJson(String receiptJson) throws IOException {
        return objectMapper.readValue(receiptJson, ReceiptsDTO.class);
    }

    private Receipts createReceiptEntity(ReceiptsDTO receiptsDTO, MultipartFile file) throws IOException {
        Receipts receipts = new Receipts();
        receipts.setInvoiceId(receiptsDTO.getInvoiceId());
        receipts.setReceiptCode(receiptsDTO.getReceiptCode());
        receipts.setDescription(receiptsDTO.getDescription());
        receipts.setUpdateDate(receiptsDTO.getUpdateDate());
        receipts.setClientId(receiptsDTO.getClientId());
        receipts.setPaymentStatus(receiptsDTO.getPaymentStatus());
        receipts.setCustomerName(receiptsDTO.getCustomerName());
        if(receiptsDTO.getPaymentStatus().equals(PaymentStatus.cheque)){
            receipts.setChequeNumber(receiptsDTO.getChequeNumber());
        }
        if (file != null && !file.isEmpty()) {
            byte[] fileBytes = file.getBytes();
            receipts.setFile(file.getBytes());
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                receipts.setFileName(originalFilename.trim());
            }
        }

        receipts.setTotalPaidAmount(receiptsDTO.getTotalPaidAmount());
        return receipts;
    }
    @Override
    public void deleteReceipt(Long receiptId) {
        Optional<Receipts> receipt = receiptsRepository.findById(receiptId);

        if (receipt.isPresent()) {
            receiptsRepository.deleteById(receiptId);
        } else {
            throw new RuntimeException("Receipt not found with id: " + receiptId);
        }
    }
}
