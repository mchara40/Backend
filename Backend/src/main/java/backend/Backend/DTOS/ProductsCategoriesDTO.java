package backend.Backend.DTOS;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductsCategoriesDTO {
    private Long id;
    private String name;
    private String description;
}
