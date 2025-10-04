package backend.Backend.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proposals")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "total_amount_without_discount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmountWithoutDiscount;

    @Column(name = "file", nullable = true)
    private byte[] file; // Store the file content as binary data

    @Column(name = "file_name", nullable = true)
    private String fileName; // Store the original file name

    @Column(name = "discount_proposal_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountProposalAmount;

    @Column(name = "discount_proposal_text", nullable = false)
    private Integer discountProposalText;

    @Column(name="discount_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountStatus discountStatus;

    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name="client_id", nullable = false)
    private Long clientId;


}