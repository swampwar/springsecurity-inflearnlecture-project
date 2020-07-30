package wind.yang.security.domain.dto;

import lombok.Getter;
import lombok.Setter;
import wind.yang.security.domain.entity.Role;

import java.util.Set;

@Getter
@Setter
public class ResourcesDto {
    private String id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet;
}
