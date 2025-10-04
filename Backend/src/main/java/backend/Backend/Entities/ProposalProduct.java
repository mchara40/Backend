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
@Table(name = "proposal_product")
public class ProposalProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalProductId;

    @Column(name="proposal_id",length=10, nullable=false)
    private Long proposalId;

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

    @Column(name = "custom_product_price", precision = 10, scale = 2)
    private BigDecimal customPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Products products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Proposal proposal;
}