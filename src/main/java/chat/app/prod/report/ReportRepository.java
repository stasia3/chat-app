package chat.app.prod.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByCreatedAtDesc();

    List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<Report> findByTargetTypeOrderByCreatedAtDesc(ReportTargetType targetType);

    List<Report> findByStatusAndTargetTypeOrderByCreatedAtDesc(
            ReportStatus status,
            ReportTargetType targetType
    );
}