package chat.app.prod.report;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final UserCaseService userCaseService;
    private final BlockRequestService blockRequestService;

    public ReportController(ReportService reportService,
                            UserCaseService userCaseService,
                            BlockRequestService blockRequestService) {
        this.reportService = reportService;
        this.userCaseService = userCaseService;
        this.blockRequestService = blockRequestService;
    }

    // USER creates reports

    @PostMapping("/report/comments/{commentId}")
    public String reportComment(@PathVariable Long commentId,
                                @RequestParam String reason,
                                @RequestParam(required = false) String details,
                                @RequestParam Long postId,
                                Authentication authentication) {

        reportService.reportComment(commentId, authentication.getName(), reason, details);

        return "redirect:/posts/" + postId + "#comments";
    }

    @PostMapping("/report/posts/{postId}")
    public String reportPost(@PathVariable Long postId,
                             @RequestParam String reason,
                             @RequestParam(required = false) String details,
                             Authentication authentication) {

        reportService.reportPost(postId, authentication.getName(), reason, details);

        return "redirect:/posts";
    }

    @PostMapping("/report/users/{username}")
    public String reportUser(@PathVariable String username,
                             @RequestParam String reason,
                             @RequestParam(required = false) String details,
                             Authentication authentication) {

        reportService.reportUser(username, authentication.getName(), reason, details);

        return "redirect:/profile/" + username;
    }

    // REPORTER pages

    @GetMapping("/reports")
    public String reports(@RequestParam(required = false) ReportStatus status,
                          @RequestParam(required = false) ReportTargetType targetType,
                          Model model) {

        model.addAttribute("reports", reportService.getReports(status, targetType));

        model.addAttribute("statuses", ReportStatus.values());
        model.addAttribute("targetTypes", ReportTargetType.values());

        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedTargetType", targetType);

        model.addAttribute("title", "Reports");

        return "reports/reports";
    }

    @GetMapping("/reports/{id}")
    public String reportDetails(@PathVariable Long id, Model model) {

        model.addAttribute("report", reportService.getReportById(id));
        model.addAttribute("statuses", ReportStatus.values());
        model.addAttribute("title", "Report details");

        return "reports/report-details";
    }

    @PostMapping("/reports/{id}/review")
    public String reviewReport(@PathVariable Long id,
                               @RequestParam ReportStatus status,
                               @RequestParam String conclusion,
                               @RequestParam(required = false) String actionTaken,
                               Authentication authentication) {

        reportService.reviewReport(
                id,
                authentication.getName(),
                status,
                conclusion,
                actionTaken
        );

        return "redirect:/reports/" + id;
    }

    @GetMapping("/reports/cases")
    public String userCases(@RequestParam(required = false) String search,
                            Model model) {

        model.addAttribute("cases", userCaseService.getCases(search));
        model.addAttribute("search", search);
        model.addAttribute("title", "User cases");

        return "reports/user-cases";
    }

    @GetMapping("/reports/cases/{id}")
    public String userCaseDetails(@PathVariable Long id, Model model) {
        UserCase userCase = userCaseService.getCaseById(id);

        model.addAttribute("userCase", userCase);
        model.addAttribute("reports", userCase.getReports());
        model.addAttribute("title", "User case");

        return "reports/user-case-details";
    }

    @GetMapping("/reports/cases/{id}/block-request")
    public String blockRequestPage(@PathVariable Long id, Model model) {
        UserCase userCase = userCaseService.getCaseById(id);

        model.addAttribute("userCase", userCase);
        model.addAttribute("title", "Block request");

        return "reports/block-request";
    }

    @PostMapping("/reports/cases/{id}/block-request")
    public String createBlockRequest(@PathVariable Long id,
                                     @RequestParam String reason,
                                     Authentication authentication) {

        blockRequestService.createBlockRequest(
                id,
                authentication.getName(),
                reason
        );

        return "redirect:/reports/cases/" + id;
    }


}