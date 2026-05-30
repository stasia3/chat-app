package chat.app.prod.profile;

import chat.app.prod.service.FriendService;
import chat.app.prod.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private PostService postService;

    @Test
    void shouldShowProfilePageForAuthenticatedUser() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(friendService.countFriends("eva"))
                .thenReturn(3);

        Mockito.when(postService.getPostsByUsername("eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/profile").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/profile"))
                .andExpect(model().attribute("title", "My Profile"))
                .andExpect(model().attribute("username", "eva"))
                .andExpect(model().attribute("friendCount", 3))
                .andExpect(model().attributeExists("posts"));
    }
}
