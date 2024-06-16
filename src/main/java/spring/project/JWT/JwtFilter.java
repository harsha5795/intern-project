package spring.project.JWT;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private jwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    Claims claims = null;
    private String username = null;


    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUsername() {
        return username;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // Check if the request path matches any of the public endpoints
        if (httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            // If it matches, allow the request to proceed without authentication
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            // For other endpoints, attempt to extract and validate JWT token
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;
            String username = null; // Initialize username variable

            // Check if Authorization header with Bearer token is present
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                token = authorizationHeader.substring(7); // Extract JWT token from Authorization header
                username = jwtUtil.extractUsername(token); // Extract username from JWT token
                claims = jwtUtil.extractAllClaims(token); // Extract all claims from JWT token
            }

            // If username is extracted and there is no authenticated user context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from database based on the extracted username
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

                // Validate the extracted JWT token against loaded user details
                if (jwtUtil.validatetoken(token, userDetails)) {
                    // Create Authentication token using loaded user details
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Set additional authentication details from the request
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                    // Set this Authentication token into SecurityContextHolder for future reference
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            // Proceed with the filter chain after potential authentication setup
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

}