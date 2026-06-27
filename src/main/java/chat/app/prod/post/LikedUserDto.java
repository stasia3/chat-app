package chat.app.prod.post;

public class LikedUserDto {

    private final String username;

    public LikedUserDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}