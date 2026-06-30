package chat.app.prod.post;

public class CommentDto {

    private final Comment comment;
    private final String authorProfileImageUrl;

    public CommentDto(Comment comment, String authorProfileImageUrl) {
        this.comment = comment;
        this.authorProfileImageUrl = authorProfileImageUrl;
    }

    public Comment getComment() {
        return comment;
    }

    public String getAuthorProfileImageUrl() {
        return authorProfileImageUrl;
    }
}