package chat.app.prod.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BlockedAccountController {

    @GetMapping("/account-blocked")
    public String accountBlocked() {
        return "auth/account-blocked";
    }
}