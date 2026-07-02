package chat.app.prod.notification;

import chat.app.prod.post.Post;
import chat.app.prod.user.User;

import java.time.LocalDateTime;

public class NotificationDto {

    private final Long id;
    private final User actor;
    private final String actorProfileImageUrl;
    private final NotificationType type;
    private final String message;
    private final Post post;
    private final LocalDateTime createdAt;

    public NotificationDto(Long id,
                           User actor,
                           String actorProfileImageUrl,
                           NotificationType type,
                           String message,
                           Post post,
                           LocalDateTime createdAt) {
        this.id = id;
        this.actor = actor;
        this.actorProfileImageUrl = actorProfileImageUrl;
        this.type = type;
        this.message = message;
        this.post = post;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }

    public User getActor() { return actor; }

    public String getActorProfileImageUrl() { return actorProfileImageUrl; }

    public NotificationType getType() { return type; }

    public String getMessage() { return message; }

    public Post getPost() { return post; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}