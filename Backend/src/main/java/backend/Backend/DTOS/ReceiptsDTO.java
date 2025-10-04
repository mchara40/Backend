package backend.Backend.DTOS;


import backend.Backend.Entities.DiscountStatus;
import backend.Backend.Entities.InvoiceStatus;
import backend.Backend.Entities.InvoiceType;
import backend.Backend.Entities.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReceiptsDTO {
    private Long id;
    private Long invoiceId;
    private String receiptCode;
    private String description;
    private LocalDateTime updateDate;
    private String fileName;
    private byte[] file;
    private BigDecimal totalPaidAmount;
    private Long clientId; // New field for client ID
    private PaymentStatus paymentStatus; // New field for payment status
    private String chequeNumber; // New field for cheque number
    private String customerName; // New field for customer name


    @JsonCreator
    public ReceiptsDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("invoiceId") Long invoiceId,
            @JsonProperty("receiptCode") String receiptCode,
            @JsonProperty("description") String description,
            @JsonProperty("updateDate") LocalDateTime updateDate,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("file") byte[] file,
            @JsonProperty("totalPaidAmount") BigDecimal totalPaidAmount,
            @JsonProperty("clientId") Long clientId,
            @JsonProperty("paymentStatus") PaymentStatus paymentStatus,
            @JsonProperty("chequeNumber") String chequeNumber,
            @JsonProperty("customerName") String customerName

    ) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.receiptCode = receiptCode;
        this.description = description;
        this.updateDate = updateDate;
        this.fileName = fileName;
        this.file = file;
        this.totalPaidAmount = totalPaidAmount;
        this.clientId = clientId;
        this.paymentStatus = paymentStatus;
        this.chequeNumber = chequeNumber;
        this.customerName = customerName;

    }
}