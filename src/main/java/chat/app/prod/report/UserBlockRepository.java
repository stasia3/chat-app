package chat.app.prod.report;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    List<UserBlock> findByUserOrderByBlockedAtDesc(User user);

    List<UserBlock> findByActiveTrueOrderByBlockedAtDesc();

    Optional<UserBlock> findByUserAndActiveTrue(User user);

    boolean existsByUserAndActiveTrue(User user);

    List<UserBlock> findByActiveTrueAndUserUsernameContainingIgnoreCaseOrderByBlockedAtDesc(String username);


}