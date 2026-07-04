package chat.app.prod.report;

import chat.app.prod.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_blocks")
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who was blocked
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Admin who blocked the user
    @ManyToOne(optional = false)
    @JoinColumn(name = "blocked_by_id", nullable = false)
    private User blockedBy;

    @ManyToOne
    @JoinColumn(name = "block_request_id")
    private BlockRequest blockRequest;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;

    @Column(nullable = false)
    private boolean active = true;

    public UserBlock() {
    }

    public UserBlock(User user,
                     User blockedBy,
                     BlockRequest blockRequest,
                     String reason,
                     LocalDateTime blockedAt,
                     boolean active) {
        this.user = user;
        this.blockedBy = blockedBy;
        this.blockRequest = blockRequest;
        this.reason = reason;
        this.blockedAt = blockedAt;
        this.active = active;
    }

    @PrePersist
    public void onCreate() {
        if (blockedAt == null) {
            blockedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(User blockedBy) {
        this.blockedBy = blockedBy;
    }

    public BlockRequest getBlockRequest() {
        return blockRequest;
    }

    public void setBlockRequest(BlockRequest blockRequest) {
        this.blockRequest = blockRequest;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}