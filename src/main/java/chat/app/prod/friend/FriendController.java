package chat.app.prod.friend;

import chat.app.prod.profile.ProfileRepository;
import chat.app.prod.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class FriendController {

    private final FriendService friendService;
    private final ProfileRepository profileRepository;

    public FriendController(FriendService friendService, ProfileRepository profileRepository) {
        this.friendService = friendService;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/friends")
    public String friendsPage(@RequestParam(defaultValue = "discover") String tab,
                              @RequestParam(required = false) String search,
                              Model model,
                              Authentication authentication) {

        String currentUsername = authentication.getName();

        model.addAttribute("title", "Find Friends · DevPortal");
        model.addAttribute("activeTab", tab);
        model.addAttribute("search", search);

        model.addAttribute("friendCount", friendService.countFriends(currentUsername));
        model.addAttribute("requestCount", friendService.countAllRequests(currentUsername));

        if ("discover".equals(tab)) {
            model.addAttribute("users",
                    friendService.searchUsersForFriendRequest(currentUsername, search)
                            .stream()
                            .map(this::toFriendUserDto)
                            .toList());
        }

        if ("friends".equals(tab)) {
            model.addAttribute("friends",
                    friendService.searchFriends(currentUsername, search)
                            .stream()
                            .map(this::toFriendUserDto)
                            .toList());
        }

        if ("requests".equals(tab)) {
            model.addAttribute("newRequests",
                    friendService.searchPendingReceivedRequests(currentUsername, search)
                            .stream()
                            .map(request -> toFriendRequestDto(request, true))
                            .toList());

            model.addAttribute("pendingRequests",
                    friendService.searchPendingSentRequests(currentUsername, search)
                            .stream()
                            .map(request -> toFriendRequestDto(request, false))
                            .toList());

            model.addAttribute("rejectedRequests",
                    friendService.searchRejectedSentRequests(currentUsername, search)
                            .stream()
                            .map(request -> toFriendRequestDto(request, false))
                            .toList());
        }

        return "friend/find";
    }

    @PostMapping("/friends/request/{username}")
    public String sendRequest(@PathVariable String username,
                              Authentication authentication) {
        String currentUsername = authentication.getName();

        friendService.sendFriendRequest(currentUsername, username);

        return "redirect:/friends?tab=discover";
    }

    @GetMapping("/notifications")
    public String notificatioinsPage(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        List<FriendRequest> requests = friendService.getPendingRequests(currentUsername);

        model.addAttribute("title", "Notifications");
        model.addAttribute("requests", requests);

        return "friend/notifications";
    }

    @PostMapping("/notifications/accept/{id}")
    public String acceptRequest(@PathVariable Long id, Authentication authentication) {
        friendService.acceptRequest(id, authentication.getName());
        return "redirect:/friends?tab=requests";
    }

    @PostMapping("/notifications/reject/{id}")
    public String rejectRequest(@PathVariable Long id, Authentication authentication) {
        friendService.rejectRequest(id, authentication.getName());
        return "redirect:/friends?tab=requests";
    }

    @GetMapping("/friends/list")
    public String friendsList(Model model, Authentication authentication) {
        String currentUsername  = authentication.getName();

        model.addAttribute("title", "My Friends");
        model.addAttribute("friends", friendService.getFriends(currentUsername));

        return "redirect:/friends?tab=friends";
    }

    @PostMapping("/friends/remove/{username}")
    public String removeFriend(@PathVariable String username,
                               Authentication authentication) {

        friendService.removeFriend(authentication.getName(), username);

        return "redirect:/friends?tab=friends";
    }

    @PostMapping("/friends/request/{id}/cancel")
    public String cancelSentRequest(@PathVariable Long id,
                                    Authentication authentication) {

        friendService.cancelSentRequest(id, authentication.getName());

        return "redirect:/friends?tab=requests";
    }

    @PostMapping("/friends/request/{id}/delete")
    public String deleteRejectedSentRequest(@PathVariable Long id,
                                            Authentication authentication) {

        friendService.deleteRejectedSentRequest(id, authentication.getName());

        return "redirect:/friends?tab=requests";
    }

    private FriendUserDto toFriendUserDto(User user) {
        String imageUrl = profileRepository.findByUserUsername(user.getUsername())
                .map(profile -> profile.getProfileImageUrl())
                .orElse(null);

        return new FriendUserDto(user, imageUrl);
    }
    private FriendRequestDto toFriendRequestDto(FriendRequest request, boolean senderAvatar) {

        String username = senderAvatar
                ? request.getSender().getUsername()
                : request.getReceiver().getUsername();

        String imageUrl = profileRepository.findByUserUsername(username)
                .map(profile -> profile.getProfileImageUrl())
                .orElse(null);

        return new FriendRequestDto(request, imageUrl);
    }

}
