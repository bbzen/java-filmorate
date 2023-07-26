

create table if not exists users
(
user_id int generated by default as identity primary key,
email varchar(255) not null,
login varchar(255) not null,
user_name varchar(255),
birthday timestamp
);

create table if not exists friends
(
requester_id int references users(user_id) ON delete CASCADE,
acceptor_id int references users(user_id) ON delete CASCADE,
CONSTRAINT constraint_name UNIQUE (requester_id, acceptor_id)
);

create table if not exists mpa
(mpa_id int generated by default as identity primary key,
mpa_name varchar(5) unique
);

create table if not exists films
(
    film_id int generated by default as identity primary key,
    film_name varchar(255) not null,
    film_description varchar(500),
    release_date timestamp,
    film_duration bigint,
    film_mpa int references mpa(mpa_id)
);

create table if not exists likes
(
film_id int references films(film_id) ON delete CASCADE,
user_id int references users(user_id),
constraint constrlikes unique (film_id, user_id)
);


create table if not exists genres
(
genre_id int generated by default as identity primary key,
genre_name varchar(50) unique
);

create table if not exists film_genres
(
film_id int references films(film_id) on delete cascade,
genre_id int references genres(genre_id),
constraint constrfilmgenres unique (film_id, genre_id)
);


create table if not exists reviews
(
  review_id   Integer generated by default as identity primary key,
  user_id     int references users on delete cascade,
  film_id     int references films on delete cascade,
  content     varchar,
  is_positive boolean
);

create table if not exists review_like
(
  user_id     int references users on delete cascade,
  review_id   int references reviews on delete cascade,
  is_like     boolean,
  primary key (review_id, user_id)
);

create table if not exists directors
(
dir_id int generated by default as identity primary key,
dir_name varchar(255) unique
);

create table if not exists film_directors
(
film_id int references films(film_id) on delete cascade,
dir_id int references directors(dir_id) on delete cascade,
constraint constrfilmdirs unique (film_id, dir_id)
);

CREATE TABLE IF NOT EXISTS feeds (
    eventId   bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    userId    bigint REFERENCES public.users (user_id) ON DELETE CASCADE,
    timestamp  bigint,
    eventType varchar(30) NOT NULL,
    operation varchar(30) NOT NULL,
    entityId  bigint
);