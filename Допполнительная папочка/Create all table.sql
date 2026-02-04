CREATE TABLE status (
	type VARCHAR(20) NOT NULL PRIMARY KEY
);
CREATE TABLE book (
	id INT NOT NULL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	author VARCHAR(50) NOT NULL,
	status VARCHAR(15) NOT NULL REFERENCES  status(type),
	price MONEY DEFAULT 0,
    publicationDate DATE DEFAULT CURRENT_DATE,
   	information VARCHAR(150) DEFAULT 'No information'
);
CREATE TABLE bookCopy (
	id INT NOT NULL PRIMARY KEY,
	idBook INT NOT NULL REFERENCES  book(id),
	arrivalDate DATE NOT NULL
);
CREATE TABLE orders (
	id INT NOT NULL PRIMARY KEY,
	status VARCHAR(15) NOT NULL REFERENCES  status(type),
	orderDate DATE NOT NULL,
	customerName VARCHAR(50) NOT NULL,
	customerContact VARCHAR(50) NOT NULL,
	totalPrice MONEY
);
CREATE TABLE orderItem (
	id INT NOT NULL PRIMARY KEY,
	idOrders INT NOT NULL REFERENCES  orders(id),
	book INT NOT NULL REFERENCES  book(id),
	bookCopy INT REFERENCES  bookCopy(id),
	status VARCHAR(15) NOT NULL REFERENCES  status(type),
	price MONEY
);
CREATE TABLE request (
	id INT NOT NULL PRIMARY KEY,
	idOrderItem INT NOT NULL REFERENCES  orderItem(id)
);