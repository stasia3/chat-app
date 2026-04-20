package chat.app.prod.service;

import chat.app.prod.entity.Message;
import chat.app.prod.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {
    private final MessageRepository messageRepository;

    public ChatService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void sendMessage(String sender, String receiver, String content) {
        Message message = new Message(sender, receiver, content, LocalDateTime.now());
        messageRepository.save(message);
    }

    public List<Message> getConversation(String user1, String user2) {
        return messageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(user1, user2, user2, user1);
    }
}
