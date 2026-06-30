package chat.app.prod.friend;

public class FriendRequestDto {

    private final FriendRequest request;
    private final String profileImageUrl;

    public FriendRequestDto(FriendRequest request, String profileImageUrl) {
        this.request = request;
        this.profileImageUrl = profileImageUrl;
    }

    public FriendRequest getRequest() {
        return request;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}