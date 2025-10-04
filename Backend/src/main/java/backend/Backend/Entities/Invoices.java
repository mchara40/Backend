package backend.Backend.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "code")
    private String code;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "description")
    private String description;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "file", nullable = true)
    private byte[] file; // Store the file content as binary data

    @Column(name = "file_name", nullable = true)
    private String fileName; // Store the original file name

    @Column(name = "discount_invoice_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountInvoiceAmount;

    @Column(name="discount_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountStatus discountStatus;

    @Column(name = "discount_invoice_text", nullable = false)
    private Integer discountInvoiceText;

    @Column(name="invoice_type",nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Column(name="proposal_id",length=10, nullable=true)
    private Long proposalId;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "total_paid", precision = 10, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "total_paid_vat", precision = 10, scale = 2)
    private BigDecimal totalPaidVAT;

    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;

    @Column(name="client_id", nullable = false)
    private Long clientId;
}