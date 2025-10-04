package backend.Backend.DTOS;
import backend.Backend.Entities.PriceStatus;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductsDTO {
    private Long id;
    private Long categoryId;
    private String code;
    private String name;
    private String description;
    private PriceStatus priceStatus;
    private BigDecimal price;
    private String categoryName;
}

