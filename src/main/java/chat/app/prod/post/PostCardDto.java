package chat.app.prod.post;

public class PostCardDto {

    private final Post post;
    private final long likeCount;
    private final boolean likedByCurrentUser;

    public PostCardDto(Post post, long likeCount, boolean likedByCurrentUser) {
        this.post = post;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
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
}