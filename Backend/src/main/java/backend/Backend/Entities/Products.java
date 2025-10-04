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
@Table(name = "products")
public class Products {
    public Products(String name, Long categoryId, String code, String description, BigDecimal price, PriceStatus priceStatus) {
        this.name = name;
        this.categoryId = categoryId;
        this.code = code;
        this.description = description;
        this.price = price;
        this.priceStatus = priceStatus;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name")
    private String name;


    @Column(name="category_id",length=10, nullable=false)
    private Long categoryId;


    @Column(name = "code",nullable = false)
    private String code;


    @Column(name = "description",nullable = true)
    private String description;

    @Column(name="price_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceStatus priceStatus;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ProductsCategories productsCategories;
}
