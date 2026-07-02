package chat.app.prod.post;

public class LikedUserDto {

    private final String username;
    private final String profileImageUrl;

    public LikedUserDto(String username, String profileImageUrl) {
        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}