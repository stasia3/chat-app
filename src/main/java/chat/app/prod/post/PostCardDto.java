package chat.app.prod.post;

public class PostCardDto {

    private final Post post;
    private final long likeCount;
    private final boolean likedByCurrentUser;
    private final long commentCount;

    public PostCardDto(Post post, long likeCount, boolean likedByCurrentUser, long commentCount) {
        this.post = post;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
        this.commentCount = commentCount;
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
}