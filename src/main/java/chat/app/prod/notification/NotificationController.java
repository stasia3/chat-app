package chat.app.prod.notification;

import chat.app.prod.friend.FriendService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final FriendService friendService;

    public NotificationController(NotificationService notificationService,
                                  FriendService friendService) {
        this.notificationService = notificationService;
        this.friendService = friendService;
    }

    @GetMapping("/notifications")
    public String notificationsPage(@RequestParam(defaultValue = "ALL") String type,
                                    Model model,
                                    Authentication authentication) {
        String currentUsername = authentication.getName();

        model.addAttribute("title", "Notifications");
        model.addAttribute("activeType", type);
        model.addAttribute("notifications",
                notificationService.getNotifications(currentUsername, type));

        return "notification/notifications";
    }

    @PostMapping("/notifications/{id}/hide")
    public String hideNotification(@PathVariable Long id,
                                   Authentication authentication) {
        notificationService.hideNotification(id, authentication.getName());

        return "redirect:/notifications";
    }

    @PostMapping("/notifications/hide-all")
    public String hideAllNotifications(Authentication authentication) {
        notificationService.hideAllNotifications(authentication.getName());

        return "redirect:/notifications";
    }

    @PostMapping("/notifications/accept/{id}")
    public String acceptRequest(@PathVariable Long id,
                                Authentication authentication) {
        friendService.acceptRequest(id, authentication.getName());
        return "redirect:/friends?tab=requests";
    }

    @PostMapping("/notifications/reject/{id}")
    public String rejectRequest(@PathVariable Long id,
                                Authentication authentication) {
        friendService.rejectRequest(id, authentication.getName());
        return "redirect:/friends?tab=requests";
    }

    @PostMapping("/notifications/settings")
    public String updateNotificationSettings(
            @RequestParam(required = false) boolean notifyFriendPosts,
            @RequestParam(required = false) boolean notifyPostComments,
            @RequestParam(required = false) boolean notifyPostLikes,
            @RequestParam(required = false) boolean notifyCommentDeleted,
            @RequestParam(required = false) boolean notifyFriendRequests,
            Authentication authentication) {

        notificationService.updateSettings(
                authentication.getName(),
                notifyFriendPosts,
                notifyPostComments,
                notifyPostLikes,
                notifyCommentDeleted,
                notifyFriendRequests
        );

        return "redirect:/profile";
    }
}