package chat.app.prod.post;

public class PostCardDto {

    private final Post post;
    private final long likeCount;
    private final boolean likedByCurrentUser;
    private final long commentCount;
    private final String authorProfileImageUrl;

    public PostCardDto(Post post,
                       long likeCount,
                       boolean likedByCurrentUser,
                       long commentCount,
                       String authorProfileImageUrl) {
        this.post = post;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.commentCount = commentCount;
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    public Post getPost() {
        return post;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }
}