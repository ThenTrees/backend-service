package vn.thentrees.backendservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vn.thentrees.backendservice.common.Gender;
import vn.thentrees.backendservice.common.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class UserCreationRequest implements Serializable {
    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotBlank(message = "firstName must be not blank")
    private String lastName;
    private Gender gender;
    private Date birthday;
    private String username;

    private String password;

    @Email(message = "Email invalid")
    private String email;
    private String phone;
    private UserType type;
    private List<AddressRequest> addresses; // home,office
}
