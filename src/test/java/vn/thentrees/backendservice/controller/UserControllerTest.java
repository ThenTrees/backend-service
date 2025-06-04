package vn.thentrees.backendservice.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import vn.thentrees.backendservice.common.Gender;
import vn.thentrees.backendservice.dto.response.UserPageResponse;
import vn.thentrees.backendservice.dto.response.UserResponse;
import vn.thentrees.backendservice.helper.JwtProvider;
import vn.thentrees.backendservice.service.UserDetailService;
import vn.thentrees.backendservice.service.UserService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class) // chi test o controller
class UserControllerTest {

    // b1: inject cac mock bean

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailService userDetailService;

    @MockitoBean
    private JwtProvider jwtProvider; // mocked bean for test

    private static UserResponse userResponse1;
    private static UserResponse userResponse2;
    // b2: Setup: khoi tao data de test|khi chay se chay qua cai nay dau tien
    @BeforeAll
    static void beforeAll() {
        userResponse1 = new UserResponse();

        userResponse1.setId(1L);
        userResponse1.setUsername("username1");
        userResponse1.setEmail("email1");
        userResponse1.setFirstName("first1");
        userResponse1.setLastName("last1");
        userResponse1.setGender(Gender.MALE);
        userResponse1.setPhone("0938749250");

        userResponse2 = new UserResponse();
        userResponse2.setId(2L);
        userResponse2.setUsername("username2");
        userResponse2.setEmail("email2");
        userResponse2.setFirstName("first2");
        userResponse2.setLastName("last2");
        userResponse2.setGender(Gender.FEMALE);
        userResponse2.setPhone("0938749250");
        userResponse2.setGender(Gender.MALE);
    }

    @Test
    @WithMockUser(authorities = {"admin", "manager"}) // check ben controller dang dung gi -> cuc ki quan trong
    void testGetAllUsers() throws Exception {
        List<UserResponse> users = List.of(userResponse1, userResponse2);

        UserPageResponse userPageResponse = new UserPageResponse();
        userPageResponse.setPageNumber(0);
        userPageResponse.setPageSize(10);
        userPageResponse.setTotalElements(2);
        userPageResponse.setTotalPages(1);
        userPageResponse.setUsers(users);

        when(userService.findAll(null,null,0,20)).thenReturn(userPageResponse);
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("get user list")))
                .andExpect(jsonPath("$.data.pageNumber", is(0)))
                .andExpect(jsonPath("$.data.pageSize", is(10)))
                .andExpect(jsonPath("$.data.totalElements", is(2)))
                .andExpect(jsonPath("$.data.totalPages", is(1)))
        ;
    }

}
