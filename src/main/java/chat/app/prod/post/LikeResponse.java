package chat.app.prod.post;

public class LikeResponse {

    private long likeCount;
    private boolean likedByCurrentUser;

    public LikeResponse(long likeCount, boolean likedByCurrentUser) {
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
}