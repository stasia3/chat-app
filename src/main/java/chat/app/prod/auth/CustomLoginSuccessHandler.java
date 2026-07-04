package chat.app.prod.auth;

import chat.app.prod.report.UserBlockService;
import chat.app.prod.user.Role;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserBlockService userBlockService;

    public CustomLoginSuccessHandler(UserRepository userRepository,
                                     UserBlockService userBlockService) {
        this.userRepository = userRepository;
        this.userBlockService = userBlockService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userBlockService.isUserBlockedByUsername(user.getUsername())) {
            response.sendRedirect("/account-blocked");
            return;
        }

        if (user.getRole() == Role.REPORTER) {
            response.sendRedirect("/reports");
            return;
        }

        if (user.getRole() == Role.ADMIN) {
            response.sendRedirect("/admin/block-requests");
            return;
        }



        response.sendRedirect("/profile");
    }
}