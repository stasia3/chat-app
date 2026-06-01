package chat.app.prod.chat;

import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public void sendMessage(String senderUsername, String receiverUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = new Message(sender, receiver, content, LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getConversation(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found: " + username1));
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found: " + username2));
        return messageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(user1, user2, user2, user1);
    }
}
