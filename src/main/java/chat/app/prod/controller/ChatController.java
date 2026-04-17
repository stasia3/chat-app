package chat.app.prod.controller;

import chat.app.prod.entity.User;
import chat.app.prod.repository.UserRepository;
import chat.app.prod.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ChatController {
    private final UserRepository userRepository;
    private final ChatService chatService;

    public ChatController(UserRepository userRepository, ChatService chatService) {
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    @GetMapping("/users")
    public String users(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();

        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .toList();

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
