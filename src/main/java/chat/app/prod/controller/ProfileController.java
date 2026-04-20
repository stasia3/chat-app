package chat.app.prod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class ProfileController {
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("title", "My Profile");
        model.addAttribute("username", username);
        return "profile";
    }
}
