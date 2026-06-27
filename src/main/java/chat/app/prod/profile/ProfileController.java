package chat.app.prod.profile;

import chat.app.prod.friend.FriendService;
import chat.app.prod.post.PostService;
import chat.app.prod.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("posts", postService.getMyPostCards(username));
        return "profile/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Model model, Authentication authentication) {
        String username = authentication.getName();

        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        model.addAttribute("title", "Edit Profile");
        model.addAttribute("profile", profile);

        return "profile/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute Profile profileForm,
                              Authentication authentication) {
        String username = authentication.getName();

        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFirstName(profileForm.getFirstName());
        profile.setLastName(profileForm.getLastName());
        profile.setHeadline(profileForm.getHeadline());
        profile.setBio(profileForm.getBio());
        profile.setProgrammingLanguages(profileForm.getProgrammingLanguages());
        profile.setGithubLink(profileForm.getGithubLink());
        profile.setLinkedinLink(profileForm.getLinkedinLink());

        profileRepository.save(profile);

        return "redirect:/profile";
    }
}
