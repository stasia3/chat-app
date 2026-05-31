package chat.app.prod.controller;

import chat.app.prod.entity.User;
import chat.app.prod.service.ChatService;
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

    public ChatController(FriendService friendService, ChatService chatService) {
        this.friendService = friendService;
        this.chatService = chatService;
    }

    @GetMapping("/users")
    public String users(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        List<User> users = friendService.getFriends(currentUsername);

        model.addAttribute("users", users);
        model.addAttribute("currentUsername", currentUsername);

        return "users";
    }

    @GetMapping("/chat/{username}")
    public String chatPage(@PathVariable String username,
                           Model model,
                           Authentication authentication) {
        String currentUsername = authentication.getName();

        model.addAttribute("currentUsername", currentUsername);
        model.addAttribute("selectedUsername", username);
        model.addAttribute("messages", chatService.getConversation(currentUsername, username));

        return "chat";
    }

    @PostMapping("/chat/{username}")
    public String sendMessage(@PathVariable String username,
                              @RequestParam String content,
                              Authentication authentication) {
        String currentUsername = authentication.getName();

        if (content != null && !content.trim().isEmpty()) {
            chatService.sendMessage(currentUsername, username, content.trim());
        }

        return "redirect:/chat/" + username;
    }
}
