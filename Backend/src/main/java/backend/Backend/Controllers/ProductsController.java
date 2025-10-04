package backend.Backend.Controllers;
import backend.Backend.DTOS.ProductsDTO;
import backend.Backend.DTOS.ProductsResponseDTO;
import backend.Backend.Services.ProductsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private ProductsService productsService;


    @PostMapping("/create")
    public ResponseEntity<ProductsResponseDTO> createProducts(@RequestBody ProductsResponseDTO productsDTO){
        ProductsResponseDTO products = productsService.create(productsDTO);
        return new ResponseEntity<>(products, HttpStatus.CREATED);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<ProductsDTO>> getAllProducts(){
        List<ProductsDTO> allProducts= productsService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{ProductsId}")
    public ResponseEntity<ProductsDTO> getProducts(@PathVariable("ProductsId")Integer ProductsId ){
        ProductsDTO ProductsDTO =  productsService.getProducts(ProductsId);
        return ResponseEntity.ok(ProductsDTO);

    }

    @PutMapping("/{ProductsId}")
    public ResponseEntity<ProductsDTO> updateProducts(@PathVariable("ProductsId")Integer ProductsId,@RequestBody ProductsDTO employeeDto){
        ProductsDTO employee = productsService.updateProducts(ProductsId, employeeDto);
        return ResponseEntity.ok(employee);
    }

    @RequestMapping(value = "/{ProductsId}", method = DELETE)
    public ResponseEntity<ProductsDTO> deleteProducts(@PathVariable("ProductsId")Integer ProductsId){
        ProductsDTO ProductsDTO = productsService.deleteProducts(ProductsId);
        return ResponseEntity.ok(ProductsDTO);
    }
}

