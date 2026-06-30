package chat.app.prod.chat;

import chat.app.prod.friend.FriendUserDto;
import chat.app.prod.profile.Profile;
import chat.app.prod.profile.ProfileRepository;
import chat.app.prod.user.User;
import chat.app.prod.friend.FriendService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {
    private final FriendService friendService;
    private final ChatService chatService;
    private final ProfileRepository profileRepository;


    public ChatController(FriendService friendService, ChatService chatService, ProfileRepository profileRepository) {
        this.friendService = friendService;
        this.chatService = chatService;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/users")
    public String users(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        List<User> users = friendService.getFriends(currentUsername);

        model.addAttribute("users", users);
        model.addAttribute("currentUsername", currentUsername);
//        @GetMapping("/users")
//        public String users() {
//            return "redirect:/chat";
//        }
//        return "friend/users";
        return "redirect:/chat";
    }

    @GetMapping("/chat")
    public String chatHome(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("users",
                friendService.getFriends(currentUsername)
                        .stream()
                        .map(this::toFriendUserDto)
                        .toList());
        model.addAttribute("selectedUsername", null);
        model.addAttribute("messages", List.of());

        return "chat/chat";
    }

    @GetMapping("/chat/{username}")
    public String chatPage(@PathVariable String username,
                           Model model,
                           Authentication authentication) {
        String currentUsername = authentication.getName();

        if (currentUsername.equals(username) || !friendService.areFriends(currentUsername, username)) {
            return "redirect:/users";
        }

        String selectedUserImageUrl = profileRepository.findByUserUsername(username)
                .map(profile -> profile.getProfileImageUrl())
                .orElse(null);

        model.addAttribute("selectedUserImageUrl", selectedUserImageUrl);
        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("users",
                friendService.getFriends(currentUsername)
                        .stream()
                        .map(this::toFriendUserDto)
                        .toList());
        model.addAttribute("messages", chatService.getConversationDtos(currentUsername, username));

        return "chat/chat";
    }

    @PostMapping("/chat/{username}")
    public String sendMessage(@PathVariable String username,
                              @RequestParam String content,
                              Authentication authentication) {
        String currentUsername = authentication.getName();

        if (currentUsername.equals(username) || !friendService.areFriends(currentUsername, username)) {
            return "redirect:/users";
        }

        if (content != null && !content.trim().isEmpty()) {
            chatService.sendMessage(currentUsername, username, content.trim());
        }

        return "redirect:/chat/" + username;
    }

    @GetMapping("/chat/{username}/messages")
    @ResponseBody
    public List<MessageDto> getNewMessages(@PathVariable String username,
                                           @RequestParam(defaultValue = "0") Long lastId,
                                           Authentication authentication) {
        String currentUsername = authentication.getName();

        if (currentUsername.equals(username)) {
            return List.of();
        }

        return chatService.getNewMessages(currentUsername, username, lastId)
                .stream()
                .map(chatService::toDto)
                .toList();
    }

    private FriendUserDto toFriendUserDto(User user) {
        String imageUrl = profileRepository.findByUserUsername(user.getUsername())
                .map(profile -> profile.getProfileImageUrl())
                .orElse(null);

        return new FriendUserDto(user, imageUrl);
    }
}
