package chat.app.prod;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "DevPortal");
        model.addAttribute("message", "Code is the goal, not money. Create, don’t consume.");
        return "index";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("title", "My Profile");
        return "profile";
    }
}
