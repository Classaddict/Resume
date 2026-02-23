-- ================================================================
-- COMIX Test Database — PostgreSQL
-- 100-row sample for development / unit testing
-- publishers: 11  |  series: 95  |  volumes: 99  |  issues: 100
-- ================================================================

BEGIN;

-- ────────────────────────────────────────────────────────────
-- SCHEMA  (mirrors production tables, suffix:  )
-- ────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS publishers  (
    publisher_id   SERIAL       PRIMARY KEY,
    name           TEXT         NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS series  (
    series_id      SERIAL       PRIMARY KEY,
    title          TEXT         NOT NULL,
    publisher_id   INTEGER      NOT NULL
                                REFERENCES publishers (publisher_id),
    UNIQUE (title, publisher_id)
);

CREATE TABLE IF NOT EXISTS volumes  (
    volume_id      SERIAL       PRIMARY KEY,
    series_id      INTEGER      NOT NULL
                                REFERENCES series (series_id),
    volume_number  INTEGER      NOT NULL DEFAULT 1,
    UNIQUE (series_id, volume_number)
);

CREATE TABLE IF NOT EXISTS issues  (
    issue_id            SERIAL   PRIMARY KEY,
    volume_id           INTEGER  NOT NULL
                                 REFERENCES volumes (volume_id),
    issue_number        TEXT,
    story_title         TEXT,
    variant_description TEXT,
    release_date        DATE,
    format              TEXT,
    creators            TEXT
);

CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS personal_collections  (
    collection_entry        SERIAL      PRIMARY KEY,
    user_id              INTEGER     NOT NULL REFERENCES users (user_id),
    issue_id                INTEGER     NOT NULL REFERENCES issues (issue_id),
    added_date           DATE,
    grade                SMALLINT    CHECK (grade IS NULL OR grade BETWEEN 1 AND 10),
    slabbed              BOOLEAN     NOT NULL DEFAULT FALSE,
    comic_value       NUMERIC(10,2),
    description          TEXT,
    principal_characters TEXT,
    CONSTRAINT slabbed_requires_grade CHECK (slabbed = FALSE OR grade IS NOT NULL)
);

CREATE TABLE IF NOT EXISTS comics  (
    comic_id       SERIAL   PRIMARY KEY,
    issue_id       INTEGER  NOT NULL UNIQUE
                            REFERENCES issues (issue_id),
    publisher_id   INTEGER  NOT NULL
                            REFERENCES publishers (publisher_id),
    series_id      INTEGER  NOT NULL
                            REFERENCES series (series_id),
    volume_id      INTEGER  NOT NULL
                            REFERENCES volumes (volume_id)
);

-- ────────────────────────────────────────────────────────────
-- Sequence resets
-- ────────────────────────────────────────────────────────────

SELECT setval('public.publishers_publisher_id_seq', 11);
SELECT setval('public.series_series_id_seq', 95);
SELECT setval('public.volumes_volume_id_seq', 99);
SELECT setval('public.issues_issue_id_seq', 100);
SELECT setval('public.comics_comic_id_seq', 100);

-- ────────────────────────────────────────────────────────────
-- publishers 
-- ────────────────────────────────────────────────────────────

INSERT INTO users (user_id, username) VALUES 
  (1, 'Bobby'),
  (2, 'Tony Tailpipe');

INSERT INTO publishers  (publisher_id, name) VALUES
  (1, 'DC Comics'),
  (2, 'Marvel Comics'),
  (3, 'Dark Horse Comics'),
  (4, 'Image Comics'),
  (5, 'Boom! Studios'),
  (6, 'Rebellion'),
  (7, 'Malibu Comics'),
  (8, 'IDW Publishing'),
  (9, 'Dynamite Entertainment'),
  (10, 'Mirage Publishing'),
  (11, 'Topps Comics')
ON CONFLICT (publisher_id) DO NOTHING;

-- ────────────────────────────────────────────────────────────
-- series 
-- ────────────────────────────────────────────────────────────

INSERT INTO series  (series_id, title, publisher_id) VALUES
  (1, 'Mazing Man', 1),
  (2, 'Action Comics', 1),
  (3, 'Age of Ultron vs. Marvel Zombies', 2),
  (4, 'Aliens: Rescue', 3),
  (5, 'All-New X-Men', 2),
  (6, 'The Amazing Spider-Man & Silk: The Spider(fly) Effect', 2),
  (7, 'The Amazing Spider-Man', 2),
  (8, 'American Gods: The Moment Of The Storm', 3),
  (9, 'Angela', 4),
  (10, 'Astonishing X-Men', 2),
  (11, 'Avengers', 2),
  (12, 'Bastard Samurai', 4),
  (13, 'Batgirl', 1),
  (14, 'Batman and Robin', 1),
  (15, 'Batman Secret Files 2019', 1),
  (16, 'Batman', 1),
  (17, 'Batman: Legends of the Dark Knight', 1),
  (18, 'Batman: The Detective', 1),
  (19, 'Big Trouble In Little China', 5),
  (20, 'Black Panther and The Crew', 2),
  (21, 'Buffy the Vampire Slayer: Season Eight', 3),
  (22, 'Buffy The Vampire Slayer: Spike', 3),
  (23, 'Catwoman', 1),
  (24, 'The Clone Conspiracy', 2),
  (25, 'Convergence: Harley Quinn', 1),
  (26, 'Daredevil', 2),
  (27, 'Darth Vader', 2),
  (28, 'The Defenders', 2),
  (29, 'Detective Comics', 1),
  (30, 'Dial H For HERO', 1),
  (31, 'Dredd: Underbelly', 6),
  (32, 'Elektra: Assassin', 2),
  (33, 'Excalibur', 2),
  (34, 'Firearm', 7),
  (35, 'Flash', 1),
  (36, 'Forever Evil: Rogues Rebellion', 1),
  (37, 'Future State: Superman / Wonder Woman', 1),
  (38, 'Generation X', 2),
  (39, 'Grayson', 1),
  (40, 'Guardians of the Galaxy', 2),
  (41, 'Hellshock', 4),
  (42, 'Hulk', 2),
  (43, 'Impulse', 1),
  (44, 'The Incredible Hulk', 2),
  (45, 'Indestructible Hulk', 2),
  (46, 'Insurgent', 1),
  (47, 'Judge Dredd', 8),
  (48, 'Justice League Dark Annual', 1),
  (49, 'Justice League', 1),
  (50, 'Justice League: Odyssey', 1),
  (51, 'Lois Lane', 1),
  (52, 'Marauders', 2),
  (53, 'Mice Templar', 4),
  (54, 'Mosaic', 2),
  (55, 'The New 52:  Futures End', 1),
  (56, 'The New Warriors', 2),
  (57, 'Nightwing', 1),
  (58, 'Old Man Logan', 2),
  (59, 'Peter Parker: The Spectacular Spider-Man', 2),
  (60, 'Powers', 4),
  (61, 'Prototype (Malibu Comics)', 7),
  (62, 'Red Sonja, Vol. 1 (Dynamite Entertainment)', 9),
  (63, 'Rune', 7),
  (64, 'Savage Sword of Conan', 2),
  (65, 'Serenity', 3),
  (66, 'Silver Surfer', 2),
  (67, 'Spawn', 4),
  (68, 'Spider-Man', 2),
  (69, 'Star Wars (Marvel)', 2),
  (70, 'Star Wars, Vol. 2 (Dark Horse) (2013)', 3),
  (71, 'Star Wars, Vol. 2 (Marvel)', 2),
  (72, 'Star Wars: Darth Vader', 2),
  (73, 'Star Wars: Droids', 3),
  (74, 'Star Wars: Shadows of the Empire - Evolution', 3),
  (75, 'Star-Lord Annual', 2),
  (76, 'Suicide Squad', 1),
  (77, 'Superior Spider-Man', 2),
  (78, 'Superman', 1),
  (79, 'Swamp Thing', 1),
  (80, 'Talon', 1),
  (81, 'Teenage Mutant Ninja Turtles', 8),
  (82, 'Teenage Mutant Ninja Turtles: Universe', 8),
  (83, 'Thor, Vol. 6 Annual', 2),
  (84, 'Transformers', 2),
  (85, 'Turtle Soup', 10),
  (86, 'Ultimate Spider-Man', 2),
  (87, 'Uncanny X-Men', 2),
  (88, 'Usagi Yojimbo', 8),
  (89, 'The Walking Dead', 4),
  (90, 'Weapon X', 2),
  (91, 'Wolfpack', 2),
  (92, 'Wonder Woman', 1),
  (93, 'X-Files', 11),
  (94, 'X-Men', 2),
  (95, 'X-Men: Gold', 2)
ON CONFLICT (series_id) DO NOTHING;

-- ────────────────────────────────────────────────────────────
-- volumes 
-- ────────────────────────────────────────────────────────────

INSERT INTO volumes  (volume_id, series_id, volume_number) VALUES
  (1, 1, 1),
  (2, 2, 3),
  (3, 3, 1),
  (4, 4, 1),
  (5, 5, 1),
  (6, 6, 1),
  (7, 7, 5),
  (8, 8, 1),
  (9, 9, 1),
  (10, 10, 4),
  (11, 11, 7),
  (12, 12, 1),
  (13, 13, 5),
  (14, 14, 2),
  (15, 15, 1),
  (16, 16, 1),
  (17, 16, 3),
  (18, 17, 1),
  (19, 18, 1),
  (20, 19, 1),
  (21, 20, 1),
  (22, 21, 1),
  (23, 22, 1),
  (24, 23, 5),
  (25, 24, 1),
  (26, 25, 1),
  (27, 26, 1),
  (28, 26, 6),
  (29, 27, 1),
  (30, 28, 5),
  (31, 29, 2),
  (32, 30, 1),
  (33, 31, 1),
  (34, 32, 1),
  (35, 33, 1),
  (36, 34, 1),
  (37, 35, 5),
  (38, 36, 1),
  (39, 37, 1),
  (40, 38, 2),
  (41, 39, 1),
  (42, 40, 6),
  (43, 41, 1),
  (44, 42, 2),
  (45, 43, 1),
  (46, 44, 1),
  (47, 44, 2),
  (48, 45, 1),
  (49, 46, 1),
  (50, 47, 4),
  (51, 48, 1),
  (52, 49, 2),
  (53, 50, 1),
  (54, 51, 2),
  (55, 52, 1),
  (56, 53, 4),
  (57, 54, 1),
  (58, 55, 1),
  (59, 56, 1),
  (60, 57, 4),
  (61, 58, 2),
  (62, 59, 1),
  (63, 60, 1),
  (64, 61, 1),
  (65, 62, 1),
  (66, 63, 1),
  (67, 64, 2),
  (68, 65, 1),
  (69, 66, 3),
  (70, 67, 1),
  (71, 68, 1),
  (72, 68, 2),
  (73, 69, 1),
  (74, 70, 1),
  (75, 71, 1),
  (76, 72, 2),
  (77, 73, 1),
  (78, 74, 1),
  (79, 75, 1),
  (80, 76, 5),
  (81, 77, 1),
  (82, 78, 4),
  (83, 79, 2),
  (84, 80, 1),
  (85, 81, 5),
  (86, 82, 1),
  (87, 83, 1),
  (88, 84, 1),
  (89, 85, 1),
  (90, 86, 1),
  (91, 87, 1),
  (92, 88, 4),
  (93, 89, 1),
  (94, 90, 3),
  (95, 91, 1),
  (96, 92, 4),
  (97, 93, 1),
  (98, 94, 1),
  (99, 95, 1)
ON CONFLICT (volume_id) DO NOTHING;

-- ────────────────────────────────────────────────────────────
-- issues 
-- ────────────────────────────────────────────────────────────

INSERT INTO issues  (issue_id, volume_id, issue_number, story_title,
                        variant_description, release_date, format, creators) VALUES
  (1, 1, '1', '"Y''know, After A Long Hard Day..."', NULL, '1986-01-01', 'Comic', 'Bob Rozakis | Stephen DeStefano | Karl Kesel'),
  (2, 2, '975A', 'Superman Reborn, Part 2 / The Man in the Purple Hat', 'Regular Patrick Gleason & Mick Gray Cover', '2017-03-08', 'Comic', 'Paul Dini | Dan Jurgens | Ian Churchill'),
  (3, 3, '3A', 'Secret Wars: Battleworld - Send Help', NULL, '2015-08-19', 'Comic', 'James Robinson | Steve Pugh'),
  (4, 4, '1B', NULL, NULL, '2019-07-24', 'Comic', 'Brian Wood | Kieran Mckeown | JL Straw'),
  (5, 5, '25A', NULL, 'Regular Stuart Immonen Cover', '2014-04-09', 'Comic', 'Brian Michael Bendis | Ronnie del Carmen | Jason Shiga'),
  (6, 6, '2', NULL, NULL, '2016-04-13', 'Comic', 'Robbie Thompson | Tom Grummett | Todd Nauck'),
  (7, 7, '47A', 'Sins Rising, Part Three', 'Regular Josemaria Casanovas Cover', '2020-08-26', 'Comic', 'Nicholas Spencer | Marcelo Ferreira | Roberto Poggi'),
  (8, 8, '8A', NULL, NULL, '2019-12-18', 'Comic', 'Neil Gaiman | Scott Hampton'),
  (9, 9, '3', NULL, NULL, '1995-02-01', 'Comic', 'Neil Gaiman | Greg Capullo | Mark Pennington'),
  (10, 10, '3A', 'Life of X, Part Three', 'Regular Ed McGuinness Cover', '2017-09-06', 'Comic', 'Charles Soule | Edward McGuinness, Jr. | Mark Morales'),
  (11, 11, '3.1A', NULL, 'Regular Barry Kitson Cover', '2017-01-18', 'Comic', 'Mark Waid | Barry Kitson | Mark Farmer'),
  (12, 12, '1', NULL, NULL, '2002-01-01', 'Comic', 'Miles Gunter | Michael Avon Oeming | Kelsey Shannon'),
  (13, 13, '48B', 'Joker War: Collateral Damage - Gordons Never Give Up, Part 1', 'Variant Ian MacDonald Cover', '2020-09-09', 'Comic', 'Cecil Castellucci | Robbi Rodriguez'),
  (14, 14, '17', 'Life Is But A Dream', NULL, '2013-02-13', 'Comic', 'Peter J. Tomasi | Patrick Gleason | Mick Gray'),
  (15, 15, NULL, NULL, NULL, '2019-07-31', 'Comic', 'Tim Seeley | Andy Kubert | Jackson Lanzing'),
  (16, 16, '653', 'Batman: Face the Face - Batman: Face the Face, Part 6', NULL, '2006-05-24', 'Comic', 'James Robinson | Don Kramer | Wayne Faucher'),
  (17, 17, '32A', 'The War of Jokes and Riddles, Conclusion', 'Regular Mikel Janin Cover', '2017-10-04', 'Comic', 'Thomas King | Mikel Janín'),
  (18, 18, '5', 'Shaman, Book 5', NULL, '1990-01-01', 'Comic', 'Denny O''Neil | Ed Hannigan | John Beatty'),
  (19, 19, '4A', NULL, 'Regular Andy Kubert Cover', '2021-07-13', 'Comic', 'Tom Taylor | Andy Kubert | Sandra Hope'),
  (20, 20, '15A', NULL, NULL, '2015-09-16', 'Comic', 'Fred Van Lente | Joe Eisma'),
  (21, 21, '2A', 'We are the Streets, Part 2: Afro-Blue', 'Regular John Cassaday Cover', '2017-05-10', 'Comic', 'Ta-Nehisi Coates | Yona Harvey | Jackson Guice'),
  (22, 22, '6B', 'No Future For You, Part One', 'Variant Cover', '2007-09-01', 'Comic', 'Brian K. Vaughan | Georges Jeanty | Andy Owens'),
  (23, 23, '4A', 'A Dark Place, Part Four', 'Frison Cover', '2012-11-21', 'Comic', 'Victor Gischler | Paul Lee | Andy Owens'),
  (24, 24, '27A', 'Highway Robbery', 'Regular Joelle Jones Cover', '2020-11-17', 'Comic', 'Ram V. | Fernando Blanco'),
  (25, 25, '5A', NULL, 'Regular Gabriele Dell Otto Cover', '2017-02-15', 'Comic', 'Dan Slott | Jim Cheung | Jay Leisten'),
  (26, 26, '2A', 'Convergence - Rabbit Season', 'Steve Pugh Regular Cover', '2015-05-06', 'Comic', 'Steve Pugh | Phil Winslade | John Dell, III'),
  (27, 27, '279', 'Before The Flame', NULL, '1990-04-01', 'Comic', 'Ann Nocenti | John Romita Jr. | Al Williamson'),
  (28, 28, '26A', 'King in Black - The Black Kitchen, Part 1', 'Regular Marco Checchetto Cover', '2021-01-27', 'Comic', 'Chip Zdarsky | Mike Hawthorne | Marco Checchetto'),
  (29, 29, '6', 'Vader, Book I, Part VI', 'Regular Adi Granov Cover', '2015-06-03', 'Comic', 'Kieron Gillen | Salvador Larroca'),
  (30, 30, '6A', 'Kingpins of New York', 'Regular David Marquez Cover', '2017-10-11', 'Comic', 'Brian Michael Bendis | David Marquez'),
  (31, 31, '49A', 'The Bronze Age, Salt Of The Earth', NULL, '2016-02-03', 'Comic', 'Peter J. Tomasi | Fernando Pasarin | Matt Ryan'),
  (32, 32, '10', 'Miguel And Summer Travel the Multiverse', NULL, '2020-01-01', 'Comic', 'Sam Humphries | Joseph Quinones, Jr.'),
  (33, 33, NULL, NULL, NULL, '2014-01-29', 'Comic', 'Arthur Wyatt | Henry Flint'),
  (34, 34, '3', 'Rough Cut', NULL, '1986-10-01', 'Comic', 'Frank Miller | Bill Sienkiewicz'),
  (35, 35, '92', 'I Want You', 'Deluxe Edition', '1995-12-01', 'Comic', 'Warren Ellis | Casey Jones | Tom Simmons'),
  (36, 36, '12', 'The Rafferty Saga, Prologue', NULL, '1994-08-01', 'Comic', 'James Robinson | Ben Herrera | Michael Christian'),
  (37, 37, '7A', 'No More Speedsters', 'Regular Carmine Di Giandomenico Cover', '2016-09-28', 'Comic', 'Joshua Williamson | Carmine Di Giandomenico'),
  (38, 38, '5A', 'Forever Evil - Fastest Psychopath Alive', NULL, '2014-02-12', 'Comic', 'Brian Buccellato | Scott Hepburn'),
  (39, 39, '2A', 'Future State - The Planet''s Finest', 'Regular Lee Weeks Cover', '2021-02-09', 'Comic', 'Dan Watters | Leila Del Duca'),
  (40, 40, '87', NULL, NULL, '2018-02-21', 'Comic', 'Christina Strain | Amilcar Pinna'),
  (41, 41, '10A', 'Nemesis, Part 2', 'Mikel Janin Regular Cover', '2015-07-22', 'Comic', 'Tim Seeley | Mikel Janín'),
  (42, 42, '3A', 'Forever.  If We Wanted', 'Regular Ivan Shavrin Cover', '2020-03-18', 'Comic', 'Al Ewing | Juan Cabal | Belén Ortega'),
  (43, 43, '2', 'The Sign Of The Cross, Part 2', NULL, '1994-08-01', 'Comic', 'Jae Lee'),
  (44, 44, '9', 'The Omega Hulk, Chapter Five', NULL, '2014-12-03', 'Comic', 'Gerry Duggan | Mark Bagley | Andrew Hennessey'),
  (45, 45, '19', 'Final Night - A Game Of Spew', NULL, '1996-09-11', 'Comic', 'Mark Waid | Humberto Ramos | Wayne Faucher'),
  (46, 46, '365', 'Countdown, Part 2: Fantastic Four', NULL, '1989-11-21', 'Comic', 'Peter David | Jeff Purves | Marie Severin'),
  (47, 47, '21', 'Maximum Security - Part 9: The Truth Is Really ''out There''', NULL, '2000-12-01', 'Comic', 'Paul Jenkins | Kyle Hotz | Eric Powell'),
  (48, 48, '3A', 'Agent Of S.H.I.E.L.D., Part 3', 'Regular Leinil Francis Yu Cover', '2013-01-16', 'Comic', 'Mark Waid | Leinil Francis Yu | Gerry Alanguilan'),
  (49, 49, '2', NULL, NULL, '2013-02-06', 'Comic', NULL),
  (50, 50, '17A', 'American Way of Death, Part One', NULL, '2014-03-26', 'Comic', NULL),
  (51, 51, '2', 'War Of The Houses', NULL, '2014-10-29', 'Comic', 'J.M. DeMatteis | Klaus Janson | John Stanisci'),
  (52, 52, '4A', 'The Extinction Machines, Part Four', 'Regular Fernando Pasarin Cover', '2016-09-07', 'Comic', 'Bryan Hitch | Jesús Merino | Andy Owens'),
  (53, 53, '16A', 'Lost and Found', 'Regular Will Conrad Cover', '2019-12-11', 'Comic', 'Daniel Abnett | Cliff Richards'),
  (54, 54, '4B', NULL, 'Variant Emanuela Lupacchino Cover', '2019-10-02', 'Comic', 'Greg Rucka | Mike Perkins'),
  (55, 55, '15A', 'X of Swords - X of Swords, Chapter 14', 'Regular Russell Dauterman Cover', '2020-11-11', 'Comic', 'Gerry Duggan | Benjamin Percy | Jonathan Hickman'),
  (56, 56, '4A', NULL, NULL, '2013-07-03', 'Comic', NULL),
  (57, 57, '1A', 'Mood Indigo', 'Regular Stuart Immonen Cover', '2016-10-12', 'Comic', 'Geoffrey Thorne | Khary Randolph'),
  (58, 58, '44', NULL, NULL, '2015-03-04', 'Comic', 'Brian Azzarello | Keith Giffen | Dan Jurgens'),
  (59, 59, '1A', 'From The Ground Up!', NULL, '1990-05-30', 'Comic', 'Fabian Nicieza | Mark Bagley | Al Williamson'),
  (60, 60, '46A', NULL, 'Regular Mike Perkins Cover', '2018-07-04', 'Comic', 'Benjamin Percy | Lalit Kumar Sharma | Christopher Mooneyham'),
  (61, 61, '8A', NULL, 'Andrea Sorrentino Regular Cover', '2016-07-13', 'Comic', 'Jeff Lemire | Andrea Sorrentino'),
  (62, 62, '313A', 'Spider-Geddon', 'Regular Jeff Dekal Cover', '2018-12-12', 'Comic', 'Sean Ryan | Juan Frigeri'),
  (63, 63, '30', 'The Sellouts, Part 6', NULL, '2003-03-01', 'Comic', 'Brian Michael Bendis | Michael Avon Oeming'),
  (64, 64, '14', 'Bent, Folded, Spindled And Mutilated', NULL, '1994-10-01', 'Comic', 'Len Strazewski | Roger Robinson | Scott Reed'),
  (65, 65, '11A', NULL, 'Frison Cover', '2014-05-28', 'Comic', 'Gail Simone | Walter Geovani'),
  (66, 66, '9', 'Janus / Argus', NULL, '1995-04-01', 'Comic', 'Keith Conroy | Jeffrey Moore | Rodney Gates'),
  (67, 67, '6A', 'The Suitor''s Revenge', 'Regular David Finch Cover', '2019-06-19', 'Comic', 'Meredith Finch | Scott Oden | Luke Ross'),
  (68, 68, '1C', 'Those Left Behind, Part 1', 'Jayne Cover', '2005-07-01', 'Comic', 'Will Conrad'),
  (69, 69, '66', 'Conflicting Emotions', NULL, '1992-06-01', 'Comic', 'Ron Marz | Steve Carr | Deryl Skelton'),
  (70, 70, '4', 'Questions, Part 4', NULL, '1992-09-01', 'Comic', 'Todd McFarlane'),
  (71, 71, '4', 'Torment, Part Four', NULL, '1990-11-01', 'Comic', 'Todd McFarlane'),
  (72, 72, '16', NULL, NULL, '2017-05-03', 'Comic', 'Brian Michael Bendis | Oscar Bazaldua'),
  (73, 73, '4A', 'In Battle With Darth Vader', NULL, '1977-10-01', 'Comic', 'Roy William Thomas, Jr. | Howard Chaykin | Steve Leialoha'),
  (74, 74, '4A', 'In the Shadow of Yavin, Part Four', 'Alex Ross Regular Cover', '2013-04-10', 'Comic', 'Brian Wood | Carlos D''Anda'),
  (75, 75, '67A', NULL, 'Regular Gerald Parel Cover', '2019-06-19', 'Comic', 'Kieron Gillen | Angel Unzueta'),
  (76, 76, '8', 'The Dying Light, The Dying Light', 'Giuseppe Camuncoli Regular Cover', '2017-11-15', 'Comic', 'Charles Soule | Giuseppe Camuncoli | Daniele Orlandini'),
  (77, 77, '2', NULL, NULL, '1994-05-01', 'Comic', 'Dan Thorsland | Bill Hughes | Andy Mushynsky'),
  (78, 78, '2', 'A Journey Of A Thousand Light-Years', NULL, '1998-03-01', 'Comic', 'Steve Perry | Ron Randall | Tom Simmons'),
  (79, 79, '1', NULL, NULL, '2017-05-24', 'Comic', 'Chip Zdarsky | Djibril Morissette'),
  (80, 80, '7A', NULL, 'Regular Daniel Sampere & Juan Albarran Cover', '2020-07-28', 'Comic', 'Thomas Taylor | Daniel Sampere | Juan Albarran'),
  (81, 81, '14', 'A Blind Eye', 'Giuseppe Camuncoli Regular Cover', '2013-07-24', 'Comic', 'Dan Slott | Humberto Ramos | Victor Olazaba'),
  (82, 82, '5A', 'Son of Superman, Part Five', 'Regular Patrick Gleason Cover', '2016-08-17', 'Comic', 'Patrick Gleason | Peter J. Tomasi | Doug Mahnke'),
  (83, 83, '2', 'Something To Live For', NULL, '1982-06-01', 'Comic', 'Martin Pasko | Mike W. Barr | Dan Spiegle'),
  (84, 84, '4A', 'Nightmares', NULL, '2013-01-30', 'Comic', 'Scott Snyder | James Tynion, IV | Guillem March'),
  (85, 85, '23B', 'City Fall', 'Kevin Eastman Variant Cover', '2013-06-26', 'Comic', 'Kevin Eastman | Tom Waltz | Bobby Curnow'),
  (86, 86, '1A', NULL, 'Regular Freddie Williams Cover', '2016-08-31', 'Comic', 'Kevin Eastman | Tom Waltz | Bobby Curnow'),
  (87, 87, '1A', NULL, 'Regular Aaron Kuder Cover', '2021-07-21', 'Comic', 'Aaron Kuder | Jed Mackay | Juan Ferreyra'),
  (88, 88, '16', 'Plight Of The Bumblebee!', NULL, '1986-05-01', 'Comic', 'Len Kaminski | Graham Nolan | Tom Morgan'),
  (89, 89, '1', 'The Naked City / Turtles Attack!!! / The Purpose Of Fear / The Ring / The Name Is Lucindra / Turtle Power!', NULL, '1991-11-01', 'Comic', 'Rick McCollum | Michael Dooney | Rick Arthur'),
  (90, 90, '122', NULL, NULL, '2008-05-28', 'Comic', 'Brian Michael Bendis | Stuart Immonen | Wade von Grawbadger'),
  (91, 91, '118', 'The Submergence Of Japan!', NULL, '1979-02-01', 'Comic', 'Chris Claremont | John Byrne | Ricardo Villamonte'),
  (92, 91, '329', 'Warriors Of The Ebon Night', 'Deluxe Edition', '1996-02-01', 'Comic', 'Scott Lobdell | Jeph Loeb | Joe Madureira'),
  (93, 92, '12', NULL, NULL, '2020-09-02', 'Comic', 'Stan Sakai'),
  (94, 93, '129', 'A New Beginning', 'Regular Charlie Adlard & Dave Stewart Cover', '2014-07-09', 'Comic', 'Robert Kirkman | Charlie Adlard | Stefano Gaudiano'),
  (95, 94, '2A', NULL, 'Regular Greg Land Cover', '2017-04-26', 'Comic', 'Gregory Pak | Greg Land | Jay Leisten'),
  (96, 95, '2', 'Wheels', NULL, '1988-01-01', 'Comic', 'Larry Hama | Ron Wilson | Kyle Baker'),
  (97, 96, '4', 'Blood', NULL, '2011-12-21', 'Comic', 'Brian Azzarello | Cliff Chiang'),
  (98, 97, '9', 'Silent Cities of the Mind, Part 2', NULL, '1995-09-01', 'Comic', 'Stefan Petrucha | Charlie Adlard'),
  (99, 98, '1E', 'Rubicon', 'Cyclops, Wolverine, Iceman Cover (Logo 2)', '1991-08-14', 'Comic', 'Chris Claremont | Jim Lee | Scott Williams'),
  (100, 99, '1A', 'Untitled / The Sorrow Beneath The Sport! / Untitled / Options! / Dreams Brighten', 'Regular Olivier Coipel Cover', '2013-11-13', 'Comic', 'Roy Thomas | Chris Claremont | Len Wein')
ON CONFLICT (issue_id) DO NOTHING;

-- ────────────────────────────────────────────────────────────
-- personal_collections   — populated at runtime
-- ────────────────────────────────────────────────────────────

-- Example (uncomment to use):
-- INSERT INTO personal_collections 
--   (user_id, issue_id, added_date, grade, slabbed,
--    value_override, description, principal_characters)
-- VALUES (1, 1, '2024-01-01', NULL, FALSE, NULL, NULL, NULL);

INSERT INTO personal_collections (collection_entry, user_id, issue_id, added_date, grade, slabbed, comic_value, description, principal_characters) VALUES 
  (1, 1, 1, '2026-2-20', NULL, FALSE, 20.0, 'A comic', 'Bob ross'),
  (2, 1, 2, '2026-2-20', NULL, FALSE, 20.0, 'A comic 2', 'Bob ross again'),
  (3, 1, 3, '2026-2-20', NULL, FALSE, 20.0, 'A comic 3', 'Bob ross one more time'),
  (4, 2, 5, '2026-2-19', NULL, FALSE, 2.0, 'A comic 4', 'Bob ross one more time');

-- ────────────────────────────────────────────────────────────
-- comics   — denormalised FK hub
-- ────────────────────────────────────────────────────────────

INSERT INTO comics  (comic_id, issue_id, publisher_id, series_id, volume_id) VALUES
  (1, 1, 1, 1, 1),
  (2, 2, 1, 2, 2),
  (3, 3, 2, 3, 3),
  (4, 4, 3, 4, 4),
  (5, 5, 2, 5, 5),
  (6, 6, 2, 6, 6),
  (7, 7, 2, 7, 7),
  (8, 8, 3, 8, 8),
  (9, 9, 4, 9, 9),
  (10, 10, 2, 10, 10),
  (11, 11, 2, 11, 11),
  (12, 12, 4, 12, 12),
  (13, 13, 1, 13, 13),
  (14, 14, 1, 14, 14),
  (15, 15, 1, 15, 15),
  (16, 16, 1, 16, 16),
  (17, 17, 1, 16, 17),
  (18, 18, 1, 17, 18),
  (19, 19, 1, 18, 19),
  (20, 20, 5, 19, 20),
  (21, 21, 2, 20, 21),
  (22, 22, 3, 21, 22),
  (23, 23, 3, 22, 23),
  (24, 24, 1, 23, 24),
  (25, 25, 2, 24, 25),
  (26, 26, 1, 25, 26),
  (27, 27, 2, 26, 27),
  (28, 28, 2, 26, 28),
  (29, 29, 2, 27, 29),
  (30, 30, 2, 28, 30),
  (31, 31, 1, 29, 31),
  (32, 32, 1, 30, 32),
  (33, 33, 6, 31, 33),
  (34, 34, 2, 32, 34),
  (35, 35, 2, 33, 35),
  (36, 36, 7, 34, 36),
  (37, 37, 1, 35, 37),
  (38, 38, 1, 36, 38),
  (39, 39, 1, 37, 39),
  (40, 40, 2, 38, 40),
  (41, 41, 1, 39, 41),
  (42, 42, 2, 40, 42),
  (43, 43, 4, 41, 43),
  (44, 44, 2, 42, 44),
  (45, 45, 1, 43, 45),
  (46, 46, 2, 44, 46),
  (47, 47, 2, 44, 47),
  (48, 48, 2, 45, 48),
  (49, 49, 1, 46, 49),
  (50, 50, 8, 47, 50),
  (51, 51, 1, 48, 51),
  (52, 52, 1, 49, 52),
  (53, 53, 1, 50, 53),
  (54, 54, 1, 51, 54),
  (55, 55, 2, 52, 55),
  (56, 56, 4, 53, 56),
  (57, 57, 2, 54, 57),
  (58, 58, 1, 55, 58),
  (59, 59, 2, 56, 59),
  (60, 60, 1, 57, 60),
  (61, 61, 2, 58, 61),
  (62, 62, 2, 59, 62),
  (63, 63, 4, 60, 63),
  (64, 64, 7, 61, 64),
  (65, 65, 9, 62, 65),
  (66, 66, 7, 63, 66),
  (67, 67, 2, 64, 67),
  (68, 68, 3, 65, 68),
  (69, 69, 2, 66, 69),
  (70, 70, 4, 67, 70),
  (71, 71, 2, 68, 71),
  (72, 72, 2, 68, 72),
  (73, 73, 2, 69, 73),
  (74, 74, 3, 70, 74),
  (75, 75, 2, 71, 75),
  (76, 76, 2, 72, 76),
  (77, 77, 3, 73, 77),
  (78, 78, 3, 74, 78),
  (79, 79, 2, 75, 79),
  (80, 80, 1, 76, 80),
  (81, 81, 2, 77, 81),
  (82, 82, 1, 78, 82),
  (83, 83, 1, 79, 83),
  (84, 84, 1, 80, 84),
  (85, 85, 8, 81, 85),
  (86, 86, 8, 82, 86),
  (87, 87, 2, 83, 87),
  (88, 88, 2, 84, 88),
  (89, 89, 10, 85, 89),
  (90, 90, 2, 86, 90),
  (91, 91, 2, 87, 91),
  (92, 92, 2, 87, 91),
  (93, 93, 8, 88, 92),
  (94, 94, 4, 89, 93),
  (95, 95, 2, 90, 94),
  (96, 96, 2, 91, 95),
  (97, 97, 1, 92, 96),
  (98, 98, 11, 93, 97),
  (99, 99, 2, 94, 98),
  (100, 100, 2, 95, 99)
ON CONFLICT (comic_id) DO NOTHING;

COMMIT;
