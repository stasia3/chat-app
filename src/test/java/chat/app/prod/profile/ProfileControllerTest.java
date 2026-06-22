package chat.app.prod.profile;

import chat.app.prod.friend.FriendService;
import chat.app.prod.post.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.of;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private ProfileRepository profileRepository;

    @MockitoBean
    private PostService postService;

    @Test
    void shouldShowProfilePageForAuthenticatedUser() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Profile profile = new Profile();
        profile.setFirstName("Eva");
        profile.setHeadline("Junior Java Developer");

        Mockito.when(profileRepository.findByUserUsername("eva"))
                        .thenReturn(of(profile));

        Mockito.when(friendService.countFriends("eva"))
                .thenReturn(3);

        Mockito.when(postService.getPostsByUsername("eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/profile").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/profile"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attribute("title", "My Profile"))
                .andExpect(model().attribute("username", "eva"))
                .andExpect(model().attribute("friendCount", 3))
                .andExpect(model().attributeExists("posts"));
    }

    @Test
    void shouldShowEditProfilePage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Profile profile = new Profile();
        profile.setFirstName("Eva");

        Mockito.when(profileRepository.findByUserUsername("eva"))
                .thenReturn(Optional.of(profile));

        mockMvc.perform(get("/profile/edit").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit-profile"))
                .andExpect(model().attribute("title", "Edit Profile"))
                .andExpect(model().attributeExists("profile"));
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Profile profile = new Profile();

        Mockito.when(profileRepository.findByUserUsername("eva"))
                .thenReturn(Optional.of(profile));

        mockMvc.perform(post("/profile/edit")
                        .principal(authentication)
                        .with(csrf())
                        .param("firstName", "Eva")
                        .param("lastName", "Green")
                        .param("headline", "Junior Java Developer")
                        .param("bio", "I like building Java web apps.")
                        .param("programmingLanguages", "Java, Spring Boot, SQL")
                        .param("githubLink", "https://github.com/eva")
                        .param("linkedinLink", "https://linkedin.com/in/eva"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(profileRepository).save(any(Profile.class));
    }
}
