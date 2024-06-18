package spring.project.Service;

import org.springframework.http.ResponseEntity;
import spring.project.Models.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

    ResponseEntity<List<Category>> getAllCategory(String filterValue);

    ResponseEntity<String> update(Map<String, String> requestMap);

}