package backend.Backend.DTOS;
import backend.Backend.Entities.DiscountStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class ProposalDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal totalAmount;
    private List<ProposalProductDTO> products;
    private String fileName;
    private byte[] file;
    private BigDecimal proposalDiscountAmount;
    private Integer proposalDiscountText;
    private DiscountStatus discountStatus;
    private LocalDateTime updateDate;
    private long clientId;
    private BigDecimal totalAmountWithoutDiscount;

    @JsonCreator
    public ProposalDTO(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("totalAmount") BigDecimal totalAmount,
            @JsonProperty("products") List<ProposalProductDTO> products,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("file") byte[] file,
            @JsonProperty("proposalDiscountAmount") BigDecimal proposalDiscountAmount,
            @JsonProperty("proposalDiscountText") Integer proposalDiscountText,
            @JsonProperty("discountStatus") DiscountStatus discountStatus,
            @JsonProperty("updateDate") LocalDateTime updateDate,
            @JsonProperty("clientId") long clientId,
            @JsonProperty("totalAmountWithoutDiscount") BigDecimal totalAmountWithoutDiscount

    ) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.totalAmount = totalAmount;
        this.products = products;
        this.fileName = fileName;
        this.file = file;
        this.proposalDiscountAmount=proposalDiscountAmount;
        this.proposalDiscountText=proposalDiscountText;
        this.discountStatus=discountStatus;
        this.updateDate=updateDate;
        this.clientId = clientId;
        this.totalAmountWithoutDiscount = totalAmountWithoutDiscount;
    }

}
