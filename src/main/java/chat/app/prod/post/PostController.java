package chat.app.prod.post;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public String postsPage(Model model) {
        model.addAttribute("title", "Posts");
        model.addAttribute("posts", postService.getAllPosts());
        return "post/posts";
    }

    @GetMapping("/posts/new")
    public String newPostPage(Model model) {
        model.addAttribute("title", "Add Post");
        return "post/new-post";
    }

    @PostMapping("/posts/new")
    public String createPost(
            @RequestParam String content,
            @RequestParam(required = false) String languageTag,
            @RequestParam(required = false) String customLanguageTag,
            @RequestParam PostVisibility visibility,
            Authentication authentication) {

        String username = authentication.getName();
        String finalLanguageTag = languageTag;

        if (customLanguageTag != null && !customLanguageTag.trim().isEmpty()) {
            finalLanguageTag = customLanguageTag.trim();
        }

        postService.createPost(
                username,
                content,
                finalLanguageTag,
                visibility
        );
        return "redirect:/profile";
    }
}