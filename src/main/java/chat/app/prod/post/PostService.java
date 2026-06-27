package chat.app.prod.post;

import chat.app.prod.friend.FriendService;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, FriendService friendService, PostLikeRepository postLikeRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.friendService = friendService;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
    }

    public void createPost(String username, String content,  String languageTag, PostVisibility visibility) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post(
                user,
                content.trim(),
                languageTag,
                visibility,
                LocalDateTime.now());
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Post> getFeedPosts(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> friends = friendService.getFriends(username);

        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(post ->
                        post.getVisibility() == PostVisibility.PUBLIC
                || post.getUser().getId().equals(currentUser.getId())
                || friends.stream()
                                .anyMatch(friend -> friend.getId().equals(post.getUser().getId()))
                ).toList();
    }

    public void toggleLike(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        postLikeRepository.findByPostAndUser(post, user)
                .ifPresentOrElse(
                        postLikeRepository::delete,
                        () -> postLikeRepository.save(
                                new PostLike(post, user, LocalDateTime.now())
                        )
                );
    }

    public long countLikes(Post post) {
        return postLikeRepository.countByPost(post);
    }

    public boolean isLikedByUser(Post post, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return postLikeRepository.existsByPostAndUser(post, user);
    }

    public List<PostCardDto> toPostCardDtos(List<Post> posts, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return posts.stream()
                .map(post -> new PostCardDto(
                        post,
                        postLikeRepository.countByPost(post),
                        postLikeRepository.existsByPostAndUser(post, currentUser),
                        commentRepository.countByPost(post)
                ))
                .toList();
    }

    public List<PostCardDto> getFeedPostCards(String username) {
        return toPostCardDtos(getFeedPosts(username), username);
    }

    public List<PostCardDto> getMyPostCards(String username) {
        return toPostCardDtos(getPostsByUsername(username), username);
    }

    public LikeResponse getLikeResponse(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long likeCount = postLikeRepository.countByPost(post);
        boolean likedByCurrentUser = postLikeRepository.existsByPostAndUser(post, user);

        return new LikeResponse(likeCount, likedByCurrentUser);
    }

    public List<LikedUserDto> getLikedUsers(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return postLikeRepository.findByPost(post)
                .stream()
                .map(like -> new LikedUserDto(like.getUser().getUsername()))
                .toList();
    }

    public PostCardDto getPostCard(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new PostCardDto(
                post,
                postLikeRepository.countByPost(post),
                postLikeRepository.existsByPostAndUser(post, currentUser),
                commentRepository.countByPost(post)
        );
    }

    public List<Comment> getComments(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    public void addComment(Long postId, String username, String content) {
        if (content == null || content.trim().isEmpty()) {
            return;
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment(
                post,
                user,
                content.trim(),
                LocalDateTime.now()
        );

        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isCommentOwner = comment.getUser().getUsername().equals(username);
        boolean isPostOwner = comment.getPost().getUser().getUsername().equals(username);

        if (!isCommentOwner && !isPostOwner) {
            return;
        }

        commentRepository.delete(comment);
    }
}