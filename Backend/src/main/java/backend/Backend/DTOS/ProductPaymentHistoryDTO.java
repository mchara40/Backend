package backend.Backend.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for tracking payment history of a product across multiple invoices
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPaymentHistoryDTO {
    private Long productId;
    private String productName;
    private BigDecimal totalPaid;
    private BigDecimal totalPrice;
    private BigDecimal remainingBalance;
    private BigDecimal paymentPercentage;
    private List<PaymentRecordDTO> paymentHistory;

    public ProductPaymentHistoryDTO(Long productId, String productName, BigDecimal totalPaid,
                                    BigDecimal totalPrice, List<PaymentRecordDTO> paymentHistory) {
        this.productId = productId;
        this.productName = productName;
        this.totalPaid = totalPaid;
        this.totalPrice = totalPrice;
        this.paymentHistory = paymentHistory;

        // Calculate remaining and percentage
        this.remainingBalance = totalPrice.subtract(totalPaid);
        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            this.paymentPercentage = totalPaid.multiply(new BigDecimal(100))
                    .divide(totalPrice, 2, java.math.RoundingMode.HALF_UP);
        } else {
            this.paymentPercentage = BigDecimal.ZERO;
        }
    }
}

