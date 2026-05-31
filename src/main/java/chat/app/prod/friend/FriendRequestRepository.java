package chat.app.prod.friend;

import chat.app.prod.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequestStatus status);
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    Optional<FriendRequest> findBySenderAndReceiverOrSenderAndReceiver(
            User sender1, User receiver1,
            User sender2, User receiver2
    );
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequestStatus status);
    List<FriendRequest> findByReceiverOrSender(User receiver, User sender);
    List<FriendRequest> findByStatusAndSenderOrStatusAndReceiver(
            FriendRequestStatus status1, User sender,
            FriendRequestStatus status2, User receiver
    );

}
