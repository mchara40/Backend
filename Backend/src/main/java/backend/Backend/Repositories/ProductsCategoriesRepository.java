package backend.Backend.Repositories;


import backend.Backend.Entities.ProductsCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsCategoriesRepository extends JpaRepository<ProductsCategories,Long> {

}
