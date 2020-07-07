package wind.yang.security.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {
    @GetMapping(path="/messages")
    public String message(){
        return "user/messages";
    }

    @GetMapping(path="/api/messages")
    @ResponseBody
    public String apiMessages(){
        return "messages ok";
    }

}
