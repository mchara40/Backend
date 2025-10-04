package backend.Backend.Services;
import backend.Backend.DTOS.InvoiceProductPaymentDTO;
import backend.Backend.DTOS.InvoicesDTO;
import backend.Backend.DTOS.ProductPaymentHistoryDTO;
import backend.Backend.DTOS.ProposalDTO;
import backend.Backend.Entities.InvoiceStatus;
import backend.Backend.Entities.Invoices;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InvoicesService {
    /**
     * Get all invoices
     * @return List of all invoices
     */
    List<InvoicesDTO> getAllInvoices();

    /**
     * Create a new invoice
     * @param invoiceJson Invoice data as JSON string
     * @param file Optional invoice file attachment
     * @return Created invoice DTO
     * @throws IOException If file handling fails
     */
    InvoicesDTO createInvoice(String invoiceJson, MultipartFile file) throws IOException;

    /**
     * Get invoice by ID
     * @param id Invoice ID
     * @return Invoice DTO
     */
    InvoicesDTO getInvoiceById(Long id);

    /**
     * Update an existing invoice
     * @param invoiceId Invoice ID to update
     * @param invoiceJson Updated invoice data as JSON string
     * @param file Optional updated invoice file attachment
     * @return Updated invoice DTO
     * @throws IOException If file handling fails
     */
    InvoicesDTO updateInvoice(Long invoiceId, String invoiceJson, MultipartFile file) throws IOException;

    /**
     * Delete an invoice
     * @param invoiceId Invoice ID to delete
     */
    void deleteInvoice(Long invoiceId);

    /**
     * Get invoice file content
     * @param invoiceId Invoice ID
     * @return File content as byte array
     */
    byte[] getInvoiceFile(Long invoiceId);

    /**
     * Get invoice file name
     * @param invoiceId Invoice ID
     * @return File name
     */
    String getInvoiceFileName(Long invoiceId);

    /**
     * Generate next available invoice code
     * @return Generated invoice code
     */
    String generateNextInvoiceCode();

    /**
     * Add a payment to an invoice product
     * @param productId Invoice product ID
     * @param paymentDTO Payment data
     * @return Created payment DTO
     */
    InvoiceProductPaymentDTO addPaymentToProduct(Long productId, InvoiceProductPaymentDTO paymentDTO);

    /**
     * Get all payments for a specific invoice product
     * @param productId Invoice product ID
     * @return List of payments
     */
    List<InvoiceProductPaymentDTO> getPaymentsForProduct(Long productId);

    List<ProductPaymentHistoryDTO> fetchPreviousPaymentsForProposal(Long proposalId);
    Invoices updateInvoiceStatus();
    Invoices updateInvoiceStatusToVoid(Long invoiceId);
    List<InvoicesDTO> getInvoicesByClientId(Long clientId);
    List<InvoicesDTO> getNotVoidedInvoicesByClientId(Long clientId);
    List<InvoicesDTO> getNotVoidedAllInvoices();

}