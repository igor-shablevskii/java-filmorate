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

    public List<Review> getReviews(Integer count, Long filmId) {
        List<Review> reviews;
        if (filmId != null) {
            isFilmExists(filmId);
            reviews = reviewDao.getReviewsByFilmId(count, filmId);
        } else {
            reviews = reviewDao.getReviews(count);
        }
        reviews.forEach(review ->
                addReactionForReview(review, reactionDao.getReactions(review.getReviewId())));
        return reviews.stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public Review getReviewById(Long reviewId) {
        isReviewExists(reviewId);
        Review review = reviewDao.getReviewById(reviewId);
        List<Reaction> reactions = reactionDao.getReactions(reviewId);
        addReactionForReview(review, reactions);
        return review;
    }

    public void deleteReview(Long reviewId) {
        isReviewExists(reviewId);
        Review review = getReviewById(reviewId);
        Feed feed = new Feed(review.getUserId(), EventType.REVIEW, Operation.REMOVE, reviewId);
        feedDao.create(feed);
        reviewDao.deleteReview(reviewId);
    }

    public void saveLike(Long reviewId, Long userId) {
        isReviewExists(reviewId);
        isUserExists(userId);
        reactionDao.saveLike(reviewId, userId);
    }

    public void saveDislike(Long reviewId, Long userId) {
        isReviewExists(reviewId);
        isUserExists(userId);
        reactionDao.saveDislike(reviewId, userId);
    }

    public void deleteLike(Long reviewId, Long userId) {
        isReactionExists(reviewId, userId);
        reactionDao.deleteReaction(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        isReactionExists(reviewId, userId);
        reactionDao.deleteReaction(reviewId, userId);
    }

    private void isUserExists(Long userId) {
        if (!userDao.containsInStorage(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    private void isFilmExists(Long filmId) {
        if (!filmDao.containsInStorage(filmId)) {
            throw new NotFoundException(String.format("Film with id = %d not found", filmId));
        }
    }

    private void isReviewExists(Long reviewId) {
        if (!reviewDao.containsInStorage(reviewId)) {
            throw new NotFoundException(String.format("Review with id = %d not found", reviewId));
        }
    }

    private void isReactionExists(Long reviewId, Long userId) {
        if (!reactionDao.containsReactionInStorage(reviewId, userId)) {
            throw new NotFoundException("Reaction on review not found");
        }
    }

    private void addReactionForReview(Review review, List<Reaction> reactions) {
        reactions.forEach(reaction -> review.getUserReactions().add(reaction));
    }
}