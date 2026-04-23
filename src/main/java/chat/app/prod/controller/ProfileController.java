package chat.app.prod.controller;

import chat.app.prod.service.FriendService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class ProfileController {

    private final FriendService friendService;

    public ProfileController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("title", "My Profile");
        model.addAttribute("username", username);
        model.addAttribute("friendCount", friendService.countFriends(username));
        return "profile";
    }
}
