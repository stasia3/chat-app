package chat.app.prod.friend;

import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
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

    public int countSentRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendRequestRepository
                .findBySenderAndStatus(user, FriendRequestStatus.PENDING)
                .size();
    }

    public List<User> searchUsersForFriendRequest(String currentUsername, String search) {
        if (search == null || search.trim().isEmpty()) {
            return List.of();
        }

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userRepository.findByUsernameContainingIgnoreCase(search.trim())
                .stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .filter(user -> friendRequestRepository
                        .findBySenderAndReceiverOrSenderAndReceiver(
                                currentUser, user,
                                user, currentUser
                        )
                        .isEmpty())
                .toList();
    }

    public List<User> searchFriends(String currentUsername, String search) {
        List<User> friends = getFriends(currentUsername);

        if (search == null || search.trim().isEmpty()) {
            return friends;
        }

        String normalizedSearch = search.trim().toLowerCase();

        return friends.stream()
                .filter(friend -> friend.getUsername().toLowerCase().contains(normalizedSearch))
                .toList();
    }

    public List<FriendRequest> getPendingSentRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendRequestRepository.findBySenderAndStatus(user, FriendRequestStatus.PENDING);
    }

    public List<FriendRequest> getRejectedSentRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return friendRequestRepository.findBySenderAndStatus(user, FriendRequestStatus.REJECTED);
    }

    public void removeFriend(String currentUsername, String friendUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        FriendRequest request = friendRequestRepository
                .findBySenderAndReceiverAndStatus(currentUser, friend, FriendRequestStatus.ACCEPTED)
                .or(() -> friendRequestRepository
                        .findBySenderAndReceiverAndStatus(friend, currentUser, FriendRequestStatus.ACCEPTED))
                .orElseThrow(() -> new RuntimeException("Friendship not found"));

        friendRequestRepository.delete(request);
    }

    public void cancelSentRequest(Long requestId, String currentUsername) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getSender().getUsername().equals(currentUsername)) {
            return;
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            return;
        }

        friendRequestRepository.delete(request);
    }

    public void deleteRejectedSentRequest(Long requestId, String currentUsername) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!request.getSender().getUsername().equals(currentUsername)) {
            return;
        }

        if (request.getStatus() != FriendRequestStatus.REJECTED) {
            return;
        }

        friendRequestRepository.delete(request);
    }

    public int countAllRequests(String username) {
        return getPendingRequests(username).size();
//                + getPendingSentRequests(username).size()
//                + getRejectedSentRequests(username).size();
    }

    public List<FriendRequest> searchPendingReceivedRequests(String username, String search) {
        return filterRequestsByUser(getPendingRequests(username), search, true);
    }

    public List<FriendRequest> searchPendingSentRequests(String username, String search) {
        return filterRequestsByUser(getPendingSentRequests(username), search, false);
    }

    public List<FriendRequest> searchRejectedSentRequests(String username, String search) {
        return filterRequestsByUser(getRejectedSentRequests(username), search, false);
    }

    private List<FriendRequest> filterRequestsByUser(List<FriendRequest> requests,
                                                     String search,
                                                     boolean searchSender) {
        if (search == null || search.trim().isEmpty()) {
            return requests;
        }

        String normalizedSearch = search.trim().toLowerCase();

        return requests.stream()
                .filter(request -> {
                    String username = searchSender
                            ? request.getSender().getUsername()
                            : request.getReceiver().getUsername();

                    return username.toLowerCase().contains(normalizedSearch);
                })
                .toList();
    }

    public boolean areFriends(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
                .orElseThrow(() -> new RuntimeException("User not found: " + username1));

        User user2 = userRepository.findByUsername(username2)
                .orElseThrow(() -> new RuntimeException("User not found: " + username2));

        return friendRequestRepository
                .findBySenderAndReceiverOrSenderAndReceiver(user1, user2, user2, user1)
                .filter(request -> request.getStatus() == FriendRequestStatus.ACCEPTED)
                .isPresent();
    }

}
