CREATE TABLE IF NOT EXISTS genres (
    genre_id   INTEGER PRIMARY KEY,
    genre_name varchar(100)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    mpa_name varchar(10)
);

CREATE TABLE IF NOT EXISTS films (
    film_id          INTEGER PRIMARY KEY AUTO_INCREMENT,
    film_name        varchar(100) NOT NULL,
    film_description text         NOT NULL,
    film_releaseDate date         NOT NULL,
    film_duration    INTEGER      NOT NULL,
    mpa_id    INTEGER REFERENCES mpa_ratings (mpa_id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id  INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genres (genre_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id       INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_name     varchar(200)        NOT NULL,
    user_login    varchar(200) UNIQUE NOT NULL,
    user_email    varchar(200) UNIQUE NOT NULL,
    user_birthday date                NOT NULL
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER REFERENCES films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id   INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES users (user_id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, friend_id)
);