package chat.app.prod.notification;

import chat.app.prod.friend.FriendRequest;
import chat.app.prod.post.Comment;
import chat.app.prod.post.Post;
import chat.app.prod.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User receiver;

    @ManyToOne
    private User actor;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private FriendRequest friendRequest;

    private boolean hidden = false;

    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(User receiver, User actor, NotificationType type,
                        String message, Post post, Comment comment,
                        FriendRequest friendRequest, LocalDateTime createdAt) {
        this.receiver = receiver;
        this.actor = actor;
        this.type = type;
        this.message = message;
        this.post = post;
        this.comment = comment;
        this.friendRequest = friendRequest;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public User getActor() {
        return actor;
    }

    public void setActor(User actor) {
        this.actor = actor;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;

    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}