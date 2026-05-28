package chat.app.prod.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "DevPortal");
        model.addAttribute("message", "A calm and focused space for programmers to share ideas, build projects, and connect — designed without noise, just clarity.");
        return "index";
    }
}