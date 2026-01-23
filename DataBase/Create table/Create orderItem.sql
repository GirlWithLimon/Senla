CREATE TABLE orderItem (
	id INT NOT NULL PRIMARY KEY,
	idOrders INT NOT NULL REFERENCES  orders(id),
	idBook INT NOT NULL REFERENCES  book(id),
	idBookCopy INT REFERENCES  bookCopy(id),
	status VARCHAR(15) NOT NULL REFERENCES  status(type)
);