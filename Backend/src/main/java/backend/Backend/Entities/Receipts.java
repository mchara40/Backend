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
@Table(name = "receipts")
public class Receipts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="invoice_id",length=10, nullable=false)
    private Long invoiceId;

    @Column(name = "receipt_code")
    private String receiptCode;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;


    @Column(name = "description")
    private String description;

    @Column(name = "total_paid_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalPaidAmount;

    @Column(name = "file", nullable = true)
    private byte[] file; // Store the file content as binary data

    @Column(name = "file_name", nullable = true)
    private String fileName; // Store the original file name

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Invoices invoices;

    @Column(name="client_id", nullable = false)
    private Long clientId;

    @Column(name="payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "cheque_number")
    private String chequeNumber;

    @Column(name = "customer_name")
    private String customerName;
}
