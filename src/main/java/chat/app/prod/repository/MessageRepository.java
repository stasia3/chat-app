package chat.app.prod.repository;

import chat.app.prod.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
            String sender1, String receiver1,
            String sender2, String receiver2
    );
}
