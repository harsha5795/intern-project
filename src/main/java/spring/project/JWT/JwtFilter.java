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
        if (httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } else {
            String authorizationHeader = httpServletRequest.getHeader("Authorization");
            String token = null;
//            String username = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                token = authorizationHeader.substring(7); // Extract JWT token from Authorization header
                username = jwtUtil.extractUsername(token); // Extract username from JWT token
                claims = jwtUtil.extractAllClaims(token); // Extract all claims from JWT token
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validatetoken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

// When a request is made it must go through the series of filter chain before it goes to the controller
// In our application, doFilterInternal is one of the filters
// The method checks if the request path matches any of the public endpoints (/user/login, /user/forgotPassword, /user/signup).
// 1)If it matches, the filter directly transfers the request and current response to the next filter.
// 2)If the request path does not match these public endpoints,
//    The filter retrieves the Authorization header from the request and extracts the JWT token and extracts the username and claims from the token using jwtUtil.
//    If the username from the extracted token is not null and there is no existing authenticated user named by that username in the SecurityContextHolder
//       a)it will make add that user as authenticated in SecurityContextHolder only if that corresponding token is valid i.e it checks whether the username
//         extracted from the token exists in the database or not and expiry time of the token
//       b)it will not mark that user as authenticated in SecurityContextHolder if that token is not valid
//   Now the filter transfers the request and current response to the next filter

}