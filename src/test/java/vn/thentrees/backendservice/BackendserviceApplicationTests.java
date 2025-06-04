package vn.thentrees.backendservice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import vn.thentrees.backendservice.controller.AuthenticationController;
import vn.thentrees.backendservice.controller.UserController;

@SpringBootTest
class BackendserviceApplicationTests {

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private AuthenticationController authenticationController;

	@Test
	void contextLoads() {
		Assertions.assertThat(userController).isNotNull();
		Assertions.assertThat(authenticationController).isNotNull();
	}

}
