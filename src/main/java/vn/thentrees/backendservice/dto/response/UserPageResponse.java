package vn.thentrees.backendservice.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserPageResponse extends PageResponseAbstract implements Serializable {
    private List<UserResponse> users;
}