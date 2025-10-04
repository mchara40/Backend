package backend.Backend.Services.impl;

import backend.Backend.DTOS.ProductsDTO;
import backend.Backend.DTOS.ProductsResponseDTO;
import backend.Backend.Entities.Products;
import backend.Backend.Mappers.ProductsMapper;
import backend.Backend.Repositories.ProductsRepository;
import backend.Backend.Services.ProductsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductsServiceImpl implements ProductsService {
    private ProductsRepository productsRepository;
    @Override
    public ProductsResponseDTO create(ProductsResponseDTO productsDTO) {
        if (!productsRepository.existsByCategoryId(productsDTO.getCategoryId())) {
            throw new RuntimeException("Category with id " + productsDTO.getCategoryId() + " does not exist");
        }
        Products products = ProductsMapper.mapToEntityV2(productsDTO);
        Products saveproducts= productsRepository.save(products);

        return new ProductsResponseDTO(
                products.getCode(),
                products.getName(),
                products.getDescription(),
                products.getPriceStatus(),
                products.getPrice(),
                products.getCategoryId()
        );
    }

    @Override
    public List<ProductsDTO> getAllProducts() {
        List<Products> allProducts = productsRepository.findAll();
        return allProducts.stream().map(products -> ProductsMapper.mapToDTO(products))
                .collect(Collectors.toList());
    }

    @Override
    public ProductsDTO getProducts(Integer id) {
        Products products = productsRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        return ProductsMapper.mapToDTO(products);
    }

    @Override
    public ProductsDTO updateProducts(Integer id, ProductsDTO productsDTO) {
        Products products = productsRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        products.setName(productsDTO.getName());
        products.setCategoryId(productsDTO.getCategoryId());
        products.setDescription(productsDTO.getDescription());
        products.setCode(productsDTO.getCode());
        products.setPriceStatus(productsDTO.getPriceStatus());
        products.setPrice(productsDTO.getPrice());
        Products saveProducts=productsRepository.save(products);
        return ProductsMapper.mapToDTO(saveProducts);
    }

    @Override
    public ProductsDTO deleteProducts(Integer id) {
        Products products = productsRepository.findById(Long.valueOf(id))
                .orElseThrow(()->new RuntimeException("Products Categories is not exists with given id: " + id));
        productsRepository.delete(products);
        return ProductsMapper.mapToDTO(products);
    }


}
