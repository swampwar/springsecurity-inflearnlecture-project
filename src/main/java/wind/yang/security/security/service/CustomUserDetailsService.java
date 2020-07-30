package wind.yang.security.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wind.yang.security.domain.entity.Account;
import wind.yang.security.domain.entity.Role;
import wind.yang.security.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 인증시 입력된 정보(ID)에 해당하는 사용자정보(UserDetails)를 가져오는 서비스
 * FormAuthenticationProvider.authenticate() 에서 사용한다.
 */
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("username not found"));

        List<GrantedAuthority> roles = new ArrayList<>();
        for(Role role : account.getUserRoles()){
            roles.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        AccountContext accountContext = new AccountContext(account, roles);
        return accountContext;
    }
}
