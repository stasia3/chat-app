package chat.app.prod.report;

import chat.app.prod.post.Comment;
import chat.app.prod.post.Post;
import chat.app.prod.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User reporter;

    @ManyToOne
    private User reportedUser;

    @ManyToOne
    private Post reportedPost;

    @ManyToOne
    private Comment reportedComment;

    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType;

    private String reason;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.PENDING;

    private LocalDateTime createdAt;

    public Report() {
    }

    public Report(User reporter,
                  User reportedUser,
                  Post reportedPost,
                  Comment reportedComment,
                  ReportTargetType targetType,
                  String reason,
                  String details,
                  ReportStatus status,
                  LocalDateTime createdAt) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reportedPost = reportedPost;
        this.reportedComment = reportedComment;
        this.targetType = targetType;
        this.reason = reason;
        this.details = details;
        this.status = status;
        this.createdAt = createdAt;
    }

    // getters and setters
}