package chat.app.prod.report;

import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlockRequestService {

    private final BlockRequestRepository blockRequestRepository;
    private final UserCaseRepository userCaseRepository;
    private final UserRepository userRepository;

    public BlockRequestService(BlockRequestRepository blockRequestRepository,
                               UserCaseRepository userCaseRepository,
                               UserRepository userRepository) {
        this.blockRequestRepository = blockRequestRepository;
        this.userCaseRepository = userCaseRepository;
        this.userRepository = userRepository;
    }

    public void createBlockRequest(Long userCaseId,
                                   String reporterUsername,
                                   String reason) {

        if (reason == null || reason.isBlank()) {
            throw new RuntimeException("Block request reason is required");
        }

        UserCase userCase = userCaseRepository.findById(userCaseId)
                .orElseThrow(() -> new RuntimeException("User case not found"));

        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        BlockRequest blockRequest = new BlockRequest(
                userCase,
                reporter,
                reason.trim(),
                BlockRequestStatus.PENDING,
                LocalDateTime.now()
        );

        blockRequestRepository.save(blockRequest);
    }

    public List<BlockRequest> getAllBlockRequests() {
        return blockRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<BlockRequest> getBlockRequestsByStatus(BlockRequestStatus status) {
        if (status == null) {
            return getAllBlockRequests();
        }

        return blockRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<BlockRequest> getBlockRequestsForCase(UserCase userCase) {
        return blockRequestRepository.findByUserCaseOrderByCreatedAtDesc(userCase);
    }

    public BlockRequest getById(Long id) {
        return blockRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Block request not found"));
    }
}