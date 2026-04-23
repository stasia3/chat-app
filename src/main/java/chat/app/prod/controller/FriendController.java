package chat.app.prod.controller;

import chat.app.prod.entity.FriendRequest;
import chat.app.prod.entity.User;
import chat.app.prod.service.FriendService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping("/friends")
    public String friendsPage(@RequestParam(required = false) String username,
                              Model model,
                              Authentication authentication) {
        String currentUsername = authentication.getName();

        List<User> users = friendService.searchUsers(username, currentUsername);

        model.addAttribute("title", "Friends");
        model.addAttribute("search", username);
        model.addAttribute("users", users);

        return "friends";
    }

    @PostMapping("/friends/request/{username}")
    public String sendRequest(@PathVariable String username,
                              Authentication authentication) {
        String currentUsername = authentication.getName();

        friendService.sendFriendRequest(currentUsername, username);

        return "redirect:/friends";
    }

    @GetMapping("/notifications")
    public String notificatioinsPage(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        List<FriendRequest> requests = friendService.getPendingRequests(currentUsername);

        model.addAttribute("title", "Notifications");
        model.addAttribute("requests", requests);

        return "notifications";
    }

    @PostMapping("/notifications/accept/{id}")
    public String acceptRequest(@PathVariable Long id, Authentication authentication) {
        friendService.acceptRequest(id, authentication.getName());
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/reject/{id}")
    public String rejectRequest(@PathVariable Long id, Authentication authentication) {
        friendService.rejectRequest(id, authentication.getName());
        return "redirect:/notifications";
    }

    @GetMapping("/friends/list")
    public String friendsList(Model model, Authentication authentication) {
        String currentUsername  = authentication.getName();

        model.addAttribute("title", "My Friends");
        model.addAttribute("friends", friendService.getFriends(currentUsername));

        return "friends-list";
    }
}
