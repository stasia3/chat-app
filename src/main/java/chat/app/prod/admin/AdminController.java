package chat.app.prod.admin;

import chat.app.prod.report.BlockRequestService;
import chat.app.prod.report.BlockRequestStatus;
import chat.app.prod.report.UserBlockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final BlockRequestService blockRequestService;
    private final UserBlockService userBlockService;

    public AdminController(BlockRequestService blockRequestService,
                           UserBlockService userBlockService) {
        this.blockRequestService = blockRequestService;
        this.userBlockService = userBlockService;
    }

    @GetMapping("/admin/block-requests")
    public String blockRequests(@RequestParam(required = false) BlockRequestStatus status,
                                Model model) {

        model.addAttribute("requests", blockRequestService.getBlockRequestsByStatus(status));
        model.addAttribute("statuses", BlockRequestStatus.values());
        model.addAttribute("selectedStatus", status);

        return "admin/block-requests";
    }

    @GetMapping("/admin/blocked-users")
    public String blockedUsers(@RequestParam(required = false) String search,
                               Model model) {

        model.addAttribute("blocks", userBlockService.getActiveBlocks(search));
        model.addAttribute("search", search);

        return "admin/blocked-users";
    }

    @PostMapping("/admin/blocked-users/{userId}/unblock")
    public String unblockUser(@PathVariable Long userId) {
        userBlockService.unblockUser(userId);

        return "redirect:/admin/blocked-users";
    }
}