package spring.project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.project.Repository.BillRepository;
import spring.project.Repository.CategoryRepository;
import spring.project.Repository.ProductRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImp implements DashboardService{
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    BillRepository billRepository;


    @Override
    public ResponseEntity<Map<String, Object>> getCount() {

        System.out.println("inside getCount");

        Map<String , Object> map = new HashMap<>();
        map.put("category" , categoryRepository.count());
        map.put("product" , productRepository.count());
        map.put("bill" , billRepository.count());
        return new ResponseEntity<>(map , HttpStatus.OK);
    }

}
