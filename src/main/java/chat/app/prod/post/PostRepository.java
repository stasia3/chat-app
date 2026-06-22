package chat.app.prod.post;

import chat.app.prod.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findByUserOrderByCreatedAtDesc(User user);
    List<Post> findByVisibilityOrderByCreatedAtDesc(PostVisibility visibility);
}