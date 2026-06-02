package chat.app.prod.profile;

import chat.app.prod.friend.FriendService;
import chat.app.prod.post.PostService;
import chat.app.prod.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class ProfileController {

    private final FriendService friendService;
    private final PostService postService;
    private final ProfileRepository profileRepository;

    public ProfileController(FriendService friendService, PostService postService, ProfileRepository profileRepository) {
        this.friendService = friendService;
        this.postService = postService;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        String username = authentication.getName();

        Profile profile = profileRepository.findByUserUsername(username)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

        model.addAttribute("profile", profile);
        model.addAttribute("title", "My Profile");
        model.addAttribute("username", username);
        model.addAttribute("friendCount", friendService.countFriends(username));
        model.addAttribute("posts", postService.getPostsByUsername(username));
        return "profile/profile";
    }
}
