package chat.app.prod.profile;

import chat.app.prod.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String firstName;
    private String lastName;
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private String programmingLanguages;
    private String githubLink;
    private String linkedinLink;

     public Profile() {}

    public Long getId() {
         return id;
    }

    public void setId(Long id) {
         this.id = id;
    }

    public User getUser() {
         return user;
    }

    public void setUser(User user) {
         this.user = user;
    }

    public String getFirstName() {
         return firstName;
    }

    public void setFirstName(String firstName) {
         this.firstName = firstName;
    }

    public String getLastName() {
         return lastName;
    }

    public void setLastName(String lastName) {
         this.lastName = lastName;
    }

    public String getHeadline() {
         return headline;
    }

    public void setHeadline(String headline) {
         this.headline = headline;
    }

    public String getBio() {
         return bio;
    }

    public void setBio(String bio) {
         this.bio = bio;
    }

    public String getProgrammingLanguages() {
         return programmingLanguages;
    }

    public void setProgrammingLanguages(String progLang) {
         this.programmingLanguages = progLang;
    }

    public String getGithubLink() {
         return githubLink;
    }

    public void setGithubLink(String githubLink) {
         this.githubLink = githubLink;
    }

    public String getLinkedinLink() {
         return linkedinLink;
    }

    public void setLinkedinLink(String linkedinLink) {
         this.linkedinLink = linkedinLink;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
