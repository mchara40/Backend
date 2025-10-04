package backend.Backend.DTOS;
import backend.Backend.Entities.DiscountStatus;
import backend.Backend.Entities.InvoiceStatus;
import backend.Backend.Entities.InvoiceType;
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
public class InvoicesDTO {

    private Long id;
    private String customerName;
    private String code;
    private String description;
    private BigDecimal totalAmount;
    private List<InvoiceProductDTO> products;
    private String fileName;
    private byte[] file;
    private BigDecimal invoiceDiscountAmount;
    private Integer invoiceDiscountText;
    private DiscountStatus discountStatus;
    private InvoiceType invoiceType;
    private LocalDateTime updateDate;
    private Long proposalId;
    private String address;
    private String phone;
    private BigDecimal totalPaid;
    private BigDecimal totalPaidVAT;
    private BigDecimal remainingBalance;
    private InvoiceStatus invoiceStatus; // New field for invoice status
    private List<InvoiceProductPaymentDTO> payments; // New field for payments
    private Long clientId; // New field for client ID


    // Original constructor - compatible with existing code
    @JsonCreator
    public InvoicesDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("customerName") String customerName,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("products") List<InvoiceProductDTO> products,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("file") byte[] file,
            @JsonProperty("invoiceDiscountAmount") BigDecimal invoiceDiscountAmount,
            @JsonProperty("invoiceDiscountText") Integer invoiceDiscountText,
            @JsonProperty("discountStatus") DiscountStatus discountStatus,
            @JsonProperty("invoiceType") InvoiceType invoiceType,
            @JsonProperty("updateDate") LocalDateTime updateDate,
            @JsonProperty("proposalId") Long proposalId,
            @JsonProperty("address") String address,
            @JsonProperty("phone") String phone,
            @JsonProperty("totalPaid") BigDecimal totalPaid,
            @JsonProperty("totalPaidVAT") BigDecimal totalPaidVAT,
            @JsonProperty("remainingBalance") BigDecimal remainingBalance,
            @JsonProperty("invoiceStatus") InvoiceStatus invoiceStatus,
            @JsonProperty("clientId") Long clientId
    ) {
        this.id = id;
        this.customerName = customerName;
        this.code = code;
        this.description = description;
        this.totalAmount = totalAmount;
        this.products = products;
        this.fileName = fileName;
        this.file = file;
        this.invoiceDiscountAmount = invoiceDiscountAmount;
        this.invoiceDiscountText = invoiceDiscountText;
        this.discountStatus = discountStatus;
        this.invoiceType = invoiceType;
        this.updateDate = updateDate;
        this.proposalId = proposalId;
        this.address = address;
        this.phone = phone;
        this.totalPaid = totalPaid;
        this.totalPaidVAT = totalPaidVAT;
        this.remainingBalance = remainingBalance;
        this.payments = new ArrayList<>(); // Initialize with empty list
        this.invoiceStatus = invoiceStatus;
        this.clientId = clientId; // Initialize client ID
    }
    public InvoicesDTO(
            InvoiceStatus invoiceStatus
    ){
        this.invoiceStatus = invoiceStatus;
    }
    // New constructor with payments included
    public InvoicesDTO(
            Long id,
            String customerName,
            String code,
            String description,
            BigDecimal totalAmount,
            List<InvoiceProductDTO> products,
            String fileName,
            byte[] file,
            BigDecimal invoiceDiscountAmount,
            Integer invoiceDiscountText,
            DiscountStatus discountStatus,
            InvoiceType invoiceType,
            LocalDateTime updateDate,
            Long proposalId,
            String address,
            String phone,
            BigDecimal totalPaid,
            BigDecimal totalPaidVAT,
            BigDecimal remainingBalance,
            List<InvoiceProductPaymentDTO> payments,
            InvoiceStatus invoiceStatus,
            Long clientId
    ) {
        this.id = id;
        this.customerName = customerName;
        this.code = code;
        this.description = description;
        this.totalAmount = totalAmount;
        this.products = products;
        this.fileName = fileName;
        this.file = file;
        this.invoiceDiscountAmount = invoiceDiscountAmount;
        this.invoiceDiscountText = invoiceDiscountText;
        this.discountStatus = discountStatus;
        this.invoiceType = invoiceType;
        this.updateDate = updateDate;
        this.proposalId = proposalId;
        this.address = address;
        this.phone = phone;
        this.totalPaid = totalPaid;
        this.totalPaidVAT = totalPaidVAT;
        this.remainingBalance = remainingBalance;
        this.payments = payments != null ? payments : new ArrayList<>();
        this.invoiceStatus = invoiceStatus;
        this.clientId = clientId;
    }
}