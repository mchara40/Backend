package backend.Backend.Services;



import backend.Backend.DTOS.ProductsCategoriesDTO;

import java.util.List;

public interface ProductsCategoriesService {
    ProductsCategoriesDTO create(ProductsCategoriesDTO productsCategoriesDTO);
    List<ProductsCategoriesDTO> getAllProductsCategories();
    ProductsCategoriesDTO getProductsCategory(Integer id);
    ProductsCategoriesDTO updateProductsCategory(Integer id, ProductsCategoriesDTO productsCategoriesDTO);
    ProductsCategoriesDTO deleteProductsCategory(Integer id);
}

