package spring.project.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spring.project.Models.User;
import spring.project.Repository.UserRepository;
import spring.project.Service.UserService;
import spring.project.Utils.CafeUtils;
import spring.project.constants.Cafeconstants;

import java.util.Map;
import java.util.Objects;

@Slf4j
//Instead of manually creating a logger instance in each class, @Slf4j does it for you.
//It’s part of Lombok. You just annotate your class with @Slf4j, and you can start logging
//using log.info(), log.error(), etc.
@Service
//We keep @Service in ServiceImp but not at Service because, in Controller we call
//@autowire
//Userservice userservice
//As we kept @Service in serviceImp, an object of serviceImp is assigned to the interface poiner
public class UserServiceImp implements UserService {

    @Autowired
    UserRepository userRepository;
    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }
    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signup {}", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                //System.out.println("inside validaSignUpMap");
                User user = userRepository.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponeEntity("Successfully  Registered.", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponeEntity("Email already exits.", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponeEntity(Cafeconstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
