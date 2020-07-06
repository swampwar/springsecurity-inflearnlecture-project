package wind.yang.security.domain.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Account {
    @Id @GeneratedValue
    private Long id;
    @Column
    private String username;
    @Column
    private String email;
    @Column
    private int age;
    @Column
    private String password;
    @Column
    private String role;

}
