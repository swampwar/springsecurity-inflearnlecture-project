package wind.yang.security.aopsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import wind.yang.security.domain.dto.AccountDto;

import java.security.Principal;

@Controller
public class AopSecurityController {
    @Autowired
    AopMethodService aopMethodService;

    @Autowired
    AopPointcutService aopPointcutService;

    @GetMapping("/preAuthorize")
    @PreAuthorize("hasRole('ROLE_USER') and #account.username == principal.username") // spEL을 지원한다.
    public String preAuthorize(AccountDto account, Model model, Principal principal){
        model.addAttribute("method", "Success @PreAuthorize");
        return "aop/method";
    }

    @GetMapping("/methodSecured")
    public String methodSecured(Model model){
        aopMethodService.methodSecured();
        model.addAttribute("method", "Success MethodSecured");

        return "aop/method";
    }

    @GetMapping("/pointcutSecured")
    public String pointcutSecured(Model model){
        aopPointcutService.pointcutSecure();
        aopPointcutService.notSecured();
        model.addAttribute("pointcut", "Seccess PointcutSecured");

        return "aop/method";
    }
}
