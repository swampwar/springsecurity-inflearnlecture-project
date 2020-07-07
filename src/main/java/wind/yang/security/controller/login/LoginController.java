package wind.yang.security.controller.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wind.yang.security.domain.entity.Account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error
                      , @RequestParam(value = "exception", required = false) String exception
                      , Model model){
        model.addAttribute("erorr", error);
        model.addAttribute("exception", exception);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            new SecurityContextLogoutHandler().logout(req, resp, authentication);
        }

        return "redirect:/login";
    }

    @GetMapping("/denied")
    public String denied(@RequestParam(name = "exception", required = false) String exception, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = (Account) authentication.getPrincipal();
        model.addAttribute("exception", exception);
        model.addAttribute("username", account.getUsername());

        return "user/login/denied";
    }
}
