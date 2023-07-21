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