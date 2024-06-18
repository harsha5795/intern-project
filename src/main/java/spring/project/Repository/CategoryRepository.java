package spring.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.Models.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}