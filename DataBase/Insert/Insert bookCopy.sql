DELETE FROM bookCopy;
ALTER SEQUENCE book_id_seq RESTART WITH 1;

INSERT INTO bookCopy (idBook, arrivalDate)
VALUES
(1, '2000-09-09');
