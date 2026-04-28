DROP TABLE IF EXISTS clubs CASCADE;


CREATE TABLE clubs (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(30) NOT NULL,
    yellow INT NOT NULL,
    capacity INT NOT NULL,
    genre VARCHAR(20) NOT NULL,
    count INT NOT NULL DEFAULT 0,
    city VARCHAR(30) NOT NULL
);

INSERT INTO clubs (name, yellow, capacity, genre,city) 
VALUES('Club Arcane', 70,100,'Pop','Boston'),('Club Underground',30,50,'Rock','Hartford'),('Club Soda', 12,20,'Metal','LA'),('Studio 52', 32,52,'Rap','New York');