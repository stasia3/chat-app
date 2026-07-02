package chat.app.prod.profile;

import chat.app.prod.friend.FriendService;
import chat.app.prod.post.PostService;
import chat.app.prod.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
        model.addAttribute("username", username);

        return "profile/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute Profile profileForm,
                              @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                              Authentication authentication) throws IOException {

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

        if (profileImage != null && !profileImage.isEmpty()) {
            String uploadDir = "uploads/profile-images/";

            Files.createDirectories(Paths.get(uploadDir));

            String originalFilename = profileImage.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String filename = username + "-" + UUID.randomUUID() + extension;

            Path filePath = Paths.get(uploadDir, filename);
            Files.write(filePath, profileImage.getBytes());

            profile.setProfileImageUrl("/uploads/profile-images/" + filename);
        }

        profileRepository.save(profile);

        return "redirect:/profile";
    }

    @GetMapping("/profile/{username}")
    public String viewUserProfile(@PathVariable String username,
                                  Model model,
                                  Authentication authentication) {
        String currentUsername = authentication.getName();

        if (currentUsername.equals(username)) {
            return "redirect:/profile";
        }

        Profile profile = profileRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        model.addAttribute("title", username + " · Profile");
        model.addAttribute("profile", profile);
        model.addAttribute("username", username);
        model.addAttribute("currentUsername", currentUsername);

        model.addAttribute("friendCount", friendService.countFriends(username));
        model.addAttribute("posts", postService.getMyPostCards(username));

        model.addAttribute("areFriends", friendService.areFriends(currentUsername, username));

        return "profile/public-profile";
    }
}
