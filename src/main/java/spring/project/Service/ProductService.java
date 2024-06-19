package spring.project.Service;

import org.springframework.http.ResponseEntity;
import spring.project.wrapper.ProductWrapper;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);
    ResponseEntity<List<ProductWrapper>> getAllProduct();
    ResponseEntity<String> update(Map<String, String> requestMap);
    ResponseEntity<String> delete(Integer id);
    ResponseEntity<String> updateProductStatus(Map<String, String> requestMap);
    ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);
    ResponseEntity<ProductWrapper> getProductById(Integer id);
}
