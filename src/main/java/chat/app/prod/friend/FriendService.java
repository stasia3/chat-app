package chat.app.prod.friend;

import chat.app.prod.entity.User;
import chat.app.prod.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendService(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    public List<User> searchUsers(String username, String currentUsername) {
        if (username == null || username.trim().isEmpty()) {
            return List.of();
        }

        return userRepository.findByUsernameContainingIgnoreCase(username.trim())
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .toList();
    }

    public void sendFriendRequest(String senderUsername, String receiverUsername) {
        if (senderUsername.equals(receiverUsername)) {
            return;
        }

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        boolean alreadyExists = friendRequestRepository
                .findBySenderAndReceiverOrSenderAndReceiver(sender, receiver, receiver, sender)
                .isPresent();

        if (alreadyExists) {
            return;
        }

        FriendRequest request = new FriendRequest(
                sender,
                receiver,
                FriendRequestStatus.PENDING,
                LocalDateTime.now()
        );

        friendRequestRepository.save(request);
    }

    public List<FriendRequest> getPendingRequests(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendRequestRepository.findByReceiverAndStatus(currentUser, FriendRequestStatus.PENDING);
    }

    public void acceptRequest(Long requestId, String currentUsername) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getUsername().equals(currentUsername)) {
            return;
        }

        request.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(request);
    }

    public void rejectRequest(Long requestId, String currentUsername) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getReceiver().getUsername().equals(currentUsername)) {
            return;
        }

        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }

    public int countFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> acceptedRequests =
            friendRequestRepository.findByStatusAndSenderOrStatusAndReceiver(
                    FriendRequestStatus.ACCEPTED, user,
                    FriendRequestStatus.ACCEPTED, user
            );
        return acceptedRequests.size();
    }

    public List<User> getFriends(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> acceptedRequests = friendRequestRepository.findByStatusAndSenderOrStatusAndReceiver(
                FriendRequestStatus.ACCEPTED, user,
                FriendRequestStatus.ACCEPTED, user
        );

        return acceptedRequests.stream()
                .map(request -> {
                    if (request.getSender().getId().equals(user.getId())) {
                        return request.getReceiver();
                    }
                    return request.getSender();
                })
                .toList();
    }
}
