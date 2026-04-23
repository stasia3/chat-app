package chat.app.prod.controller;

import chat.app.prod.service.PostService;
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
        return "posts";
    }

    @GetMapping("/posts/new")
    public String newPostPage(Model model) {
        model.addAttribute("title", "Add Post");
        return "new-post";
    }

    @PostMapping("/posts/new")
    public String createPost(@RequestParam String content, Authentication authentication) {
        String username = authentication.getName();
        postService.createPost(username, content);
        return "redirect:/profile";
    }
}