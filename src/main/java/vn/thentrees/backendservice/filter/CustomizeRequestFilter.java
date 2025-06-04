package vn.thentrees.backendservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.thentrees.backendservice.common.TokenType;
import vn.thentrees.backendservice.helper.JwtProvider;
import vn.thentrees.backendservice.service.UserDetailService;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-FILTER")
public class CustomizeRequestFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailService serviceDetail;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Request method: {} - URL: {}", request.getMethod(), request.getRequestURL());

        final String authHeader = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasLength(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Authorization token: {}", token);
            String username = "";
            try{
                username = jwtProvider.extractUsername(token, TokenType.ACCESS_TOKEN);
                log.info("Username from token: {}", username);
            }catch (AccessDeniedException e) {
                log.info("Access denied: {}", e.getMessage());
                throw e;
            }
            UserDetails user = serviceDetail.userDetailService().loadUserByUsername(username);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        }else {
            filterChain.doFilter(request, response);
        }
    }
}
