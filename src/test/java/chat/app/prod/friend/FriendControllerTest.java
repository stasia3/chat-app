package chat.app.prod.friend;

import chat.app.prod.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @Test
    void shouldShowFriendsPage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(friendService.searchUsers(null ,"eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/friends").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("friend/friends"))
                .andExpect(model().attribute("title", "Friends"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void shouldShowFriendPageWithSearch() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        User user = new User();
        user.setUsername("ava");

        Mockito.when(friendService.searchUsers("ava", "eva"))
                .thenReturn(List.of(user));

        mockMvc.perform(get("/friends")
                        .principal(authentication)
                        .param("username", "ava"))
                .andExpect(status().isOk())
                .andExpect(view().name("friend/friends"))
                .andExpect(model().attribute("title", "Friends"))
                .andExpect(model().attribute("search", "ava"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void shouldSendFriendRequest() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        mockMvc.perform(post("/friends/request/ava")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/friends"));

        Mockito.verify(friendService).sendFriendRequest("eva", "ava");
    }

    @Test
    void shouldShowNotificationsPage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(friendService.getPendingRequests("eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/notifications").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("friend/notifications"))
                .andExpect(model().attribute("title", "Notifications"))
                .andExpect(model().attributeExists("requests"));
    }

    @Test
    void shouldAcceptFriendRequest() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        mockMvc.perform(post("/notifications/accept/10")
                        .principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        Mockito.verify(friendService).acceptRequest(10L, "eva");
    }

    @Test
    void shouldShowFriendsList() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(friendService.getFriends("eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/friends/list").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("friend/friends-list"))
                .andExpect(model().attribute("title", "My Friends"))
                .andExpect(model().attributeExists("friends"));
    }

}
