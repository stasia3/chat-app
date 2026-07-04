package chat.app.prod.report;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCaseRepository extends JpaRepository<UserCase, Long> {

    Optional<UserCase> findByUser(User user);

    Optional<UserCase> findByUserUsername(String username);
}