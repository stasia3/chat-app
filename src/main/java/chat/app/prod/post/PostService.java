package chat.app.prod.post;

import chat.app.prod.friend.FriendService;
import chat.app.prod.notification.NotificationService;
import chat.app.prod.notification.NotificationType;
import chat.app.prod.profile.ProfileRepository;
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
    private final ProfileRepository profileRepository;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository, UserRepository userRepository, FriendService friendService, PostLikeRepository postLikeRepository, CommentRepository commentRepository, ProfileRepository profileRepository, NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.friendService = friendService;
        this.postLikeRepository = postLikeRepository;
        this.commentRepository = commentRepository;
        this.profileRepository = profileRepository;
        this.notificationService = notificationService;
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

        friendService.getFriends(username).forEach(friend ->
                notificationService.createNotification(
                        friend,
                        user,
                        NotificationType.FRIEND_POST,
                        user.getUsername() + " published a new post.",
                        post,
                        null,
                        null
                )
        );
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
                        () -> {
                            PostLike like = new PostLike(post, user, LocalDateTime.now());
                            postLikeRepository.save(like);

                            notificationService.createNotification(
                                    post.getUser(),
                                    user,
                                    NotificationType.POST_LIKE,
                                    user.getUsername() + " liked your post.",
                                    post,
                                    null,
                                    null
                            );
                        }
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
                        commentRepository.countByPost(post),
                        getAuthorProfileImageUrl(post)
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
                .map(like -> {
                    String username = like.getUser().getUsername();

                    String imageUrl = profileRepository.findByUserUsername(username)
                            .map(profile -> profile.getProfileImageUrl())
                            .orElse(null);

                    return new LikedUserDto(username, imageUrl);
                })
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
                commentRepository.countByPost(post),
                getAuthorProfileImageUrl(post)
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
        String formattedContent = formatCommentContent(content.trim());

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment(
                post,
                user,
                formattedContent,
                LocalDateTime.now()
        );

        commentRepository.save(comment);

        notificationService.createNotification(
                post.getUser(),
                user,
                NotificationType.POST_COMMENT,
                user.getUsername() + " commented on your post.",
                post,
                comment,
                null
        );
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        boolean isCommentOwner = comment.getUser().getUsername().equals(username);
        boolean isPostOwner = comment.getPost().getUser().getUsername().equals(username);

        if (!isCommentOwner && !isPostOwner) {
            return;
        }

        if (isPostOwner && !isCommentOwner) {
            comment.setDeleted(true);
            comment.setDeletedByPostAuthor(true);
            commentRepository.save(comment);


            notificationService.createNotification(
                    comment.getUser(),
                    comment.getPost().getUser(),
                    NotificationType.COMMENT_DELETED,
                    comment.getPost().getUser().getUsername() + " deleted your comment.",
                    comment.getPost(),
                    comment,
                    null
            );
            return;
        }

        commentRepository.delete(comment);
    }
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            return;
        }

        postRepository.delete(post);
    }

    public List<PostCardDto> getFilteredFeedPostCards(String username,
                                                      String keyword,
                                                      String visibility,
                                                      String language) {
        List<PostCardDto> posts = getFeedPostCards(username);

        return posts.stream()
                .filter(postCard -> {
                    Post post = postCard.getPost();

                    boolean matchesKeyword =
                            keyword == null ||
                                    keyword.trim().isEmpty() ||
                                    post.getContent().toLowerCase().contains(keyword.trim().toLowerCase());

                    boolean matchesVisibility =
                            visibility == null ||
                                    visibility.equals("ALL") ||
                                    post.getVisibility().name().equals(visibility);

                    boolean matchesLanguage =
                            language == null ||
                                    language.equals("ALL") ||
                                    (post.getLanguageTag() != null &&
                                            post.getLanguageTag().equalsIgnoreCase(language));

                    return matchesKeyword && matchesVisibility && matchesLanguage;
                })
                .toList();
    }

    private String getAuthorProfileImageUrl(Post post) {
        return profileRepository.findByUserUsername(post.getUser().getUsername())
                .map(profile -> profile.getProfileImageUrl())
                .orElse(null);
    }

    public List<CommentDto> getCommentDtos(Long postId) {
        return getComments(postId)
                .stream()
                .map(comment -> {
                    String imageUrl = profileRepository
                            .findByUserUsername(comment.getUser().getUsername())
                            .map(profile -> profile.getProfileImageUrl())
                            .orElse(null);

                    return new CommentDto(comment, imageUrl);
                })
                .toList();
    }

    private String formatCommentContent(String content) {
        String escaped = content
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        return escaped
                .replaceAll(
                        "(?s)```(java|javascript|python|html|css)?\\R(.*?)\\R```",
                        "<pre><code class=\"language-$1\">$2</code></pre>"
                )
                .replace("\n", "<br>");
    }
}