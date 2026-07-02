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

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               ProfileRepository profileRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
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
}