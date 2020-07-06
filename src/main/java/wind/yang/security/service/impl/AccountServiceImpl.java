package wind.yang.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wind.yang.security.domain.entity.Account;
import wind.yang.security.repository.UserRepository;
import wind.yang.security.service.UserService;

import javax.transaction.Transactional;

@Service("userService")
public class AccountServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
