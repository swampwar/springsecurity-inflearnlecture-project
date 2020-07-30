package wind.yang.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wind.yang.security.domain.entity.RoleHierarchy;

public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {
    RoleHierarchy findByChildName(String roleName);
}
