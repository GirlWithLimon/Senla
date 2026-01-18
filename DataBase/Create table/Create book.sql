CREATE TABLE book (
	id INT NOT NULL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	author VARCHAR(50) NOT NULL,
	status VARCHAR(15) NOT NULL REFERENCES  status(type),
	price MONEY,
	publicationDate DATE,
	information VARCHAR(150)
);