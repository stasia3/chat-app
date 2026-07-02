package chat.app.prod.report;

import chat.app.prod.post.Comment;
import chat.app.prod.post.CommentRepository;
import chat.app.prod.post.Post;
import chat.app.prod.post.PostRepository;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         CommentRepository commentRepository,
                         PostRepository postRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
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

        Report report = new Report(
                reporter,
                null,
                null,
                comment,
                ReportTargetType.COMMENT,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now()
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

        Report report = new Report(
                reporter,
                null,
                post,
                null,
                ReportTargetType.POST,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now()
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

        Report report = new Report(
                reporter,
                reportedUser,
                null,
                null,
                ReportTargetType.USER,
                reason,
                details,
                ReportStatus.PENDING,
                LocalDateTime.now()
        );

        reportRepository.save(report);
    }
}