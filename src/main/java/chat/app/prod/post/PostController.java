package chat.app.prod.post;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public String postsPage(Model model, Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("title", "Posts");
        model.addAttribute("posts", postService.getFeedPostCards(username));
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

    @PostMapping("/posts/{postId}/like")
    @ResponseBody
    public LikeResponse toggleLike(@PathVariable Long postId,
                                   Authentication authentication) {
        String username = authentication.getName();

        postService.toggleLike(postId, username);

        return postService.getLikeResponse(postId, username);
    }

    @GetMapping("/posts/{postId}/likes")
    @ResponseBody
    public List<LikedUserDto> likedUsers(@PathVariable Long postId) {
        return postService.getLikedUsers(postId);
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId,
                           Model model,
                           Authentication authentication) {
        String username = authentication.getName();

        model.addAttribute("title", "View Post");
        model.addAttribute("username", username);
        model.addAttribute("postItem", postService.getPostCard(postId, username));
        model.addAttribute("comments", postService.getComments(postId));

        return "post/view-post";
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String content,
                             Authentication authentication) {
        String username = authentication.getName();

        postService.addComment(postId, username, content);

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long commentId,
                                Authentication authentication) {
        String username = authentication.getName();

        postService.deleteComment(commentId, username);

        return "redirect:/posts/" + postId + "#comments";
    }
}