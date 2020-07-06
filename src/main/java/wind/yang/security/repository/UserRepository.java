package wind.yang.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wind.yang.security.domain.entity.Account;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
