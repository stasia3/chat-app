package chat.app.prod.notification;

import chat.app.prod.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private User user;

    private boolean notifyFriendPosts = true;
    private boolean notifyPostComments = true;
    private boolean notifyPostLikes = true;
    private boolean notifyCommentDeleted = true;
    private boolean notifyFriendRequests = true;

    public NotificationSettings() {}

    public NotificationSettings(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isNotifyFriendPosts() {
        return notifyFriendPosts;
    }

    public void setNotifyFriendPosts(boolean notifyFriendPosts) {
        this.notifyFriendPosts = notifyFriendPosts;
    }

    public boolean isNotifyPostComments() {
        return notifyPostComments;
    }

    public void setNotifyPostComments(boolean notifyPostComments) {
        this.notifyPostComments = notifyPostComments;
    }

    public boolean isNotifyPostLikes() {
        return notifyPostLikes;
    }

    public void setNotifyPostLikes(boolean notifyPostLikes) {
        this.notifyPostLikes = notifyPostLikes;
    }

    public boolean isNotifyCommentDeleted() {
        return notifyCommentDeleted;
    }

    public void setNotifyCommentDeleted(boolean notifyCommentDeleted) {
        this.notifyCommentDeleted = notifyCommentDeleted;
    }

    public boolean isNotifyFriendRequests() {
        return notifyFriendRequests;
    }

    public void setNotifyFriendRequests(boolean notifyFriendRequests) {
        this.notifyFriendRequests = notifyFriendRequests;
    }

    // getters and setters
}