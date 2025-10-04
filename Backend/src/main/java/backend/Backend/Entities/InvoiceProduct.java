package backend.Backend.Entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "invoice_product")
public class InvoiceProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_product_id")
    private Long invoiceProductId;

    @Column(name="invoice_id",length=10, nullable=false)
    private Long invoiceId;

    @Column(name="product_id",length=10, nullable=false)
    private Long  productId;

    @Column(name="quantity",length=10, nullable=false)
    private Integer quantity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "total_product_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalProductPrice;

    @Column(name = "discount_product", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountProduct;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "remaining_balance", precision = 10, scale = 2)
    private BigDecimal remainingBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Products products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Invoices invoices;
}