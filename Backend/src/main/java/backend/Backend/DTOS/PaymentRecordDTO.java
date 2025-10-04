package backend.Backend.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing a single payment in the payment history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordDTO {
    private Long paymentId;
    private Long invoiceId;
    private String invoiceCode;
    private BigDecimal amount;
    private String date;
}
