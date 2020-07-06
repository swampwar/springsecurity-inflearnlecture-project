package wind.yang.security.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class AccountDto {
    private String username;
    private String password;
    private String email;
    private int age;
    private String role;
}
