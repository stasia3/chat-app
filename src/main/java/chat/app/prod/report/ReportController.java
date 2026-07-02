
package chat.app.prod.report;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/reports/comments/{commentId}")
    public String reportComment(@PathVariable Long commentId,
                                @RequestParam String reason,
                                @RequestParam(required = false) String details,
                                @RequestParam Long postId,
                                Authentication authentication) {

        reportService.reportComment(
                commentId,
                authentication.getName(),
                reason,
                details
        );

        return "redirect:/posts/" + postId + "#comments";
    }

    @PostMapping("/reports/posts/{postId}")
    public String reportPost(@PathVariable Long postId,
                             @RequestParam String reason,
                             @RequestParam(required = false) String details,
                             Authentication authentication) {

        reportService.reportPost(
                postId,
                authentication.getName(),
                reason,
                details
        );

        return "redirect:/posts";
    }

    @PostMapping("/reports/users/{username}")
    public String reportUser(@PathVariable String username,
                             @RequestParam String reason,
                             @RequestParam(required = false) String details,
                             Authentication authentication) {

        reportService.reportUser(
                username,
                authentication.getName(),
                reason,
                details
        );

        return "redirect:/profile/" + username;
    }
}