package backend.Backend.Services.impl;
import backend.Backend.DTOS.ProductsCategoriesDTO;
import backend.Backend.Entities.Products;
import backend.Backend.Entities.ProductsCategories;
import backend.Backend.Mappers.ProductsCategoriesMapper;
import backend.Backend.Repositories.ProductsCategoriesRepository;
import backend.Backend.Repositories.ProductsRepository;
import backend.Backend.Services.ProductsCategoriesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductsCategoriesServiceImpl implements ProductsCategoriesService {
    private ProductsCategoriesRepository productsCategoriesRepository;
    private ProductsRepository productsRepository;


    @Override
    public ProductsCategoriesDTO create(ProductsCategoriesDTO productsCategoriesDTO) {
        ProductsCategories productsCategories = ProductsCategoriesMapper.mapToEntity(productsCategoriesDTO);
        ProductsCategories saveproductsCategories= productsCategoriesRepository.save(productsCategories);
        return ProductsCategoriesMapper.mapToDTO(saveproductsCategories);

    }

    @Override
    public List<ProductsCategoriesDTO> getAllProductsCategories() {
        List<ProductsCategories> allProductsCategories = productsCategoriesRepository.findAll();
        return allProductsCategories.stream().map(productsCategories -> ProductsCategoriesMapper.mapToDTO(productsCategories))
                .collect(Collectors.toList());
    }

    @Override
    public ProductsCategoriesDTO getProductsCategory(Integer id) {
        ProductsCategories productsCategories = productsCategoriesRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        return ProductsCategoriesMapper.mapToDTO(productsCategories);
    }

    @Override
    public ProductsCategoriesDTO updateProductsCategory(Integer id, ProductsCategoriesDTO productsCategoriesDTO) {
        ProductsCategories productsCategories = productsCategoriesRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        productsCategories.setName(productsCategoriesDTO.getName());
        productsCategories.setDescription(productsCategoriesDTO.getDescription());
        ProductsCategories saveProductsCategories=productsCategoriesRepository.save(productsCategories);
        return ProductsCategoriesMapper.mapToDTO(saveProductsCategories);
    }

    @Override
    public ProductsCategoriesDTO deleteProductsCategory(Integer id) {
        ProductsCategories productsCategories = productsCategoriesRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        List<Products> productsToDelete = productsRepository.findByCategoryId(productsCategories.getId());
        if (!productsToDelete.isEmpty()) {
            productsRepository.deleteAll(productsToDelete);
        }
        productsCategoriesRepository.delete(productsCategories);
        return ProductsCategoriesMapper.mapToDTO(productsCategories);
    }


}
