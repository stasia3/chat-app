package chat.app.prod.auth;

import chat.app.prod.profile.Profile;
import chat.app.prod.profile.ProfileRepository;
import chat.app.prod.report.BlockRequestService;
import chat.app.prod.report.UserBlockService;
import chat.app.prod.user.Role;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserBlockService userBlockService;
    private final BlockRequestService blockRequestService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          ProfileRepository profileRepository,
                          UserBlockService userBlockService,
                          BlockRequestService blockRequestService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.userBlockService = userBlockService;
        this.blockRequestService = blockRequestService;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {

        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists.");
            return "auth/register";
        }

        if (!user.getEmail().matches(emailRegex)) {
            model.addAttribute("error", "Invalid email format.");
            return "auth/register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email already exists.");
            return "auth/register";
        }

        if (!user.getPassword().matches(passwordRegex)) {
            model.addAttribute("error", "Password must have at least 8 characters, one uppercase letter, one lowercase letter, and one digit.");
            return "auth/register";
        }

        if (!user.getPassword().equals(user.getPasswordCheck())) {
            model.addAttribute("error", "Passwords do not match.");
            return "auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        User savedUser = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(savedUser);

        profileRepository.save(profile);

        return "redirect:/login";
    }

    @GetMapping("/admin/block-requests/{id}")
    public String blockRequestDetails(@PathVariable Long id, Model model) {
        model.addAttribute("request", blockRequestService.getById(id));

        return "admin/block-request-details";
    }

    @PostMapping("/admin/block-requests/{id}/approve")
    public String approveBlockRequest(@PathVariable Long id,
                                      @RequestParam String adminDecision,
                                      Authentication authentication) {

        blockRequestService.approveBlockRequest(
                id,
                authentication.getName(),
                adminDecision
        );

        userBlockService.blockUserFromRequest(
                id,
                authentication.getName(),
                adminDecision
        );

        return "redirect:/admin/block-requests/" + id;
    }

    @PostMapping("/admin/block-requests/{id}/reject")
    public String rejectBlockRequest(@PathVariable Long id,
                                     @RequestParam String adminDecision,
                                     Authentication authentication) {

        blockRequestService.rejectBlockRequest(
                id,
                authentication.getName(),
                adminDecision
        );

        return "redirect:/admin/block-requests/" + id;
    }
}