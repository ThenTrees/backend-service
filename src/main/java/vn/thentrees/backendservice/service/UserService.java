package vn.thentrees.backendservice.service;

import vn.thentrees.backendservice.dto.request.UserCreationRequest;
import vn.thentrees.backendservice.dto.request.UserPasswordRequest;
import vn.thentrees.backendservice.dto.request.UserUpdateRequest;
import vn.thentrees.backendservice.dto.response.UserPageResponse;
import vn.thentrees.backendservice.dto.response.UserResponse;

public interface UserService {
    UserPageResponse findAll(String keyword, String sort, int page, int size);

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    long save(UserCreationRequest req);

    void update(UserUpdateRequest req);

    void changePassword(UserPasswordRequest req);

    void delete(Long id);
}
