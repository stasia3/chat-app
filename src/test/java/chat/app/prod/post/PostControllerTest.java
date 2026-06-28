package chat.app.prod.post;

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

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void shouldShowPostsPage() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        Mockito.when(postService.getFilteredFeedPostCards(
                        "eva",
                        null,
                        "ALL",
                        "ALL"
                ))
                .thenReturn(List.of());

        mockMvc.perform(get("/posts")
                        .principal(authentication)
                        .requestAttr("_csrf", new org.springframework.security.web.csrf.DefaultCsrfToken(
                                "X-CSRF-TOKEN",
                                "_csrf",
                                "test-token"
                        )))
                .andExpect(status().isOk())
                .andExpect(view().name("post/posts"))
                .andExpect(model().attribute("title", "Posts"))
                .andExpect(model().attributeExists("posts"))
                .andExpect(model().attribute("visibility", "ALL"))
                .andExpect(model().attribute("language", "ALL"));
    }
    @Test
    void shouldShowNewPostPage() throws Exception {
        mockMvc.perform(get("/posts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/new-post"))
                .andExpect(model().attribute("title", "Add Post"));
    }

    @Test
    void shouldCreatePost() throws Exception {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("eva", "password");

        mockMvc.perform(post("/posts/new")
                        .principal(authentication)
                        .param("content", "My first post")
                        .param("languageTag", "Java")
                        .param("visibility", "PUBLIC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Mockito.verify(postService).createPost(
                "eva",
                "My first post",
                "Java",
                PostVisibility.PUBLIC
        );
    }
}