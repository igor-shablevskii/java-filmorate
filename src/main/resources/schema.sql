CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INTEGER PRIMARY KEY,
    genre_name varchar(100)
);

CREATE TABLE IF NOT EXISTS directors (
    director_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    director_name varchar(100)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    mpa_name varchar(10)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id          INTEGER PRIMARY KEY AUTO_INCREMENT,
    film_name        varchar(100) NOT NULL,
    film_description text         NOT NULL,
    film_releaseDate date         NOT NULL,
    film_duration    INTEGER      NOT NULL,
    mpa_id           INTEGER REFERENCES mpa_ratings (mpa_id),
    rate             INTEGER      NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_director (
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors (director_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id       INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_name     varchar(200)        NOT NULL,
    user_login    varchar(200) UNIQUE NOT NULL,
    user_email    varchar(200) UNIQUE NOT NULL,
    user_birthday date                NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    content     text,
    useful      INTEGER,
    is_positive boolean     NOT NULL,
    user_id     INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    film_id     INTEGER REFERENCES films (film_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_reactions
(
    user_id   INTEGER REFERENCES users (user_id),
    review_id INTEGER REFERENCES reviews (review_id) ON DELETE CASCADE,
    reaction_type varchar     NOT NULL,
    PRIMARY KEY (review_id, user_id)
);
