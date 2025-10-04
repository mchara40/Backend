package backend.Backend.DTOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalProductDTO {
    private Long proposalProductId;
    private String productName;
    private String productCategory;
    private Long productId;
    private Long proposalId;
    private BigDecimal totalProductPrice;
    private BigDecimal discount;
    private Integer quantity;
    private String productDescription;
    private BigDecimal customPrice;
}
