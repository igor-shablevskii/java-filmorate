package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping("/{id}")
    public Review get(@PathVariable Integer id) {
        Review review = reviewService.getReviewById(id);
        log.info("Get review by id = {}", review.getReviewId());
        return review;
    }

    @GetMapping
    public List<Review> getAll(@RequestParam(defaultValue = "10", required = false) Integer count,
                                   @RequestParam(required = false) Integer filmId) {
        List<Review> reviews = reviewService.getReviews(count, filmId);
        log.info("Get all reviews, count = {}", reviews.size());
        return reviews;
    }

    @PostMapping
    public Review create(@RequestBody @Valid Review review) {
        Review savedReview = reviewService.save(review);
        log.info("Review {} created and added in storage", savedReview);
        return savedReview;
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        Review updatedReview = reviewService.update(review);
        log.info("Review {} updated in storage", review);
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        log.info("Review by id = {} deleted in storage", id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.saveLike(id, userId);
        log.info("User {} likes review id {}", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.saveDislike(id, userId);
        log.info("User {} doesn't like review id {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.deleteLike(id, userId);
        log.info("User {} deleted like", userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.deleteDislike(id, userId);
        log.info("User {} deleted dislike", userId);
    }
}


