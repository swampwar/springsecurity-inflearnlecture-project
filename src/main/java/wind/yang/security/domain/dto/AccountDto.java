package wind.yang.security.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccountDto {
    private String id;
    private String username;
    private String password;
    private String email;
    private int age;
    private List<String> roles;
}
