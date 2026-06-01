package chat.app.prod.chat;

import chat.app.prod.friend.FriendService;
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

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private ChatService chatService;

    @Test
    void shouldShowUsersPage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(friendService.getFriends("eva"))
                .thenReturn(List.of());

        mockMvc.perform(get("/users").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("friend/users"))
                .andExpect(model().attribute("currentUsername", "eva"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    void shouldShowChatPage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(chatService.getConversation("eva", "ava"))
                .thenReturn(List.of());

        mockMvc.perform(get("/chat/ava").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("chat/chat"))
                .andExpect(model().attribute("currentUsername", "eva"))
                .andExpect(model().attribute("selectedUsername", "ava"))
                .andExpect(model().attributeExists("messages"));
    }

    @Test
    void shouldSendMessage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "passowrd");

        mockMvc.perform(post("/chat/ava")
                        .principal(authentication)
                        .param("content", "Hello Ava"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/ava"));

        Mockito.verify(chatService).sendMessage("eva", "ava", "Hello Ava");
    }

    @Test
    void shouldNotSendEmptyMessage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        mockMvc.perform(post("/chat/ava")
                        .principal(authentication)
                        .param("content", " "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/ava"));

        Mockito.verify(chatService, Mockito.never())
                .sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
