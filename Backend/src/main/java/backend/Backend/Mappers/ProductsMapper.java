package backend.Backend.Mappers;

import backend.Backend.DTOS.ProductsDTO;
import backend.Backend.DTOS.ProductsResponseDTO;
import backend.Backend.Entities.Products;

public class ProductsMapper {
    public static ProductsDTO mapToDTO(Products products){
        return new ProductsDTO(
                products.getId(),
                products.getCategoryId(),
                products.getCode(),
                products.getName(),
                products.getDescription(),
                products.getPriceStatus(),
                products.getPrice(),
                products.getProductsCategories().getName()

        );
    }
    public static Products mapToEntity(ProductsDTO productsDTO){
        return new Products(
                productsDTO.getName(),
                productsDTO.getCategoryId(),
                productsDTO.getCode(),
                productsDTO.getDescription(),
                productsDTO.getPrice(),
                productsDTO.getPriceStatus()
        );
    }

    public static Products mapToEntityV2(ProductsResponseDTO productsDTO){
        return new Products(
                productsDTO.getName(),
                productsDTO.getCategoryId(),
                productsDTO.getCode(),
                productsDTO.getDescription(),
                productsDTO.getPrice(),
                productsDTO.getPriceStatus()
        );
    }
}