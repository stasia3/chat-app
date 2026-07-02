package chat.app.prod.notification;

import chat.app.prod.friend.FriendRequest;
import chat.app.prod.post.Comment;
import chat.app.prod.post.Post;
import chat.app.prod.profile.ProfileRepository;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final NotificationSettingsRepository settingsRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               ProfileRepository profileRepository,
                               NotificationSettingsRepository settingsRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.settingsRepository = settingsRepository;
    }

    public List<NotificationDto> getNotifications(String username, String type) {
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications;

        if (type != null && !type.isBlank() && !"ALL".equals(type)) {
            NotificationType notificationType = NotificationType.valueOf(type);

            notifications = notificationRepository
                    .findByReceiverAndTypeAndHiddenFalseOrderByCreatedAtDesc(receiver, notificationType);
        } else {
            notifications = notificationRepository
                    .findByReceiverAndHiddenFalseOrderByCreatedAtDesc(receiver);
        }

        return notifications.stream()
                .map(this::toDto)
                .toList();
    }

    public void createNotification(User receiver,
                                   User actor,
                                   NotificationType type,
                                   String message,
                                   Post post,
                                   Comment comment,
                                   FriendRequest friendRequest) {

        if (receiver == null) {
            return;
        }

        if (actor != null && receiver.getId().equals(actor.getId())) {
            return;
        }

        NotificationSettings settings = settingsRepository.findByUser(receiver)
                .orElseGet(() -> settingsRepository.save(new NotificationSettings(receiver)));

        if (type == NotificationType.FRIEND_POST && !settings.isNotifyFriendPosts()) return;
        if (type == NotificationType.POST_COMMENT && !settings.isNotifyPostComments()) return;
        if (type == NotificationType.POST_LIKE && !settings.isNotifyPostLikes()) return;
        if (type == NotificationType.COMMENT_DELETED && !settings.isNotifyCommentDeleted()) return;
        if (type == NotificationType.FRIEND_REQUEST && !settings.isNotifyFriendRequests()) return;

        Notification notification = new Notification(
                receiver,
                actor,
                type,
                message,
                post,
                comment,
                friendRequest,
                LocalDateTime.now()
        );

        notificationRepository.save(notification);
    }

    public void hideNotification(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getReceiver().getUsername().equals(username)) {
            return;
        }

        notification.setHidden(true);
        notificationRepository.save(notification);
    }

    public void hideAllNotifications(String username) {
        User receiver = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notifications =
                notificationRepository.findByReceiverAndHiddenFalseOrderByCreatedAtDesc(receiver);

        notifications.forEach(notification -> notification.setHidden(true));

        notificationRepository.saveAll(notifications);
    }

    private NotificationDto toDto(Notification notification) {
        String actorProfileImageUrl = null;

        if (notification.getActor() != null) {
            actorProfileImageUrl = profileRepository
                    .findByUserUsername(notification.getActor().getUsername())
                    .map(profile -> profile.getProfileImageUrl())
                    .orElse(null);
        }

        return new NotificationDto(
                notification.getId(),
                notification.getActor(),
                actorProfileImageUrl,
                notification.getType(),
                notification.getMessage(),
                notification.getPost(),
                notification.getCreatedAt()
        );
    }

    public NotificationSettings getSettings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return settingsRepository.findByUser(user)
                .orElseGet(() -> settingsRepository.save(new NotificationSettings(user)));
    }

    public void updateSettings(String username,
                               boolean notifyFriendPosts,
                               boolean notifyPostComments,
                               boolean notifyPostLikes,
                               boolean notifyCommentDeleted,
                               boolean notifyFriendRequests) {

        NotificationSettings settings = getSettings(username);

        settings.setNotifyFriendPosts(notifyFriendPosts);
        settings.setNotifyPostComments(notifyPostComments);
        settings.setNotifyPostLikes(notifyPostLikes);
        settings.setNotifyCommentDeleted(notifyCommentDeleted);
        settings.setNotifyFriendRequests(notifyFriendRequests);

        settingsRepository.save(settings);
    }
}