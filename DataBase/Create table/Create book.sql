CREATE TABLE book (
	id SERIAL NOT NULL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	author VARCHAR(50) NOT NULL,
	status VARCHAR(30) NOT NULL REFERENCES  status(type),
	price REAL DEFAULT 0,
    publicationDate DATE DEFAULT CURRENT_DATE,
   	information VARCHAR(150) DEFAULT 'No information'
);