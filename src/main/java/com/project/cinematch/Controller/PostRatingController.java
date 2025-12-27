package com.project.cinematch.Controller;

import com.project.cinematch.DTO.PostRatingSummaryDTO;
import com.project.cinematch.DTO.RatePostDTO;
import com.project.cinematch.Model.PostRating;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.PostRatingRepository;
import com.project.cinematch.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum")
public class PostRatingController {

    private final PostRatingRepository ratingRepo;
    private final UserRepository userRepository;

    public PostRatingController(PostRatingRepository ratingRepo, UserRepository userRepository) {
        this.ratingRepo = ratingRepo;
        this.userRepository = userRepository;
    }

    @PutMapping("/posts/{postId}/rating")
    public ResponseEntity<?> ratePost(@PathVariable Long postId,
                                      @RequestBody RatePostDTO dto,
                                      Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        Integer rating = dto.getRating();
        if (rating == null || rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body("Rating must be 1..5");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PostRating pr = ratingRepo.findByPostIdAndUserId(postId, user.getId()).orElse(null);
        if (pr == null) {
            pr = new PostRating();
            pr.setPostId(postId);
            pr.setUserId(user.getId());
        }

        pr.setRating(rating);
        ratingRepo.save(pr);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/{postId}/rating")
    public ResponseEntity<PostRatingSummaryDTO> getSummary(@PathVariable Long postId,
                                                           Authentication authentication) {

        Long userId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) userId = user.getId();
        }

        PostRatingRepository.Agg agg = ratingRepo.aggForPost(postId);
        double avg = (agg == null || agg.getAvgRating() == null) ? 0.0 : agg.getAvgRating();
        long cnt = (agg == null || agg.getCnt() == null) ? 0L : agg.getCnt();

        Integer my = null;
        if (userId != null) {
            my = ratingRepo.myRating(postId, userId);
        }

        return ResponseEntity.ok(new PostRatingSummaryDTO(avg, cnt, my));
    }
}
