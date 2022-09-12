CREATE TABLE IF NOT EXISTS tours (
    id            INT         NOT NULL    AUTO_INCREMENT  PRIMARY KEY,
    text          VARCHAR     NOT NULL
);
CREATE TABLE IF NOT EXISTS trips (
    id            INT         NOT NULL    AUTO_INCREMENT  PRIMARY KEY,
    text          VARCHAR     NOT NULL,
    tour_id       INT         NOT NULL
);
