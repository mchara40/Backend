package backend.Backend.DTOS;
import backend.Backend.Entities.PriceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductsResponseDTO {
    private String code;
    private String name;
    private String description;
    private PriceStatus priceStatus;
    private BigDecimal price;
    private Long categoryId;
}
