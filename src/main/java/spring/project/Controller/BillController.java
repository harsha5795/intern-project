package spring.project.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.Models.Bill;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/bill")
public interface BillController {

    @PostMapping(path = "/generateReport")
    public ResponseEntity<String> generateReport(@RequestBody Map<String, Object> requestMap);
//Here value of productDetails key  will be in the form of JSON array
//Here, Map<String, Object> requestMap is used to handle the incoming JSON request body as a generic datatype i.e Object

    @GetMapping(path = "/getBills")
    public ResponseEntity<List<Bill>> getBills();
//for admin all bills will be returned and for a user his corresponding bills will be returned

    @PostMapping(path = "/getPdf")
    public ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> requestMap);
//output will be a byte array here we must provide the filename i.e uuid
//if that corresponding file doesn't exists at that appropriate place it file explorer, it will normally
//generate the pdf and add it at appropriate place in file explorer without saving again inside the database

    @PostMapping(path = "/delete/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Integer id);


}
