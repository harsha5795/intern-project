package spring.project.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import spring.project.Models.User;
import jakarta.servlet.Filter;

import java.util.Arrays;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    @Autowired
    JwtFilter jwtFilter;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customerUserDetailsService);
//    }
    @Bean
    public CustomerUserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }
//  used to load user from the database

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
//  Here we are essentially setting up the rules and methods that will be used to verify and
//  authenticate users in our application.
//  providing userDetailsService class to load user from the database

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.

                // Disable CSRF protection because we're using JWT tokens for authentication
                csrf(csrf -> csrf.disable())

                // Configure CORS to allow requests from any origin, method, and header
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.asList("*"));
                    configuration.setAllowedMethods(Arrays.asList("*"));
                    configuration.setAllowedHeaders(Arrays.asList("*"));
                    return configuration;
                }))

                // Allow unrestricted access to login, signup, and forgotPassword endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login", "/user/signup", "/user/forgotPassword").permitAll()
                        .anyRequest().authenticated()
                )

                // Configure session management to be stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure custom authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT token authentication filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Build the security filter chain
                .build();
    }

//  (Cross-Site Request Forgery):
//    When you're logged into your online banking, if you unknowingly visit a sneaky website, it can secretly
//    submit a form in the background with appropriate url forgery. This form might instruct your browser to
//    transfer money from your bank account to the attacker's account, taking advantage of your logged-in session
//    without your permission. This sneaky trick is called CSRF.
//  Disabling CSRF protection with JWT means we trust that the JWT token itself is secure enough to verify the user's
//  identity and prevent malicious actions.


//  Stateful Session:
//    Normally, when you log into a website, the server creates a session for your browser. This session stores
//    information about your login status, permissions, etc. Each subsequent request from your browser includes a
//    session ID, which the server uses to identify and manage your session.
//  Stateless Session:
//    In a stateless configuration, the server does not keep any session information. Each request must include all
//    necessary details for the server to authenticate and authorize the request. This typically involves using tokens
//    (like JWTs) that contain user information and are validated with each requ
//  By configuring session management as stateless, Spring Security ensures that each request stands alone and contains all
//  necessary authentication details.


//  Spring Security uses a series of filters to handle different aspects of security.
//  So we are adding JWT Filter before the inbuilt UsernamePasswordAuthenticationFilter


}










