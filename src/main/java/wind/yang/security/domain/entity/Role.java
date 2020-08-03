package wind.yang.security.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"accounts", "resourcesSet"})
@Table(name = "ROLE")
@Entity
public class Role {
    @Id @GeneratedValue @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles", cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roleSet")
    @OrderBy("ordernum desc")
    private Set<Resources> resourcesSet = new LinkedHashSet<>();

    @Builder
    public Role(String roleName, String roleDesc) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
    }
}



