package chat.app.prod.service;

import chat.app.prod.model.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatService {
    private final Map<String, List<Message>> conversations = new HashMap<>();

    private String buildConversationKey(String user1, String user2) {
        List<String> users = Arrays.asList(user1, user2);
        Collections.sort(users);
        return users.get(0) + "_" + users.get(1);
    }

    public List<Message> getConversation(String user1, String user2) {
        String key = buildConversationKey(user1, user2);
        return conversations.getOrDefault(key, new ArrayList<>());
    }

    public void sendMessage(String sender, String receiver, String content) {
        String key = buildConversationKey(sender, receiver);

        Message message = new Message(sender, receiver, content, LocalDateTime.now());

        conversations.computeIfAbsent(key, k-> new ArrayList<>()).add(message);
    }
}
