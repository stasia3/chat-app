package chat.app.prod.auth;


import chat.app.prod.profile.Profile;
import chat.app.prod.profile.ProfileRepository;
import chat.app.prod.user.User;
import chat.app.prod.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProfileRepository profileRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void shouldShowRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        Mockito.when(userRepository.findByUsername("eva"))
                .thenReturn(Optional.empty());

        Mockito.when(userRepository.findByEmail("eva@gmail.com"))
                .thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded-password");

        User savedUser = new User();
        savedUser.setUsername("eva");
        savedUser.setEmail("eva@gmail.com");
        savedUser.setPassword("encoded-password");

        Mockito.when(userRepository.save(any(User.class)))
                        .thenReturn(savedUser);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "eva")
                        .param("email", "eva@gmail.com")
                        .param("password", "Password123")
                        .param("passwordCheck", "Password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Mockito.verify(userRepository).save(any(User.class));
        Mockito.verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void shouldNotRegisterExistingUser() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("eva");

        Mockito.when(userRepository.findByUsername("eva"))
                .thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "eva")
                        .param("email", "eva@gmail.com")
                        .param("password", "Password123")
                        .param("passwordCheck", "Password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("error"));

        Mockito.verify(userRepository, Mockito.never()).save(any(User.class));
        Mockito.verify(profileRepository, Mockito.never()).save(any(Profile.class));
    }
}
