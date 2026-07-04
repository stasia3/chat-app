package chat.app.prod.report;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCaseRepository extends JpaRepository<UserCase, Long> {

    Optional<UserCase> findByUser(User user);

    Optional<UserCase> findByUserUsername(String username);

    List<UserCase> findByUserUsernameContainingIgnoreCaseOrderByUpdatedAtDesc(String username);

    List<UserCase> findAllByOrderByUpdatedAtDesc();
}