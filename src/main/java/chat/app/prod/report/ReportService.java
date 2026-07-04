package chat.app.prod.report;

import chat.app.prod.post.Comment;
import chat.app.prod.post.CommentRepository;
import chat.app.prod.post.Post;
import chat.app.prod.post.PostRepository;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserCaseService userCaseService;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         CommentRepository commentRepository,
                         PostRepository postRepository,
                         UserCaseService userCaseService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userCaseService = userCaseService;
    }

    public void reportComment(Long commentId,
                              String reporterUsername,
                              String reason,
                              String details) {

        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (reason == null || reason.isBlank()) {
            reason = "Other";
        }

        if (details != null) {
            details = details.trim();
        }

        UserCase userCase = userCaseService.findOrCreateCaseForUser(comment.getUser());

        Report report = new Report(
                reporter,
                null,
                null,
                comment,
                ReportTargetType.COMMENT,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now(),
                userCase
        );

        reportRepository.save(report);
    }

    public void reportPost(Long postId,
                           String reporterUsername,
                           String reason,
                           String details) {

        if (reason == null || reason.isBlank()) {
            reason = "Other";
        }

        if (details != null) {
            details = details.trim();
        }

        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        UserCase userCase = userCaseService.findOrCreateCaseForUser(post.getUser());

        Report report = new Report(
                reporter,
                null,
                post,
                null,
                ReportTargetType.POST,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now(),
                userCase
        );

        reportRepository.save(report);
    }

    public void reportUser(String reportedUsername,
                           String reporterUsername,
                           String reason,
                           String details) {

        if (reason == null || reason.isBlank()) {
            reason = "Other";
        }

        if (details != null) {
            details = details.trim();
        }

        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new RuntimeException("Reporter not found"));

        User reportedUser = userRepository.findByUsername(reportedUsername)
                .orElseThrow(() -> new RuntimeException("Reported user not found"));

        UserCase userCase = userCaseService.findOrCreateCaseForUser(reportedUser);

        Report report = new Report(
                reporter,
                reportedUser,
                null,
                null,
                ReportTargetType.USER,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now(),
                userCase
        );

        reportRepository.save(report);
    }

    public List<Report> getReports(ReportStatus status, ReportTargetType targetType) {
        if (status != null && targetType != null) {
            return reportRepository.findByStatusAndTargetTypeOrderByCreatedAtDesc(status, targetType);
        }

        if (status != null) {
            return reportRepository.findByStatusOrderByCreatedAtDesc(status);
        }

        if (targetType != null) {
            return reportRepository.findByTargetTypeOrderByCreatedAtDesc(targetType);
        }

        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public void reviewReport(Long reportId,
                             String reviewerUsername,
                             ReportStatus status,
                             String conclusion,
                             String actionTaken) {

        Report report = getReportById(reportId);

        User reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new RuntimeException("Reviewer not found"));

        report.setStatus(status);
        report.setReviewedBy(reviewer);
        report.setReviewedAt(LocalDateTime.now());
        report.setConclusion(conclusion);
        report.setActionTaken(actionTaken);

        reportRepository.save(report);
    }
}