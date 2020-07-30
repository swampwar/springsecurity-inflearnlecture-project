package wind.yang.security.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import wind.yang.security.domain.entity.AccessIp;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
    AccessIp findByIpAddress(String IpAddress);
}
