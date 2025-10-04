package backend.Backend.Controllers;
import backend.Backend.DTOS.InvoicesDTO;
import backend.Backend.DTOS.ProductPaymentHistoryDTO;
import backend.Backend.Entities.Invoices;
import backend.Backend.Repositories.InvoiceProductRepository;
import backend.Backend.Repositories.InvoicesRepository;
import backend.Backend.Repositories.ProductsRepository;
import backend.Backend.Security.UserPrincipal;
import backend.Backend.Services.InvoicesService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {
    private InvoicesService invoicesService;
    private InvoicesRepository invoicesRepository;
    private ProductsRepository productsRepository;
    private InvoiceProductRepository invoiceProductRepository;

    @GetMapping("/findAll")
    public ResponseEntity<List<InvoicesDTO>> getAllInvoices(){
        List<InvoicesDTO> allInvoices= invoicesService.getAllInvoices();
        return ResponseEntity.ok(allInvoices);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public InvoicesDTO createInvoice(
            @RequestPart("invoice") String invoiceJson,
            @RequestPart("file") MultipartFile file) throws IOException {
        return invoicesService.createInvoice(invoiceJson, file);
    }
    @GetMapping("/proposal/{proposalId}/payment-history")
    public ResponseEntity<List<ProductPaymentHistoryDTO>> getProposalPaymentHistory(@PathVariable Long proposalId) {
        return ResponseEntity.ok(invoicesService.fetchPreviousPaymentsForProposal(proposalId));
    }
    @GetMapping("/{invoiceId}")
    public InvoicesDTO getInvoiceById(@PathVariable Long invoiceId) {
        return invoicesService.getInvoiceById(invoiceId);
    }

    @RequestMapping(value = "/delete/{invoiceId}", method = DELETE)
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long invoiceId) {
        invoicesService.deleteInvoice(invoiceId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{invoiceId}")
    public ResponseEntity<byte[]> downloadInvoiceFile(@PathVariable Long invoiceId) {
        byte[] fileContent = invoicesService.getInvoiceFile(invoiceId);
        String fileName = invoicesService.getInvoiceFileName(invoiceId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Ensure proper filename encoding for different browsers
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            fileName = "invoice.pdf"; // fallback name
        }

        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    @GetMapping("/generateNextInvoiceCode")
    public String generateNextInvoiceCode(){
        return invoicesService.generateNextInvoiceCode();
    }

    @PutMapping("/update-status")
    public ResponseEntity<Invoices> updateInvoiceStatus() {
        Invoices updatedInvoice = invoicesService.updateInvoiceStatus();
        if (updatedInvoice != null) {
            return ResponseEntity.ok(updatedInvoice);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    @PutMapping("/update/{invoiceId}")
    public InvoicesDTO updateInvoice(
            @PathVariable Long invoiceId,
            @RequestPart("invoice") String invoiceJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {
        return invoicesService.updateInvoice(invoiceId, invoiceJson, file);
    }

    @GetMapping("/findAllByClientId/{clientId}")
    public ResponseEntity<List<InvoicesDTO>> getInvoicesByClientId(@PathVariable Long clientId) {
        List<InvoicesDTO> invoices = invoicesService.getInvoicesByClientId(clientId);
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/update-status-void/{invoiceId}")
    public ResponseEntity<Invoices> updateInvoiceStatusToVoid(@PathVariable Long invoiceId) {
        Invoices updatedInvoice = invoicesService.updateInvoiceStatusToVoid(invoiceId);
        if (updatedInvoice != null) {
            return ResponseEntity.ok(updatedInvoice);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/getNotVoidedAllInvoices")
    public ResponseEntity<List<InvoicesDTO>> getNotVoidedAllInvoices() {
        List<InvoicesDTO> invoices = invoicesService.getNotVoidedAllInvoices();
        return ResponseEntity.ok(invoices);
    }
    @GetMapping("/getNotVoidedInvoicesByClientId/{clientId}")
    public ResponseEntity<List<InvoicesDTO>> getNotVoidedInvoicesByClientId(@PathVariable Long clientId) {
        List<InvoicesDTO> invoices = invoicesService.getNotVoidedInvoicesByClientId(clientId);
        return ResponseEntity.ok(invoices);
    }

}

