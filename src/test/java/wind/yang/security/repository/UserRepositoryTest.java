package wind.yang.security.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import wind.yang.security.domain.entity.Account;
import wind.yang.security.domain.entity.Role;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    public void 양방향관계_Account와_Role이_저장되는지_테스트(){
        Account account = new Account();
        account.setUsername("테스터");

        Role role = new Role();
        role.setRoleName("ROLE_TEST");

        account.getUserRoles().add(role); // Account-Role 맵핑
        role.getAccounts().add(account); // Role-Account 맵핑

        userRepository.save(account);

        Optional<Account> savedUserOpt = userRepository.findByUsername("테스터");
        Account savedUser = savedUserOpt.orElseThrow(NoSuchElementException::new);


        List<Role> roles = roleRepository.findAll();
        System.out.println("Roles Size : " + roles.size());

        for(Role eachRole : savedUser.getUserRoles()){
            System.out.println(savedUser.getUsername() + " has " + eachRole.getRoleName());
        }

        for(Role eachRole : roles){
            Set<Account> accounts = eachRole.getAccounts();
            for(Account eachAccount : accounts){
                System.out.println(eachRole.getRoleName() + " has " + eachAccount.getUsername());
            }
        }
    }

}