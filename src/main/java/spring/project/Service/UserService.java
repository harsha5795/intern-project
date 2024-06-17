package spring.project.Service;

import org.springframework.http.ResponseEntity;
import spring.project.wrapper.UserWrapper;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(Map<String,String> requestMap);
    ResponseEntity<String> login(Map<String, String> requestMap);
    ResponseEntity<List<UserWrapper>> getAllUser();
    ResponseEntity<String> update(Map<String, String> requestMap);
    ResponseEntity<String> checkToken();
    ResponseEntity<String> changePassword(Map<String, String> requestMap);
    ResponseEntity<String> forgetPassword(Map<String, String> requestMap);

}
