package chat.app.prod.chat;

import java.time.LocalDateTime;

public class MessageDto {

    private Long id;
    private String senderUsername;
    private String content;
    private LocalDateTime timestamp;

    public MessageDto(Message message, String decryptedContent) {
        this.id = message.getId();
        this.senderUsername = message.getSender().getUsername();
        this.content = decryptedContent;
        this.timestamp = message.getTimestamp();
    }

    public Long getId() {
        return id;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}