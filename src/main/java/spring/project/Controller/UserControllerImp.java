package spring.project.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import spring.project.Controller.UserController;
import spring.project.Service.UserService;
import spring.project.Utils.CafeUtils;
import spring.project.constants.Cafeconstants;

import java.util.Map;

@RestController
public class UserControllerImp implements UserController {

    @Autowired
    UserService userService;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
            try {
                //System.out.println("inside userRestImpl");
                return userService.signUp(requestMap);
            } catch (Exception ex) {
                ex.printStackTrace();
      // This stack trace shows the sequence of method calls in the program that were
      // active when the exception occurred. By examining the stack trace, developers
      // can understand where exactly in the code the error happened and what caused it.
            }
            //System.out.println("Before return");
            return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        try {
            return userService.login(requestMap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
