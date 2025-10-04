package backend.Backend.Mappers;


import backend.Backend.DTOS.ProductsCategoriesDTO;
import backend.Backend.Entities.ProductsCategories;

public class ProductsCategoriesMapper {
    public static ProductsCategoriesDTO mapToDTO(ProductsCategories productsCategories){
        return new ProductsCategoriesDTO(
                productsCategories.getId(),
                productsCategories.getName(),
                productsCategories.getDescription()

        );
    }
    public static ProductsCategories mapToEntity(ProductsCategoriesDTO productsCategoriesDTO){
        return new ProductsCategories(
                productsCategoriesDTO.getId(),
                productsCategoriesDTO.getName(),
                productsCategoriesDTO.getDescription()
        );
    }
}
