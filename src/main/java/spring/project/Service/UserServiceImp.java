package spring.project.Service;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import spring.project.JWT.CustomerUserDetailsService;
import spring.project.JWT.JwtFilter;
import spring.project.Models.User;
import spring.project.Repository.UserRepository;
import spring.project.Service.UserService;
import spring.project.Utils.CafeUtils;
import spring.project.Utils.EmailUtil;
import spring.project.constants.Cafeconstants;
import spring.project.wrapper.UserWrapper;

import java.util.*;

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
//  When a request is made to the signup endpoint, the HTTP request first goes through the filter chain and DoFilterInternal is one of the filters.
//  As this request matches one of public endpoints, the filter directly transfers the request and current response to the controller.
//  even though the signUp method itself does not include explicit security calls, it is related with the security.
//  Spring Security works by intercepting requests before they reach your application’s endpoints.
//  When you configure your security settings, you define which URLs should be accessible without authentication and which ones require it.
//  So if we removed signup url from the bypass urls in security settings, then Now, /signup will fall under.anyRequest().authenticated(),
//    meaning it will require authentication. Since new users are not authenticated when they are trying to sign up, they will be blocked
//    from accessing the /signup endpoint.

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    spring.project.JWT.jwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {}", requestMap);
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (auth.isAuthenticated()) {
                customerUserDetailsService.loadUserByUsername(requestMap.get("email"));
                if (customerUserDetailsService.getUserDatails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + jwtUtil.generateToken(
                            customerUserDetailsService.getUserDatails().getEmail(), customerUserDetailsService.getUserDatails().getRole()) + "\"}",
                            HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"" + "Wait for Admin Approvel." + "\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}",
                HttpStatus.BAD_REQUEST);
    }
//  When a request is made to the login endpoint, the HTTP request first goes through the filter chain and DoFilterInternal is one of the filters.
//  As this request matches one of public endpoints, the filter directly transfers the request and current response to the controller.
//  In the login method, a UsernamePasswordAuthenticationToken is created with the email and password from requestMap.
//  The authenticationManager.authenticate method uses this token to verify credentials against configured authentication providers (e.g., a database). This typically involves:
//    Loading the user details (using a service such as UserDetailsService).
//    Verifying the provided password against the stored password.
//  If the credentials are valid, a fully authenticated Authentication object is returned with the isAuthenticated flag set to true.
//  If credentials are invalid, an AuthenticationException is thrown, and the isAuthenticated flag remains false.

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userRepository.getAllUser(), HttpStatus.OK);

            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//  When a request is made to the getAllUser endpoint, the HTTP request first goes through the filter chain and DoFilterInternal is one of the filters.
//  As this request doesnt matches any public endpoints, then it will mark the user as authenticated if he is eligible, and pass to next filter.


    @Autowired
    EmailUtil emailUtil;

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userRepository.findById(Integer.parseInt(requestMap.get("id")));
                //Integer.parseInt is to convert String to integer

                if (!optional.isEmpty()) {

                    userRepository.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userRepository.getAllAdmin_mails());
                    return CafeUtils.getResponeEntity("User Status is updated Successfully", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponeEntity("User id doesn't exist", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponeEntity(Cafeconstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUsername());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtil.SendSimpleMessage(jwtFilter.getCurrentUsername(), "Account Approved", "USER:- " + user + "\n is approved by\nADMIN:-" + jwtFilter.getCurrentUsername(),  allAdmin);
        } else {
            emailUtil.SendSimpleMessage(jwtFilter.getCurrentUsername(), "Account Disabled", "USER:- " + user + "\n is disabled by\nADMIN:-" + jwtFilter.getCurrentUsername(),  allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponeEntity("true", HttpStatus.OK);
    }
    //this will return true only if the provided token is valid.


    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(jwtFilter.getCurrentUsername());
            if (!user.equals(null)) {
                if (user.getPassword().equals(requestMap.get("oldPassword"))) {
                    user.setPassword(requestMap.get("newPassword"));
                    userRepository.save(user);
                    return CafeUtils.getResponeEntity("Password Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponeEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
            }
            return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                emailUtil.forgetMail(user.getEmail() , "Credentials by Cafe Management System" , user.getPassword());

            return CafeUtils.getResponeEntity("Check Your mail for Credentials", HttpStatus.OK);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponeEntity(Cafeconstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
