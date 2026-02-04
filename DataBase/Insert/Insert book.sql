DELETE FROM bookCopy;
ALTER SEQUENCE book_id_seq RESTART WITH 1;

INSERT INTO book (name, author, status, price)
VALUES
('Last day of summer', 'dr.Braun', 'OUT_OF_STOCK', 250),
('Last day of winter', 'dr.Braun', 'OUT_OF_STOCK', 250),
('Sun', 'Lui Steff', 'OUT_OF_STOCK', 250),
('Flower', 'Lui Steff', 'OUT_OF_STOCK', 450);
