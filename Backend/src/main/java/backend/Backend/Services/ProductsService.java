package backend.Backend.Services;

import backend.Backend.DTOS.ProductsDTO;
import backend.Backend.DTOS.ProductsResponseDTO;

import java.util.List;

public interface ProductsService {
    ProductsResponseDTO create(ProductsResponseDTO productsDTO);
    List<ProductsDTO> getAllProducts();
    ProductsDTO getProducts(Integer id);
    ProductsDTO updateProducts(Integer id, ProductsDTO productsDTO);
    ProductsDTO deleteProducts(Integer id);
}
