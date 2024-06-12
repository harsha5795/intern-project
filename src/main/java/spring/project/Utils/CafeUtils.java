package spring.project.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtils {
   private CafeUtils(){
       //private constructor so that we cannot create an object
   }

    public static ResponseEntity<String> getResponeEntity(String responseMessage , HttpStatus httpStatus) {
        return new ResponseEntity<String>("{\"messag\":\"" + responseMessage + "\"}", httpStatus);
    }
//  By declaring it as static, We can directly access it with the class name without creating an object.


}
