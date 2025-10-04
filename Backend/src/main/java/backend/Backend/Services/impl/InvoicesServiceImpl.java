package backend.Backend.Services.impl;
import backend.Backend.DTOS.*;
import backend.Backend.Entities.*;
import backend.Backend.Repositories.*;
import backend.Backend.Services.InvoicesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InvoicesServiceImpl implements InvoicesService {

    private ProductsRepository productsRepository;
    private InvoicesRepository invoicesRepository;
    private ReceiptsRepository receiptsRepository;
    private InvoiceProductRepository invoiceProductRepository;
    private InvoiceProductPaymentRepository invoiceProductPaymentRepository;
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional(readOnly = true)
    public List<ProductPaymentHistoryDTO> fetchPreviousPaymentsForProposal(Long proposalId) {
        try {
            // Find all invoices related to this proposal
            List<Invoices> relatedInvoices = invoicesRepository.findByProposalId(proposalId);

            if (relatedInvoices.isEmpty()) {
                return new ArrayList<>(); // No previous invoices found
            }

            // Map to store cumulative payment data per product
            Map<Long, ProductPaymentHistoryDTO> productPaymentMap = new HashMap<>();

            // For each invoice, collect the payment data for each product
            for (Invoices invoice : relatedInvoices) {
                List<InvoiceProduct> invoiceProducts = invoiceProductRepository.findAllByInvoiceId(invoice.getId());

                for (InvoiceProduct product : invoiceProducts) {
                    // Get payments for this product
                    List<InvoiceProductPayment> payments = invoiceProductPaymentRepository
                            .findByInvoiceProductId(product.getInvoiceProductId());

                    // Get or create payment history record for this product
                    ProductPaymentHistoryDTO historyDTO = productPaymentMap.getOrDefault(
                            product.getProductId(),
                            new ProductPaymentHistoryDTO(
                                    product.getProductId(),
                                    product.getProducts().getName(),
                                    BigDecimal.ZERO,  // Start with 0 for total paid
                                    product.getTotalProductPrice(),  // Full product price
                                    new ArrayList<>()  // Empty payment history
                            )
                    );

                    // Add each payment to the history and update the total
                    BigDecimal invoiceProductTotal = BigDecimal.ZERO;
                    for (InvoiceProductPayment payment : payments) {
                        // Create payment record
                        PaymentRecordDTO paymentRecord = new PaymentRecordDTO(
                                payment.getPaymentId(),
                                invoice.getId(),
                                invoice.getCode(),
                                payment.getPaymentAmount(),
                                payment.getPaymentDate().toString()
                        );

                        historyDTO.getPaymentHistory().add(paymentRecord);
                        invoiceProductTotal = invoiceProductTotal.add(payment.getPaymentAmount());
                    }

                    // Update total paid amount
                    BigDecimal currentTotal = historyDTO.getTotalPaid();
                    historyDTO.setTotalPaid(currentTotal.add(invoiceProductTotal));

                    // Calculate remaining balance
                    BigDecimal productPrice = historyDTO.getTotalPrice();
                    BigDecimal totalPaid = historyDTO.getTotalPaid();
                    BigDecimal remaining = productPrice.subtract(totalPaid);
                    historyDTO.setRemainingBalance(remaining);

                    // Calculate payment percentage
                    if (productPrice.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal percentage = totalPaid.multiply(new BigDecimal(100))
                                .divide(productPrice, 2, RoundingMode.HALF_UP);
                        historyDTO.setPaymentPercentage(percentage);
                    }

                    // Update the map
                    productPaymentMap.put(product.getProductId(), historyDTO);
                }
            }

            // Convert the map to a list and return
            return new ArrayList<>(productPaymentMap.values());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching previous payments: " + e.getMessage(), e);
        }
    }



    @Override
    public Invoices updateInvoiceStatus() {
        List<Invoices> allInvoices = invoicesRepository.findAll();

        // By default, set all invoices to unpaid
        for (Invoices invoice : allInvoices) {
            if(invoice.getInvoiceStatus()!=InvoiceStatus.voided) {
                invoice.setInvoiceStatus(InvoiceStatus.unPaid);
            }
        }

        // Save all invoices as unpaid initially
        invoicesRepository.saveAll(allInvoices);
        List<Receipts> receipts = receiptsRepository.findAll();
        Map<Long, BigDecimal> invoiceTotalPaidMap = new HashMap<>();

        // Calculate total paid amount for each invoice from receipts
        for (Receipts receipt : receipts) {
            Long invoiceId = receipt.getInvoiceId();
            BigDecimal currentTotal = invoiceTotalPaidMap.getOrDefault(invoiceId, BigDecimal.ZERO);
            invoiceTotalPaidMap.put(invoiceId, currentTotal.add(receipt.getTotalPaidAmount()));
        }

        // Check each invoice and update status if needed
        List<Invoices> updatedInvoices = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : invoiceTotalPaidMap.entrySet()) {
            Long invoiceId = entry.getKey();
            BigDecimal totalPaidAmount = entry.getValue();

            Optional<Invoices> invoiceOpt = invoicesRepository.findById(invoiceId);
            if (invoiceOpt.isPresent()) {
                Invoices invoice = invoiceOpt.get();

                // Check if total paid amount equals total paid VAT
                if (totalPaidAmount.compareTo(invoice.getTotalPaidVAT()) == 0) {
                    invoice.setInvoiceStatus(InvoiceStatus.paid);
                    Invoices savedInvoice = invoicesRepository.save(invoice);
                    updatedInvoices.add(savedInvoice);
                }
            }
        }

        // Return the last updated invoice or null if none updated
        return updatedInvoices.isEmpty() ? null : updatedInvoices.get(updatedInvoices.size() - 1);
    }

    @Override
    public Invoices updateInvoiceStatusToVoid(Long invoiceId) {
        Invoices invoice = invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Set the invoice status to void
        invoice.setInvoiceStatus(InvoiceStatus.voided);
        return invoicesRepository.save(invoice);
    }


    @Override
    public List<InvoicesDTO> getInvoicesByClientId(Long clientId) {
        return invoicesRepository.findAllByClientId(clientId).stream()
                .map(invoice -> {
                    List<InvoiceProductDTO> products = invoiceProductRepository.findAllByInvoices(invoice)
                            .stream()
                            .map(ip -> new InvoiceProductDTO(
                                    ip.getInvoiceProductId(),
                                    ip.getProducts().getName(),
                                    ip.getProducts().getProductsCategories().getName(),
                                    ip.getProducts().getId(),
                                    ip.getInvoices().getId(),
                                    ip.getTotalProductPrice(),
                                    ip.getDiscountProduct(),
                                    ip.getQuantity(),
                                    ip.getProductDescription(),
                                    ip.getPaidAmount(),
                                    ip.getRemainingBalance()
                            ))
                            .collect(Collectors.toList());

                    return new InvoicesDTO(
                            invoice.getId(),
                            invoice.getCustomerName(),
                            invoice.getCode(),
                            invoice.getDescription(),
                            invoice.getTotalAmount(),
                            products,
                            invoice.getFileName(),
                            invoice.getFile(),
                            invoice.getDiscountInvoiceAmount(),
                            invoice.getDiscountInvoiceText(),
                            invoice.getDiscountStatus(),
                            invoice.getInvoiceType(),
                            invoice.getUpdateDate(),
                            invoice.getProposalId(),
                            invoice.getAddress(),
                            invoice.getPhone(),
                            invoice.getTotalPaid(),
                            invoice.getTotalPaidVAT(),
                            invoice.getRemainingBalance(),
                            invoice.getInvoiceStatus(),
                            invoice.getClientId()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoicesDTO> getNotVoidedInvoicesByClientId(Long clientId) {
        return invoicesRepository.findAllByClientIdAndInvoiceStatusNot(clientId, InvoiceStatus.voided)
                .stream()
                .map(invoice -> {
                    List<InvoiceProductDTO> products = invoiceProductRepository.findAllByInvoices(invoice)
                            .stream()
                            .map(ip -> new InvoiceProductDTO(
                                    ip.getInvoiceProductId(),
                                    ip.getProducts().getName(),
                                    ip.getProducts().getProductsCategories().getName(),
                                    ip.getProducts().getId(),
                                    ip.getInvoices().getId(),
                                    ip.getTotalProductPrice(),
                                    ip.getDiscountProduct(),
                                    ip.getQuantity(),
                                    ip.getProductDescription(),
                                    ip.getPaidAmount(),
                                    ip.getRemainingBalance()
                            ))
                            .collect(Collectors.toList());

                    return new InvoicesDTO(
                            invoice.getId(),
                            invoice.getCustomerName(),
                            invoice.getCode(),
                            invoice.getDescription(),
                            invoice.getTotalAmount(),
                            products,
                            invoice.getFileName(),
                            invoice.getFile(),
                            invoice.getDiscountInvoiceAmount(),
                            invoice.getDiscountInvoiceText(),
                            invoice.getDiscountStatus(),
                            invoice.getInvoiceType(),
                            invoice.getUpdateDate(),
                            invoice.getProposalId(),
                            invoice.getAddress(),
                            invoice.getPhone(),
                            invoice.getTotalPaid(),
                            invoice.getTotalPaidVAT(),
                            invoice.getRemainingBalance(),
                            invoice.getInvoiceStatus(),
                            invoice.getClientId()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoicesDTO> getNotVoidedAllInvoices() {
        return invoicesRepository.findAllByInvoiceStatusNot(InvoiceStatus.voided)
                .stream()
                .map(invoice -> {
                    List<InvoiceProductDTO> products = invoiceProductRepository.findAllByInvoices(invoice)
                            .stream()
                            .map(ip -> new InvoiceProductDTO(
                                    ip.getInvoiceProductId(),
                                    ip.getProducts().getName(),
                                    ip.getProducts().getProductsCategories().getName(),
                                    ip.getProducts().getId(),
                                    ip.getInvoices().getId(),
                                    ip.getTotalProductPrice(),
                                    ip.getDiscountProduct(),
                                    ip.getQuantity(),
                                    ip.getProductDescription(),
                                    ip.getPaidAmount(),
                                    ip.getRemainingBalance()
                            ))
                            .collect(Collectors.toList());

                    return new InvoicesDTO(
                            invoice.getId(),
                            invoice.getCustomerName(),
                            invoice.getCode(),
                            invoice.getDescription(),
                            invoice.getTotalAmount(),
                            products,
                            invoice.getFileName(),
                            invoice.getFile(),
                            invoice.getDiscountInvoiceAmount(),
                            invoice.getDiscountInvoiceText(),
                            invoice.getDiscountStatus(),
                            invoice.getInvoiceType(),
                            invoice.getUpdateDate(),
                            invoice.getProposalId(),
                            invoice.getAddress(),
                            invoice.getPhone(),
                            invoice.getTotalPaid(),
                            invoice.getTotalPaidVAT(),
                            invoice.getRemainingBalance(),
                            invoice.getInvoiceStatus(),
                            invoice.getClientId()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoicesDTO> getAllInvoices() {
        return invoicesRepository.findAll()
                .stream()
                .map(invoice -> {
                    List<InvoiceProductDTO> products = invoiceProductRepository.findAllByInvoices(invoice)
                            .stream()
                            .map(ip -> new InvoiceProductDTO(
                                    ip.getInvoiceProductId(),
                                    ip.getProducts().getName(),
                                    ip.getProducts().getProductsCategories().getName(),
                                    ip.getProducts().getId(),
                                    ip.getInvoices().getId(),
                                    ip.getTotalProductPrice(),
                                    ip.getDiscountProduct(),
                                    ip.getQuantity(),
                                    ip.getProductDescription(),
                                    ip.getPaidAmount(),
                                    ip.getRemainingBalance()
                            ))
                            .collect(Collectors.toList());

                    return new InvoicesDTO(
                            invoice.getId(),
                            invoice.getCustomerName(),
                            invoice.getCode(),
                            invoice.getDescription(),
                            invoice.getTotalAmount(),
                            products,
                            invoice.getFileName(),
                            invoice.getFile(),
                            invoice.getDiscountInvoiceAmount(),
                            invoice.getDiscountInvoiceText(),
                            invoice.getDiscountStatus(),
                            invoice.getInvoiceType(),
                            invoice.getUpdateDate(),
                            invoice.getProposalId(),
                            invoice.getAddress(),
                            invoice.getPhone(),
                            invoice.getTotalPaid(),
                            invoice.getTotalPaidVAT(),
                            invoice.getRemainingBalance(),
                            invoice.getInvoiceStatus(),
                            invoice.getClientId()


                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public String generateNextInvoiceCode() {
        String lastCode = invoicesRepository.findTopByOrderByCodeDesc()
                .map(Invoices::getCode)
                .orElse("N000");

        // Extract the numeric part
        int numberPart = Integer.parseInt(lastCode.substring(1));

        // Increment and format with leading zeros
        numberPart++;
        return String.format("N%03d", numberPart);
    }

    @Override
    @Transactional
    public InvoicesDTO createInvoice(String invoiceJson, MultipartFile file) throws IOException {
        // Parse the invoice JSON
        InvoicesDTO invoicesDTO = parseInvoicelJson(invoiceJson);

        // Create and save the invoice entity
        Invoices invoice = createInvoiceEntity(invoicesDTO, file);
        Invoices savedInvoice = invoicesRepository.save(invoice);

        // Create a map to track the frontend array index to the saved invoice product ID
        Map<Integer, Long> indexToInvoiceProductIdMap = new HashMap<>();
        List<InvoiceProductDTO> savedProducts = new ArrayList<>();

        // Process each product and save it
        for (int index = 0; index < invoicesDTO.getProducts().size(); index++) {
            InvoiceProductDTO productDTO = invoicesDTO.getProducts().get(index);
            Products product = findProductById(productDTO.getProductId());

            // Create and save the invoice product
            InvoiceProduct invoiceProduct = new InvoiceProduct();
            invoiceProduct.setInvoiceId(savedInvoice.getId());
            invoiceProduct.setProductId(product.getId());
            invoiceProduct.setQuantity(productDTO.getQuantity());
            invoiceProduct.setTotalProductPrice(productDTO.getTotalProductPrice());
            invoiceProduct.setDiscountProduct(productDTO.getDiscount());

            // Set payment amounts if they exist
            if (productDTO.getPaidAmount() != null) {
                invoiceProduct.setPaidAmount(productDTO.getPaidAmount());
            } else {
                invoiceProduct.setPaidAmount(BigDecimal.ZERO);
            }

            if (productDTO.getRemainingBalance() != null) {
                invoiceProduct.setRemainingBalance(productDTO.getRemainingBalance());
            } else {
                invoiceProduct.setRemainingBalance(productDTO.getTotalProductPrice());
            }

            // Set product description if available
            if (productDTO.getProductDescription() != null) {
                String description = new String(
                        productDTO.getProductDescription().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );
                invoiceProduct.setProductDescription(description);
            }

            // Save the invoice product
            InvoiceProduct savedInvoiceProduct = invoiceProductRepository.save(invoiceProduct);

            // Store the mapping between array index and saved invoice product ID
            // This is the key change - we map by array index, not by product ID
            indexToInvoiceProductIdMap.put(index, savedInvoiceProduct.getInvoiceProductId());

            // Create saved product DTO
            InvoiceProductDTO savedProductDTO = new InvoiceProductDTO(
                    savedInvoiceProduct.getInvoiceProductId(),
                    product.getName(),
                    product.getProductsCategories().getName(),
                    product.getId(),
                    savedInvoice.getId(),
                    savedInvoiceProduct.getTotalProductPrice(),
                    savedInvoiceProduct.getDiscountProduct(),
                    savedInvoiceProduct.getQuantity(),
                    savedInvoiceProduct.getProductDescription(),
                    savedInvoiceProduct.getPaidAmount(),
                    savedInvoiceProduct.getRemainingBalance()
            );

            savedProducts.add(savedProductDTO);
        }

        // Now handle payments if any exist
        List<InvoiceProductPaymentDTO> allPayments = new ArrayList<>();

        if (invoicesDTO.getPayments() != null && !invoicesDTO.getPayments().isEmpty()) {
            for (InvoiceProductPaymentDTO paymentDTO : invoicesDTO.getPayments()) {
                try {
                    // Get the temporary ID which is a negative index (1-based)
                    Long tempId = paymentDTO.getInvoiceProductId();
                    if (tempId == null) {
                        System.err.println("Payment has null ID: " + paymentDTO);
                        continue;
                    }

                    // If it's a negative ID, it's from the frontend
                    if (tempId < 0) {
                        // Convert to zero-based index
                        int index = Math.abs(tempId.intValue()) - 1;

                        // Check if index is valid
                        if (index < 0 || index >= invoicesDTO.getProducts().size()) {
                            System.err.println("Invalid product index for payment: " + index);
                            continue;
                        }

                        // Get the saved invoice product ID from our mapping
                        Long invoiceProductId = indexToInvoiceProductIdMap.get(index);
                        if (invoiceProductId == null) {
                            System.err.println("No invoice product found for index: " + index);
                            continue;
                        }

                        // Create and save the payment with the correct invoice product ID
                        InvoiceProductPayment payment = new InvoiceProductPayment();
                        payment.setInvoiceProductId(invoiceProductId);
                        payment.setPaymentAmount(paymentDTO.getPaymentAmount());

                        // Set payment date
                        if (paymentDTO.getPaymentDate() != null && !paymentDTO.getPaymentDate().isEmpty()) {
                            try {
                                payment.setPaymentDate(LocalDateTime.parse(
                                        paymentDTO.getPaymentDate(),
                                        DateTimeFormatter.ISO_DATE_TIME
                                ));
                            } catch (Exception e) {
                                payment.setPaymentDate(LocalDateTime.now());
                            }
                        } else {
                            payment.setPaymentDate(LocalDateTime.now());
                        }

                        // Save the payment
                        InvoiceProductPayment savedPayment = invoiceProductPaymentRepository.save(payment);

                        // Add to our collection
                        allPayments.add(new InvoiceProductPaymentDTO(
                                savedPayment.getPaymentId(),
                                savedPayment.getInvoiceProductId(),
                                savedPayment.getPaymentAmount(),
                                savedPayment.getPaymentDate().toString()
                        ));
                    }
                } catch (Exception e) {
                    System.err.println("Error processing payment: " + e.getMessage());
                }
            }
        }

        // Update the invoice's totalPaid and remainingBalance
        updateInvoicePaymentTotals(savedInvoice.getId());

        // Create the result DTO
        return new InvoicesDTO(
                savedInvoice.getId(),
                savedInvoice.getCustomerName(),
                savedInvoice.getCode(),
                savedInvoice.getDescription(),
                savedInvoice.getTotalAmount(),
                savedProducts,
                savedInvoice.getFileName(),
                savedInvoice.getFile(),
                savedInvoice.getDiscountInvoiceAmount(),
                savedInvoice.getDiscountInvoiceText(),
                savedInvoice.getDiscountStatus(),
                savedInvoice.getInvoiceType(),
                savedInvoice.getUpdateDate(),
                savedInvoice.getProposalId(),
                savedInvoice.getAddress(),
                savedInvoice.getPhone(),
                savedInvoice.getTotalPaid(),
                savedInvoice.getTotalPaidVAT(),
                savedInvoice.getRemainingBalance(),
                allPayments,
                savedInvoice.getInvoiceStatus(),
                savedInvoice.getClientId()
        );
    }

    private Invoices createInvoiceEntity(InvoicesDTO invoicesDTO, MultipartFile file) {
        Invoices invoice = new Invoices();
        invoice.setCustomerName(invoicesDTO.getCustomerName());
        invoice.setCode(invoicesDTO.getCode());
        invoice.setDescription(invoicesDTO.getDescription());
        invoice.setTotalAmount(invoicesDTO.getTotalAmount());
        invoice.setDiscountInvoiceAmount(invoicesDTO.getInvoiceDiscountAmount());
        invoice.setDiscountInvoiceText(invoicesDTO.getInvoiceDiscountText());
        invoice.setDiscountStatus(invoicesDTO.getDiscountStatus());
        invoice.setInvoiceType(invoicesDTO.getInvoiceType());
        invoice.setUpdateDate(LocalDateTime.now());
        invoice.setProposalId(invoicesDTO.getProposalId());
        invoice.setAddress(invoicesDTO.getAddress());
        invoice.setPhone(invoicesDTO.getPhone());
        invoice.setTotalPaidVAT(invoicesDTO.getTotalPaidVAT());
        invoice.setInvoiceStatus(InvoiceStatus.unPaid);
        invoice.setClientId(invoicesDTO.getClientId());

        // Set the file if provided
        if (file != null && !file.isEmpty()) {
            try {
                byte[] fileBytes = file.getBytes();
                invoice.setFile(fileBytes);
                String originalFilename = file.getOriginalFilename();
                if (originalFilename != null) {
                    invoice.setFileName(originalFilename.trim());
                }
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + e.getMessage(), e);
            }
        }

        return invoice;
    }

    // Helper method to update invoice payment totals
    private void updateInvoicePaymentTotals(Long invoiceId) {
        Invoices invoice = invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Get all products for this invoice
        List<InvoiceProduct> products = invoiceProductRepository.findAllByInvoiceId(invoiceId);

        // Calculate total paid amount
        BigDecimal totalPaid = BigDecimal.ZERO;
        for (InvoiceProduct product : products) {
            if (product.getPaidAmount() != null) {
                totalPaid = totalPaid.add(product.getPaidAmount());
            }
        }

        // Calculate remaining balance
        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(totalPaid);

        // Update the invoice
        invoice.setTotalPaid(totalPaid);
        invoice.setRemainingBalance(remainingBalance);
        invoicesRepository.save(invoice);
    }
    private InvoicesDTO parseInvoicelJson(String invoiceJson) throws IOException {
        return objectMapper.readValue(invoiceJson, InvoicesDTO.class);
    }

    private Products findProductById(Long productId) {
        return productsRepository.findById(productId).
                orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public InvoicesDTO getInvoiceById(Long id) {
        Invoices invoice = invoicesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        List<InvoiceProductDTO> products = invoiceProductRepository.findAllByInvoices(invoice)
                .stream()
                .map(ip -> new InvoiceProductDTO(
                        ip.getInvoiceProductId(),
                        ip.getProducts().getName(),
                        ip.getProducts().getProductsCategories().getName(),
                        ip.getProducts().getId(),
                        ip.getInvoices().getId(),
                        ip.getTotalProductPrice(),
                        ip.getDiscountProduct(),
                        ip.getQuantity(),
                        ip.getProductDescription(),
                        ip.getPaidAmount(),
                        ip.getRemainingBalance()
                ))
                .collect(Collectors.toList());

        // Get all payments for this invoice's products
        List<InvoiceProductPaymentDTO> allPayments = new ArrayList<>();
        for (InvoiceProductDTO product : products) {
            List<InvoiceProductPaymentDTO> productPayments = invoiceProductPaymentRepository
                    .findByInvoiceProductId(product.getInvoiceProductId())
                    .stream()
                    .map(payment -> new InvoiceProductPaymentDTO(
                            payment.getPaymentId(),
                            payment.getInvoiceProductId(),
                            payment.getPaymentAmount(),
                            payment.getPaymentDate().toString()
                    ))
                    .collect(Collectors.toList());

            allPayments.addAll(productPayments);
        }

        return new InvoicesDTO(
                invoice.getId(),
                invoice.getCustomerName(),
                invoice.getCode(),
                invoice.getDescription(),
                invoice.getTotalAmount(),
                products,
                invoice.getFileName(),
                invoice.getFile(),
                invoice.getDiscountInvoiceAmount(),
                invoice.getDiscountInvoiceText(),
                invoice.getDiscountStatus(),
                invoice.getInvoiceType(),
                invoice.getUpdateDate(),
                invoice.getProposalId(),
                invoice.getAddress(),
                invoice.getPhone(),
                invoice.getTotalPaid(),
                invoice.getTotalPaidVAT(),
                invoice.getRemainingBalance(),
                allPayments,
                invoice.getInvoiceStatus(),
                invoice.getClientId()
        );
    }

    @Override
    @Transactional
    public InvoicesDTO updateInvoice(Long invoiceId, String invoiceJson, MultipartFile file) throws IOException {
        InvoicesDTO invoiceDTO = parseInvoicelJson(invoiceJson);
        Invoices existingInvoice = invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        // Update basic invoice information
        existingInvoice.setCustomerName(invoiceDTO.getCustomerName());
        existingInvoice.setDescription(invoiceDTO.getDescription());
        existingInvoice.setDiscountInvoiceAmount(invoiceDTO.getInvoiceDiscountAmount());
        existingInvoice.setDiscountInvoiceText(invoiceDTO.getInvoiceDiscountText());
        existingInvoice.setDiscountStatus(invoiceDTO.getDiscountStatus());
        existingInvoice.setAddress(invoiceDTO.getAddress());
        existingInvoice.setPhone(invoiceDTO.getPhone());
        existingInvoice.setTotalAmount(invoiceDTO.getTotalAmount());
        existingInvoice.setUpdateDate(LocalDateTime.now());
        existingInvoice.setClientId(invoiceDTO.getClientId());

        // Update payment information
        existingInvoice.setTotalPaid(invoiceDTO.getTotalPaid() != null ?
                invoiceDTO.getTotalPaid() : BigDecimal.ZERO);
        existingInvoice.setTotalPaidVAT(invoiceDTO.getTotalPaidVAT() != null ?
                invoiceDTO.getTotalPaidVAT() : BigDecimal.ZERO);
        existingInvoice.setRemainingBalance(invoiceDTO.getRemainingBalance() != null ?
                invoiceDTO.getRemainingBalance() : invoiceDTO.getTotalAmount());


        // Update file if provided
        if (file != null && !file.isEmpty()) {
            byte[] fileBytes = file.getBytes();
            existingInvoice.setFile(fileBytes);
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                existingInvoice.setFileName(originalFilename.trim());
            }
        }

        // Save the updated invoice
        Invoices savedInvoice = invoicesRepository.save(existingInvoice);

        // Delete all existing products and recreate them
        invoiceProductRepository.deleteAllByInvoiceById(invoiceId);

        // Process each product
        List<InvoiceProductDTO> savedProducts = new ArrayList<>();

        for (InvoiceProductDTO productDTO : invoiceDTO.getProducts()) {
            // Find the product
            Products product = productsRepository.findById(productDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productDTO.getProductId()));

            // Create invoice product
            InvoiceProduct invoiceProduct = new InvoiceProduct();
            invoiceProduct.setInvoiceId(savedInvoice.getId());
            invoiceProduct.setProductId(product.getId());
            invoiceProduct.setQuantity(productDTO.getQuantity());
            invoiceProduct.setTotalProductPrice(productDTO.getTotalProductPrice());
            invoiceProduct.setDiscountProduct(productDTO.getDiscount());
            invoiceProduct.setPaidAmount(productDTO.getPaidAmount() != null ?
                    productDTO.getPaidAmount() : BigDecimal.ZERO);
            invoiceProduct.setRemainingBalance(productDTO.getRemainingBalance() != null ?
                    productDTO.getRemainingBalance() : productDTO.getTotalProductPrice());

            // Set product description if available
            if (productDTO.getProductDescription() != null) {
                String description = new String(
                        productDTO.getProductDescription().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );
                invoiceProduct.setProductDescription(description);
            }

            // Save the invoice product
            InvoiceProduct savedInvoiceProduct = invoiceProductRepository.save(invoiceProduct);

            // Create DTO for the saved product
            InvoiceProductDTO savedProductDTO = new InvoiceProductDTO(
                    savedInvoiceProduct.getInvoiceProductId(),
                    product.getName(),
                    product.getProductsCategories().getName(),
                    product.getId(),
                    savedInvoice.getId(),
                    savedInvoiceProduct.getTotalProductPrice(),
                    savedInvoiceProduct.getDiscountProduct(),
                    savedInvoiceProduct.getQuantity(),
                    savedInvoiceProduct.getProductDescription(),
                    savedInvoiceProduct.getPaidAmount(),
                    savedInvoiceProduct.getRemainingBalance()
            );

            savedProducts.add(savedProductDTO);
        }

        // Get all payments for this invoice
        List<InvoiceProductPaymentDTO> allPayments = new ArrayList<>();
        for (InvoiceProductDTO product : savedProducts) {
            List<InvoiceProductPaymentDTO> productPayments = invoiceProductPaymentRepository
                    .findByInvoiceProductId(product.getInvoiceProductId())
                    .stream()
                    .map(payment -> new InvoiceProductPaymentDTO(
                            payment.getPaymentId(),
                            payment.getInvoiceProductId(),
                            payment.getPaymentAmount(),
                            payment.getPaymentDate().toString()
                    ))
                    .collect(Collectors.toList());

            allPayments.addAll(productPayments);
        }

        // Create the result DTO
        return new InvoicesDTO(
                savedInvoice.getId(),
                savedInvoice.getCustomerName(),
                savedInvoice.getCode(),
                savedInvoice.getDescription(),
                savedInvoice.getTotalAmount(),
                savedProducts,
                savedInvoice.getFileName(),
                savedInvoice.getFile(),
                savedInvoice.getDiscountInvoiceAmount(),
                savedInvoice.getDiscountInvoiceText(),
                savedInvoice.getDiscountStatus(),
                savedInvoice.getInvoiceType(),
                savedInvoice.getUpdateDate(),
                savedInvoice.getProposalId(),
                savedInvoice.getAddress(),
                savedInvoice.getPhone(),
                savedInvoice.getTotalPaid(),
                savedInvoice.getTotalPaidVAT(),
                savedInvoice.getRemainingBalance(),
                allPayments,
                savedInvoice.getInvoiceStatus(),
                savedInvoice.getClientId()
        );
    }

    @Override
    @Transactional
    public void deleteInvoice(Long invoiceId) {
        try {
            // Get the invoice without initializing relationships
            Invoices invoice = invoicesRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            // Use JPQL to find all payment IDs for this invoice's products
            List<Long> paymentIds = entityManager.createQuery(
                            "SELECT p.paymentId FROM InvoiceProductPayment p " +
                                    "JOIN InvoiceProduct ip ON p.invoiceProductId = ip.invoiceProductId " +
                                    "WHERE ip.invoiceId = :invoiceId", Long.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();

            // Delete payments directly using SQL to avoid JPA relationship issues
            if (!paymentIds.isEmpty()) {
                entityManager.createNativeQuery(
                                "DELETE FROM invoice_product_payment WHERE payment_id IN (:paymentIds)")
                        .setParameter("paymentIds", paymentIds)
                        .executeUpdate();
            }

            // Delete receipts associated with the invoice
            entityManager.createNativeQuery(
                            "DELETE FROM receipts WHERE invoice_id = :invoiceId")
                    .setParameter("invoiceId", invoiceId)
                    .executeUpdate();
            // Delete invoice products using native query to avoid cascading issues
            entityManager.createNativeQuery(
                            "DELETE FROM invoice_product WHERE invoice_id = :invoiceId")
                    .setParameter("invoiceId", invoiceId)
                    .executeUpdate();

            // Finally delete the invoice itself
            entityManager.createNativeQuery(
                            "DELETE FROM invoices WHERE id = :invoiceId")
                    .setParameter("invoiceId", invoiceId)
                    .executeUpdate();

            // Clear the persistence context to avoid stale entities
            entityManager.flush();
            entityManager.clear();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getInvoiceFile(Long invoiceId) {
        Invoices invoice = invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getFile() == null) {
            throw new RuntimeException("No file found for invoice with id: " + invoiceId);
        }

        return invoice.getFile();
    }

    @Override
    public String getInvoiceFileName(Long invoiceId) {
        Invoices invoice = invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getFileName() == null) {
            throw new RuntimeException("No file found for invoice with id: " + invoiceId);
        }

        return invoice.getFileName();
    }

    @Override
    @Transactional
    public InvoiceProductPaymentDTO addPaymentToProduct(Long productId, InvoiceProductPaymentDTO paymentDTO) {
        // Find the invoice product
        InvoiceProduct invoiceProduct = invoiceProductRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Invoice product not found"));

        // Create the payment entity
        InvoiceProductPayment payment = new InvoiceProductPayment();
        payment.setInvoiceProductId(productId);
        payment.setPaymentAmount(paymentDTO.getPaymentAmount());

        // Parse date if provided, otherwise use current time
        if (paymentDTO.getPaymentDate() != null && !paymentDTO.getPaymentDate().isEmpty()) {
            try {
                LocalDateTime paymentDate = LocalDateTime.parse(
                        paymentDTO.getPaymentDate(),
                        DateTimeFormatter.ISO_DATE_TIME
                );
                payment.setPaymentDate(paymentDate);
            } catch (Exception e) {
                payment.setPaymentDate(LocalDateTime.now());
            }
        } else {
            payment.setPaymentDate(LocalDateTime.now());
        }

        // Save the payment
        InvoiceProductPayment savedPayment = invoiceProductPaymentRepository.save(payment);

        // Update the invoice product's paid amount and remaining balance
        BigDecimal currentPaidAmount = invoiceProduct.getPaidAmount() != null ?
                invoiceProduct.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidAmount = currentPaidAmount.add(paymentDTO.getPaymentAmount());

        // Ensure paid amount doesn't exceed total price
        if (newPaidAmount.compareTo(invoiceProduct.getTotalProductPrice()) > 0) {
            newPaidAmount = invoiceProduct.getTotalProductPrice();
        }

        BigDecimal newRemainingBalance = invoiceProduct.getTotalProductPrice().subtract(newPaidAmount);

        invoiceProduct.setPaidAmount(newPaidAmount);
        invoiceProduct.setRemainingBalance(newRemainingBalance);
        invoiceProductRepository.save(invoiceProduct);

        // Update the parent invoice's total paid and remaining balance
        Invoices invoice = invoiceProduct.getInvoices();

        // Calculate the new total paid for the entire invoice
        BigDecimal totalPaid = invoiceProductRepository.findAllByInvoices(invoice)
                .stream()
                .map(ip -> ip.getPaidAmount() != null ? ip.getPaidAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(totalPaid);

        invoice.setTotalPaid(totalPaid);
        invoice.setRemainingBalance(remainingBalance);
        invoicesRepository.save(invoice);

        // Return the saved payment DTO
        return new InvoiceProductPaymentDTO(
                savedPayment.getPaymentId(),
                savedPayment.getInvoiceProductId(),
                savedPayment.getPaymentAmount(),
                savedPayment.getPaymentDate().toString()
        );
    }

    @Override
    public List<InvoiceProductPaymentDTO> getPaymentsForProduct(Long productId) {
        // Check if the product exists
        if (!invoiceProductRepository.existsById(productId)) {
            throw new RuntimeException("Invoice product not found");
        }

        // Get all payments for the product
        return invoiceProductPaymentRepository.findByInvoiceProductId(productId)
                .stream()
                .map(payment -> new InvoiceProductPaymentDTO(
                        payment.getPaymentId(),
                        payment.getInvoiceProductId(),
                        payment.getPaymentAmount(),
                        payment.getPaymentDate().toString()
                ))
                .collect(Collectors.toList());
    }
}