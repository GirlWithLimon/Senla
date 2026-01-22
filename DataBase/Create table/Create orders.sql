CREATE TABLE orders (
	id INT NOT NULL PRIMARY KEY,
	status VARCHAR(15) NOT NULL REFERENCES  status(type),
	orderDate DATE NOT NULL,
	customerName VARCHAR(50) NOT NULL,
	customerContact VARCHAR(50) NOT NULL,
	totalPrice MONEY
);