package backend.Backend.Repositories;


import backend.Backend.Entities.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductsRepository extends JpaRepository<Products,Long> {
    List<Products> findByCategoryId(Long categoryId);
    // Check if a category exists by its ID
    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM ProductsCategories  WHERE id = :categoryId")
    Boolean existsByCategoryId(@Param("categoryId") Long categoryId);
}
