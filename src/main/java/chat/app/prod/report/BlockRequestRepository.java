package chat.app.prod.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRequestRepository extends JpaRepository<BlockRequest, Long> {

    List<BlockRequest> findAllByOrderByCreatedAtDesc();

    List<BlockRequest> findByStatusOrderByCreatedAtDesc(BlockRequestStatus status);

    List<BlockRequest> findByUserCaseOrderByCreatedAtDesc(UserCase userCase);
}