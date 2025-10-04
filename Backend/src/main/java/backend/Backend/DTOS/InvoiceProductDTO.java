package backend.Backend.DTOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceProductDTO {
    private Long invoiceProductId;
    private String productName;
    private String productCategory;
    private Long productId;
    private Long invoiceId;
    private BigDecimal totalProductPrice;
    private BigDecimal discount;
    private Integer quantity;
    private String productDescription;
    private BigDecimal paidAmount;
    private BigDecimal remainingBalance;
}
