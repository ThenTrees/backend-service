package vn.thentrees.backendservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.thentrees.backendservice.dto.request.SignInRequest;
import vn.thentrees.backendservice.dto.response.TokenResponse;
import vn.thentrees.backendservice.exception.ForbiddenException;
import vn.thentrees.backendservice.exception.InvalidDataException;
import vn.thentrees.backendservice.helper.JwtProvider;
import vn.thentrees.backendservice.model.UserEntity;
import vn.thentrees.backendservice.repository.UserRepository;
import vn.thentrees.backendservice.service.AuthenticationService;

import org.springframework.security.access.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static vn.thentrees.backendservice.common.TokenType.*;

@Service
@Slf4j(topic = "AUTHENTICATION-SERVICE")
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;


    @Override
    public TokenResponse getAccessToken(SignInRequest request) {
        log.info("Get access token");

        List<String> authorities = new ArrayList<>();
        try {
            // Thực hiện xác thực với username và password
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            log.info("isAuthenticated = {}", authenticate.isAuthenticated());
            log.info("Authorities: {}", authenticate.getAuthorities().toString());
            authorities.add(authenticate.getAuthorities().toString());

            // Nếu xác thực thành công, lưu thông tin vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (BadCredentialsException | DisabledException e) {
            log.error("errorMessage: {}", e.getMessage());
            throw new AccessDeniedException(e.getMessage());
        }

        String accessToken = jwtProvider.generateAccessToken(request.getUsername(), authorities);
        String refreshToken = jwtProvider.generateRefreshToken(request.getUsername(), authorities);

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }


    @Override
    public TokenResponse getRefreshToken(String refreshToken) {
        log.info("Get refresh token");

        if (!StringUtils.hasLength(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        try {
            // Verify token
            String userName = jwtProvider.extractUsername(refreshToken, REFRESH_TOKEN);

            // check user is active or inactivated
            UserEntity user = userRepository.findByUsername(userName);

            List<String> authorities = new ArrayList<>();
            user.getAuthorities().forEach(authority -> authorities.add(authority.getAuthority()));

            // generate new access token
            String accessToken = jwtProvider.generateAccessToken(user.getUsername(), authorities);

            return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
        } catch (Exception e) {
            log.error("Access denied! errorMessage: {}", e.getMessage());
            throw new ForbiddenException(e.getMessage());
        }
    }
}
