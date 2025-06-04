package vn.thentrees.backendservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.thentrees.backendservice.dto.request.UserCreationRequest;
import vn.thentrees.backendservice.dto.request.UserPasswordRequest;
import vn.thentrees.backendservice.dto.request.UserUpdateRequest;
import vn.thentrees.backendservice.dto.response.ApiResponse;
import vn.thentrees.backendservice.dto.response.UserPageResponse;
import vn.thentrees.backendservice.dto.response.UserResponse;
import vn.thentrees.backendservice.service.UserService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Controller")
@Slf4j(topic = "USER-CONTROLLER")
@RequiredArgsConstructor
@Validated
public class UserController {
    private static final String SUCCESS = "success";
    private final UserService userService;

    @Operation(summary = "Get user list", description = "API retrieve user from database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User list retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping()
    @PreAuthorize("hasAnyAuthority('admin', 'manager')")
    public ResponseEntity<ApiResponse<UserPageResponse>> getList(@RequestParam(required = false) String keyword,
                                                                 @RequestParam(required = false) String sort,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "20") int size) {
        log.info("Get user list");
        ApiResponse<UserPageResponse> response = ApiResponse.<UserPageResponse>builder()
                .status(SUCCESS)
                .code(HttpStatus.OK.value())
                .message("get user list")
                .data(userService.findAll(keyword, sort, page, size))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user detail", description = "API retrieve user detail by ID from database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User detail retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetail(@PathVariable Long userId) {
        log.info("Get user detail by ID: {}", userId);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .status(SUCCESS)
                .code(HttpStatus.OK.value())
                .message("get user detail")
                .data(userService.findById(userId))
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create User", description = "API add new user to database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Long>> createUser(@RequestBody @Valid UserCreationRequest request) {
        log.info("Create User: {}", request);

        ApiResponse<Long> response = ApiResponse.<Long>builder()
                .status(SUCCESS)
                .code(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(userService.save(request))
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update User", description = "API update user to database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "User updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PutMapping("/upd")
    public ResponseEntity<ApiResponse<Object>> updateUser(@RequestBody UserUpdateRequest request) {
        log.info("Updating user: {}", request);
        userService.update(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.builder()
                        .status(SUCCESS)
                        .code(HttpStatus.ACCEPTED.value())
                        .message("User updated successfully")
                        .timestamp(Instant.now())
                        .build());
    }

    @Operation(summary = "Change Password", description = "API change password for user to database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @PatchMapping("/change-pwd")
    public ResponseEntity<ApiResponse<Object>> changePassword(@RequestBody UserPasswordRequest request) {
        log.info("Changing password for user: {}", request);
        userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.builder()
                        .status(SUCCESS)
                        .code(HttpStatus.NO_CONTENT.value())
                        .message("Password updated successfully")
                        .data(null)
                        .timestamp(Instant.now())
                        .build());
    }

    @Operation(summary = "Delete user", description = "API activate user from database")
    @ApiResponses(
        value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user: {}", userId);
        userService.delete(userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.builder()
                    .status(SUCCESS)
                    .code(HttpStatus.RESET_CONTENT.value())
                    .message("User deleted successfully")
                    .data(null)
                    .timestamp(Instant.now())
                    .build());

    }
}