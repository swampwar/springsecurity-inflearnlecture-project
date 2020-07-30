package wind.yang.security.controller.user;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import wind.yang.security.domain.dto.AccountDto;
import wind.yang.security.domain.entity.Account;
import wind.yang.security.service.UserService;
import wind.yang.security.service.impl.AccountServiceImpl;

@Controller
public class UserController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @GetMapping(path="/mypage")
    public String myPage(){
        return "user/mypage";
    }

    @GetMapping(path = "/users")
    public String createUser(){
        return "user/login/register";
    }

    @PostMapping(path = "/users")
    public String createUser(AccountDto accountDto){
        ModelMapper modelMapper = new ModelMapper();

        Account account = modelMapper.map(accountDto, Account.class);
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        userService.createUser(account);

        return "redirect:/";
    }

    @GetMapping("/order")
    public String order(){
        userService.order();
        return "user/mypage";
    }
}
