package vn.thentrees.backendservice.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.thentrees.backendservice.common.Gender;
import vn.thentrees.backendservice.common.UserStatus;
import vn.thentrees.backendservice.common.UserType;
import vn.thentrees.backendservice.dto.request.AddressRequest;
import vn.thentrees.backendservice.dto.request.UserCreationRequest;
import vn.thentrees.backendservice.dto.request.UserPasswordRequest;
import vn.thentrees.backendservice.dto.request.UserUpdateRequest;
import vn.thentrees.backendservice.dto.response.UserPageResponse;
import vn.thentrees.backendservice.dto.response.UserResponse;
import vn.thentrees.backendservice.exception.ResourceNotFoundException;
import vn.thentrees.backendservice.model.Role;
import vn.thentrees.backendservice.model.UserEntity;
import vn.thentrees.backendservice.repository.AddressRepository;
import vn.thentrees.backendservice.repository.RoleRepository;
import vn.thentrees.backendservice.repository.UserRepository;
import vn.thentrees.backendservice.service.impl.UserServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Unit test for service layer
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static UserEntity userSample1;
    private static UserEntity userSample2;
    private static Role role;

    private UserService userService;

    private @Mock AddressRepository addressRepository;
    private @Mock UserRepository userRepository;
    private @Mock PasswordEncoder passwordEncoder;
    private @Mock RoleRepository roleRepository;

    @BeforeAll
    static void beforeAll() {
        // This method can be used to set up any static resources needed for the tests
        userSample1 = new UserEntity();
        userSample1.setId(Long.valueOf(1L));
        userSample1.setUsername("testuser1");
        userSample1.setFirstName("test");
        userSample1.setLastName("user1");
        userSample1.setEmail("example1@gmail.com");
        userSample1.setPassword("password1");
        userSample1.setType(UserType.USER);
        userSample1.setStatus(UserStatus.INACTIVE);
        userSample1.setGender(Gender.MALE);
        userSample1.setPhone("0938749250");

        userSample2 = new UserEntity();
        userSample2.setId(Long.valueOf(2L));
        userSample2.setUsername("testuser2");
        userSample2.setFirstName("test");
        userSample2.setLastName("user2");
        userSample2.setEmail("example2@gmail.com");
        userSample2.setPassword("password2");
        userSample1.setType(UserType.USER);
        userSample1.setStatus(UserStatus.ACTIVE);
        userSample1.setGender(Gender.MALE);
        userSample1.setPhone("0385788328");

        role = new Role();
        role.setId(Integer.valueOf(4));
        role.setName("user");
        role.setDescription("User role");
    }

    @BeforeEach
    void setUp() {
        // khoi tao buoc trien khai la userService
        userService = new UserServiceImpl(userRepository, roleRepository, addressRepository, passwordEncoder);
    }

    @Test
    void testGetListUser_Success() {
        // gia lap phuong thuc findAll cua userRepository
        Page<UserEntity> userPage = new PageImpl<>(List.of(userSample1, userSample2));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void testSearchUser_Success() {
        Page<UserEntity> userPage = new PageImpl<>(List.of(userSample1, userSample2));
        when(userRepository.searchByKeyword(any(), any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll("test", null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("testuser1", result.getUsers().get(0).getUsername());
    }

    @Test
    void testGetListUser_Empty() {
        // gia lap phuong thuc findAll cua userRepository
        Page<UserEntity> userPage = new PageImpl<>(List.of());
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        // Gọi phương thức cần kiểm tra
        UserPageResponse result = userService.findAll(null, null, 0, 20);

        Assertions.assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testGetUserById_Success() {
        // Gia lap phuong thuc findById cua userRepository
        when(userRepository.findById(Long.valueOf(1L))).thenReturn(Optional.of(userSample1));

        // Gọi phương thức cần kiểm tra
        UserResponse result = userService.findById(Long.valueOf(1L));

        Assertions.assertNotNull(result);
        assertEquals(1L, result.getId());
    }


    /**
     * Test trường hợp không tìm thấy người dùng theo ID
     * Nếu không tìm thấy người dùng, phương thức sẽ ném ra ResourceNotFoundException
     */
    @Test
    void testGetUserById_Failed() {
        // Gia lap phuong thuc findById cua userRepository
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(Long.valueOf(999L));
        });
        Assertions.assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findByUsername() {
        // gia lap doi tuong
        String username = "testuser1";

        // gia lap hanh vi
        when(userRepository.findByUsername(username)).thenReturn(userSample1);

        // goi phuong thuc can kiem tra
        UserResponse result = userService.findByUsername(username);

        Assertions.assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void findByEmail() {
// Giả lập hành vi của UserRepository
        when(userRepository.findByEmail("example1@gmail.com")).thenReturn(userSample1);

        // Gọi phương thức cần kiểm tra
        UserResponse result = userService.findByEmail("example1@gmail.com");

        Assertions.assertNotNull(result);
        assertEquals("example1@gmail.com", result.getEmail());
    }

    @Test
    void testSaveUser_Success() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(userSample1);
        when(roleRepository.findById(Integer.valueOf(4))).thenReturn(Optional.of(role));
        AddressRequest addressRequest = AddressRequest.builder()
                .addressType(Integer.valueOf(1))
                .floor("5")
                .city("HO CHI MINH")
                .country("VIET NAM")
                .street("Phan Van Tri")
                .apartmentNumber("14")
                .streetNumber("14")
                .building("The Ruby")
                .build();

        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
                .phone("0938749250")
                .firstName("test")
                .lastName("user1")
                .username("testuser1")
                .email("test.user.1@gmail.com")
                .type(UserType.USER)
                .gender(Gender.MALE)
                .addresses(List.of(addressRequest))
                .build();

        long userId = userService.save(userCreationRequest);

        // Kiem tra userId tra ve
        assertEquals(1L, userId);
    }

    @Test
    void updateUser_Success() {
        Long userId = (Long) 2L;

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setGender(Gender.FEMALE);
        updatedUser.setBirthday(new Date());
        updatedUser.setEmail("janesmith@gmail.com");
        updatedUser.setPhone("0123456789");
        updatedUser.setUsername("beforeUpdate");
        updatedUser.setType(UserType.USER);
        updatedUser.setStatus(UserStatus.ACTIVE);

        // gia lap hanh vi
        when(userRepository.findById(userId)).thenReturn(Optional.of(userSample2));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setId(userId);
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Smith");
        updateRequest.setGender(Gender.MALE);
        updateRequest.setBirthday(new Date());
        updateRequest.setEmail("janesmith@gmail.com");
        updateRequest.setPhone("0123456789");
        updateRequest.setUsername("affterUpdate");

        // goi phuong thuc can kiem tra
        userService.update(updateRequest);

        UserResponse result = userService.findById(userId);
        assertEquals("affterUpdate", result.getUsername());
    }

    @Test
    void testChangePassword_Success() {

        // gia lap doi tuong
        Long userId = (Long) 1L;
        UserPasswordRequest userPasswordRequest = new UserPasswordRequest();
        userPasswordRequest.setId(userId);
        userPasswordRequest.setPassword("password1");
        userPasswordRequest.setConfirmPassword("password1");

        // Giả lập hành vi của repository và password encoder
        when(userRepository.findById(userId)).thenReturn(Optional.of(userSample1));
        when(passwordEncoder.encode(userPasswordRequest.getPassword())).thenReturn("encodedNewPassword");

        // Gọi phương thức cần kiểm tra
        userService.changePassword(userPasswordRequest);

        // Kiểm tra mật khẩu được mã hóa và lưu
        assertEquals("encodedNewPassword", userSample1.getPassword());
        verify(userRepository, times(1)).save(userSample1);
        verify(passwordEncoder, times(1)).encode(userPasswordRequest.getPassword());
    }

    @Test
    void testDeleteUser_Success() {
        // gia lap du lieu
        Long userId = (Long) 1L;

        // gia lap hanh vi
        when(userRepository.findById(userId)).thenReturn(Optional.of(userSample1));

        // goi phuong thuc can kiem tra
        userService.delete(userId);

        // kiem tra ket qua
        assertEquals(UserStatus.INACTIVE, userSample1.getStatus());
        verify(userRepository, times(1)).save(userSample1); // dam bao rang repository duoc goi 1 lan
    }


    @Test
    void testDeleteUser_ThrowsException() {
// gia lap du lieu
        Long userId = (Long) 1L;

        // gia lap hanh vi
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // goi phuong thuc va kiem tra ngoai le
         Exception exception = assertThrows(ResourceNotFoundException.class, () -> userService.delete(userId));

         // kiem tra ngoai le
         Assertions.assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any(UserEntity.class)); // dam bao rang repository khong duoc goji lan nao
    }
}