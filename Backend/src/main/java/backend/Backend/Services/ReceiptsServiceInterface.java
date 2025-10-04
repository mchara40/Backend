package backend.Backend.Services;

import backend.Backend.DTOS.ProposalDTO;
import backend.Backend.DTOS.ReceiptsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReceiptsServiceInterface {
    List<ReceiptsDTO> getAllReceipts();

    ReceiptsDTO createReceipt(String receiptJson, MultipartFile file) throws IOException;
    String generateNextReceiptCode();
    byte[] getReceiptFile(Long receiptId);
    String getReceiptlFileName(Long receiptId);
    List<ReceiptsDTO> getAllReceiptsByInvoiceId(Long invoiceId);
    void deleteReceipt(Long receiptId);
}
