package chat.app.prod.report;

import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserBlockService {

    private final UserBlockRepository userBlockRepository;
    private final BlockRequestRepository blockRequestRepository;
    private final UserRepository userRepository;

    public UserBlockService(UserBlockRepository userBlockRepository,
                            BlockRequestRepository blockRequestRepository,
                            UserRepository userRepository) {
        this.userBlockRepository = userBlockRepository;
        this.blockRequestRepository = blockRequestRepository;
        this.userRepository = userRepository;
    }

    public void blockUserFromRequest(Long blockRequestId,
                                     String adminUsername,
                                     String reason) {

        BlockRequest blockRequest = blockRequestRepository.findById(blockRequestId)
                .orElseThrow(() -> new RuntimeException("Block request not found"));

        User userToBlock = blockRequest.getUserCase().getUser();

        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (userBlockRepository.existsByUserAndActiveTrue(userToBlock)) {
            throw new RuntimeException("User is already blocked");
        }

        UserBlock userBlock = new UserBlock(
                userToBlock,
                admin,
                blockRequest,
                reason,
                LocalDateTime.now(),
                true
        );

        userBlockRepository.save(userBlock);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserBlock activeBlock = userBlockRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new RuntimeException("User is not currently blocked"));

        activeBlock.setActive(false);

        userBlockRepository.save(activeBlock);
    }

    public boolean isUserBlocked(User user) {
        return userBlockRepository.existsByUserAndActiveTrue(user);
    }

    public List<UserBlock> getBlockHistoryForUser(User user) {
        return userBlockRepository.findByUserOrderByBlockedAtDesc(user);
    }

    public List<UserBlock> getActiveBlocks() {
        return userBlockRepository.findByActiveTrueOrderByBlockedAtDesc();
    }
}