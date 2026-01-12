(SELECT model,price from pc) UNION (SELECT model,price from laptop) 
UNION (SELECT model, price from printer) ORDER BY price DESC LIMIT 3; 
