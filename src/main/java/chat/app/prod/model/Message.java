package chat.app.prod.model;

import java.time.LocalDateTime;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private LocalDateTime timestamp;

    public Message() {}

    public Message(String sender, String receiver, String content, LocalDateTime timestamp) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


}
