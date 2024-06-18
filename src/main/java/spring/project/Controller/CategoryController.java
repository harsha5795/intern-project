package spring.project.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.Models.Category;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/category")
public interface CategoryController {

    @PostMapping(path = "/add")
    public ResponseEntity<String> addNewCategory(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/get")
    public ResponseEntity<List<Category>> getAllCategory(@RequestParam(required = false) String filterValue);

// By setting required = false, you're telling Spring that there is no necessity of sending filterValue input.
//  If it's absent, Spring will assign null to it
// By the way here we give inputs in the formdata, becoz if we give as rawdata then there is no @RequestBody to
//  convert it into maprequest


    @PostMapping(path = "/update")
    public ResponseEntity<String> update(@RequestBody Map<String, String> requestMap);

}
