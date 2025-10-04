package backend.Backend.Controllers;

import backend.Backend.DTOS.ProductsCategoriesDTO;
import backend.Backend.Services.ProductsCategoriesService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products-categories")
public class ProductsCategoriesController {
    private ProductsCategoriesService productsCategoriesService;


    @PostMapping("/create")
    public ResponseEntity<ProductsCategoriesDTO> createProductsCategories(@RequestBody ProductsCategoriesDTO productsCategoriesDTO){
        ProductsCategoriesDTO productsCategories = productsCategoriesService.create(productsCategoriesDTO);
        return new ResponseEntity<>(productsCategories, HttpStatus.CREATED);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<ProductsCategoriesDTO>> getAllProductsCategories(){
        List<ProductsCategoriesDTO> allProductsCategories= productsCategoriesService.getAllProductsCategories();
        return ResponseEntity.ok(allProductsCategories);
    }

    @GetMapping("/{productsCategoriesId}")
    public ResponseEntity<ProductsCategoriesDTO> getProductsCategories(@PathVariable("productsCategoriesId")Integer productsCategoriesId ){
        ProductsCategoriesDTO productsCategoriesDTO =  productsCategoriesService.getProductsCategory(productsCategoriesId);
        return ResponseEntity.ok(productsCategoriesDTO);

    }

    @PutMapping("/{productsCategoriesId}")
    public ResponseEntity<ProductsCategoriesDTO> updateProductsCategory(@PathVariable("productsCategoriesId")Integer productsCategoriesId,@RequestBody ProductsCategoriesDTO employeeDto){
        ProductsCategoriesDTO employee = productsCategoriesService.updateProductsCategory(productsCategoriesId, employeeDto);
        return ResponseEntity.ok(employee);
    }

    @RequestMapping(value = "/{productsCategoriesId}", method = DELETE)
    public ResponseEntity<ProductsCategoriesDTO> deleteProductsCategory(@PathVariable("productsCategoriesId")Integer productsCategoriesId){
        ProductsCategoriesDTO productsCategoriesDTO = productsCategoriesService.deleteProductsCategory(productsCategoriesId);
        return ResponseEntity.ok(productsCategoriesDTO);
    }
}

