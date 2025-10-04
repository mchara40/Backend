package backend.Backend.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_product_payment")
public class InvoiceProductPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name="invoice_product_id",length=10, nullable=false)
    private Long invoiceProductId;

    @Column(name = "payment_amount", precision = 10, scale = 2)
    private BigDecimal paymentAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_product_id", referencedColumnName = "invoice_product_id", insertable = false, updatable = false)
    private InvoiceProduct invoiceProduct;
    // Pre-persist hook to ensure payment_amount is always positive
    @PrePersist
    @PreUpdate
    public void validatePayment() {
        // Ensure payment amount is not null
        if (paymentAmount == null) {
            paymentAmount = BigDecimal.ZERO;
        }

        // Ensure payment date is not null
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }

        // Ensure payment amount is not negative
        if (paymentAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative");
        }
    }

}
