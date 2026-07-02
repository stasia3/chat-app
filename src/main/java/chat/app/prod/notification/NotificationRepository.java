package chat.app.prod.notification;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverAndHiddenFalseOrderByCreatedAtDesc(User receiver);

    List<Notification> findByReceiverAndTypeAndHiddenFalseOrderByCreatedAtDesc(
            User receiver,
            NotificationType type
    );
}