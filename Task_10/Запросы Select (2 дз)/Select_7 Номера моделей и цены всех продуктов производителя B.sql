(SELECT p.model,a.price FROM product p JOIN pc a ON p.model = a.model WHERE p.maker = 'B')
UNION
(SELECT p.model,a.price FROM product p JOIN laptop a ON p.model = a.model WHERE p.maker = 'B')
UNION
(SELECT p.model,a.price FROM product p JOIN printer a ON p.model = a.model WHERE p.maker = 'B')