package chat.app.prod.profile;

import chat.app.prod.service.FriendService;
import chat.app.prod.post.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class ProfileController {

    private final FriendService friendService;
    private final PostService postService;

    public ProfileController(FriendService friendService, PostService postService) {
        this.friendService = friendService;
        this.postService = postService;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("title", "My Profile");
        model.addAttribute("username", username);
        model.addAttribute("friendCount", friendService.countFriends(username));
        model.addAttribute("posts", postService.getPostsByUsername(username));
        return "profile/profile";
    }
}
