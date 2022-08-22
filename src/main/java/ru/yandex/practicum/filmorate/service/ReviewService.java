package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.ReactionDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewDbStorage reviewDao;
    private final UserDao userDao;
    private final ReactionDao reactionDao;
    private final FilmDao filmDao;
    private final FeedDao feedDao;

    @Autowired
    public ReviewService(ReviewDbStorage reviewDao,
                         UserDao userDao,
                         ReactionDao reactionDao,
                         FilmDao filmDao,
                         FeedDao feedDao) {
        this.reviewDao = reviewDao;
        this.userDao = userDao;
        this.reactionDao = reactionDao;
        this.filmDao = filmDao;
        this.feedDao = feedDao;
    }

    public Review save(Review review) {
        review.setUseful(0);
        isUserExists(review.getUserId());
        isFilmExists(review.getFilmId());
        Review savedReview = reviewDao.save(review);
        Feed feed = new Feed(savedReview.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        feedDao.create(feed);
        return savedReview;
    }

    public Review update(Review review) {
        isUserExists(review.getUserId());
        isFilmExists(review.getFilmId());
        isReviewExists(review.getReviewId());
        Review updatedReview = reviewDao.update(review);
        Feed feed = new Feed(updatedReview.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getReviewId());
        feedDao.create(feed);
        return updatedReview;
    }

    public List<Review> getReviews(Integer count, Integer filmId) {
        List<Review> reviews;
        if (filmId != null) {
            isFilmExists(filmId);
            reviews = reviewDao.getReviewsByFilmId(count, filmId);
        } else {
            reviews = reviewDao.getReviews(count);
        }
        reviews.forEach(review ->
                addReactionForReview(review, reactionDao.getReactions(review.getReviewId())));
        reviews.forEach(review -> review.setUseful(calculateUseful(review)));
        return reviews.stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Review getReviewById(int reviewId) {
        isReviewExists(reviewId);
        Review review = reviewDao.getReviewById(reviewId);
        List<Reaction> reactions = reactionDao.getReactions(reviewId);
        addReactionForReview(review, reactions);
        review.setUseful(calculateUseful(review));
        return review;
    }

    public void deleteReview(int reviewId) {
        isReviewExists(reviewId);
        Review review = getReviewById(reviewId);
        Feed feed = new Feed(review.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId);
        feedDao.create(feed);
        reviewDao.deleteReview(reviewId);
    }

    public void saveLike(int reviewId, int userId) {
        isReviewExists(reviewId);
        isUserExists(userId);
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() + 1);
        reviewDao.updateUseful(reviewId, review.getUseful());
        reactionDao.saveLike(reviewId, userId);
    }

    public void saveDislike(int reviewId, int userId) {
        isReviewExists(reviewId);
        isUserExists(userId);
        Review review = reviewDao.getReviewById(reviewId);
        review.setUseful(review.getUseful() - 1);
        reviewDao.updateUseful(reviewId, review.getUseful());
        reactionDao.saveDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        isReactionExists(reviewId, userId);
        reactionDao.deleteReaction(reviewId, userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.updateUseful(reviewId, review.getUseful());
        review.setUseful(review.getUseful() - 1);
    }

    public void deleteDislike(int reviewId, int userId) {
        isReactionExists(reviewId, userId);
        reactionDao.deleteReaction(reviewId, userId);
        Review review = reviewDao.getReviewById(reviewId);
        reviewDao.updateUseful(reviewId, review.getUseful());
        review.setUseful(review.getUseful() + 1);
    }

    private void isUserExists(Integer userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    private void isFilmExists(Integer filmId) {
        if (!filmDao.containsInStorage(filmId)) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }
    }

    private void isReviewExists(Integer reviewId) {
        if (!reviewDao.containsInStorage(reviewId)) {
            throw new NotFoundException(String.format("Review with id = %d not found", reviewId));
        }
    }

    private void isReactionExists(Integer reviewId, Integer userId) {
        if (!reactionDao.containsReactionInStorage(reviewId, userId)) {
            throw new NotFoundException("Reaction on review not found");
        }
    }

    private void addReactionForReview(Review review, List<Reaction> reactions) {
        reactions.forEach(reaction -> review.getUserReactions().add(reaction));
    }

    private Integer calculateUseful(Review review) {
        Set<Reaction> reactions = review.getUserReactions();
        Integer useful = 0;
        for (Reaction reaction : reactions) {
            if (reaction.getReaction() == ReactionType.LIKE) {
                useful++;
            } else {
                useful--;
            }
        }
        return useful;
    }
}