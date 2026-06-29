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
    private final MessageEncryptionService encryptionService;

    public ChatService(MessageRepository messageRepository, UserRepository userRepository, MessageEncryptionService messageEncryptionService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.encryptionService = messageEncryptionService;
    }

    public Message sendMessage(String senderUsername, String receiverUsername, String content) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        String encryptedContent = encryptionService.encrypt(content);
        Message message = new Message(sender, receiver, encryptedContent, LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getConversation(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found: " + username1));
        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found: " + username2));
        return messageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(user1, user2, user2, user1);
    }

    public List<Message> getNewMessages(String username1, String username2, Long lastId) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found: " + username1));

        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found: " + username2));

        return messageRepository
                .findByIdGreaterThanAndSenderAndReceiverOrIdGreaterThanAndSenderAndReceiverOrderByTimestampAsc(
                        lastId,
                        user1,
                        user2,
                        lastId,
                        user2,
                        user1
                );
    }

    public MessageDto toDto(Message message) {
        String decryptedContent = encryptionService.decrypt(message.getContent());
        return new MessageDto(message, decryptedContent);
    }

    public List<MessageDto> getConversationDtos(String username1, String username2) {
        return getConversation(username1, username2)
                .stream()
                .map(this::toDto)
                .toList();
    }
}
