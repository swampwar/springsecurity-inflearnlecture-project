package wind.yang.security.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManageController {
    @GetMapping(path = "messages")
    public String mypage(){
        return "user/messages";
    }
}
