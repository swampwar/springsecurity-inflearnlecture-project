package wind.yang.security.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConfigController {
    @GetMapping(path = "/config")
    public String config(){
        return "admin/config";
    }
}
