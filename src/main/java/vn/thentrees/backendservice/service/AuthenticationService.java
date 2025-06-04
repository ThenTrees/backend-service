package vn.thentrees.backendservice.service;

import vn.thentrees.backendservice.dto.request.SignInRequest;
import vn.thentrees.backendservice.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse getAccessToken(SignInRequest request);

    TokenResponse getRefreshToken(String request);
}
