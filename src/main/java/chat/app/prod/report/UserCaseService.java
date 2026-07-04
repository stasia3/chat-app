package chat.app.prod.report;

import chat.app.prod.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserCaseService {

    private final UserCaseRepository userCaseRepository;

    public UserCaseService(UserCaseRepository userCaseRepository) {
        this.userCaseRepository = userCaseRepository;
    }

    public UserCase findOrCreateCaseForUser(User user) {
        return userCaseRepository.findByUser(user)
                .orElseGet(() -> userCaseRepository.save(
                        new UserCase(
                                user,
                                UserCaseStatus.OPEN,
                                LocalDateTime.now(),
                                LocalDateTime.now()
                        )
                ));
    }

    public List<UserCase> getAllCases() {
        return userCaseRepository.findAll();
    }

    public UserCase getCaseById(Long id) {
        return userCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User case not found"));
    }
}