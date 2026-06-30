package chat.app.prod.friend;

import chat.app.prod.user.User;

public class FriendUserDto {

    private final User user;
    private final String profileImageUrl;

    public FriendUserDto(User user, String profileImageUrl) {
        this.user = user;
        this.profileImageUrl = profileImageUrl;
    }

    public User getUser() {
        return user;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}