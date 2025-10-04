package backend.Backend.Controllers;


import backend.Backend.DTOS.ReceiptsDTO;
import backend.Backend.Services.ReceiptsServiceInterface;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/receipts")
public class ReceiptsController {

    private ReceiptsServiceInterface receiptsServiceInterface;

    @GetMapping("/findAll")
    public ResponseEntity<List<ReceiptsDTO>> getAllReceipts(){
        List<ReceiptsDTO> allReceipts= receiptsServiceInterface.getAllReceipts();
        return ResponseEntity.ok(allReceipts);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReceiptsDTO createReceipt(
            @RequestPart("receipt") String receiptJson,
            @RequestPart("file") MultipartFile file) throws IOException {
        return receiptsServiceInterface.createReceipt(receiptJson, file);
    }
    @GetMapping("findAll/{invoiceId}")
    public ResponseEntity<List<ReceiptsDTO>> getAllReceiptsByInvoiceId(@PathVariable Long invoiceId){
        List<ReceiptsDTO> allReceipts= receiptsServiceInterface.getAllReceiptsByInvoiceId(invoiceId);
        return ResponseEntity.ok(allReceipts);
    }
    @GetMapping("/generateNextReceiptCode")
    public String generateNextReceiptCode(){
        return receiptsServiceInterface.generateNextReceiptCode();
    }

    @GetMapping("/download/{receiptId}")
    public ResponseEntity<byte[]> downloadReceiptFile(@PathVariable Long receiptId) {
        byte[] fileContent = receiptsServiceInterface.getReceiptFile(receiptId);
        String fileName = receiptsServiceInterface.getReceiptlFileName(receiptId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Ensure proper filename encoding for different browsers
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            fileName = "receipt.pdf"; // fallback name
        }

        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }
    @RequestMapping(value = "/delete/{receiptId}", method = DELETE)
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long receiptId) {
        receiptsServiceInterface.deleteReceipt(receiptId);
        return ResponseEntity.ok().build();
    }
}
