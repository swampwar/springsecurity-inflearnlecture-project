package wind.yang.security.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "username"})
@ToString(exclude = {"userRoles"})
@Table(name = "ACCOUNT")
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

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "ACCOUNT_ROLES", // 조인테이블명
            joinColumns = { @JoinColumn(name = "ACCOUNT_ID")}, // 현재 엔티티의 외래키
            inverseJoinColumns = { @JoinColumn(name = "ROLE_ID")}) // 반대 엔티티의 외래키
    private Set<Role> userRoles = new HashSet<>();

    @Builder
    public Account(String username, String email, int age, String password, Set<Role> userRoles) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.password = password;
        this.userRoles = userRoles;
    }
}



