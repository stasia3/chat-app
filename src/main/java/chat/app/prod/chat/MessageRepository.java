package chat.app.prod.chat;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
            User sender1, User receiver1,
            User sender2, User receiver2
    );
    List<Message> findByIdGreaterThanAndSenderAndReceiverOrIdGreaterThanAndSenderAndReceiverOrderByTimestampAsc(
            Long lastId1,
            User sender1,
            User receiver1,
            Long lastId2,
            User sender2,
            User receiver2
    );
}
