SELECT maker FROM product WHERE maker IN (SELECT maker FROM product WHERE type='PC')
AND maker NOT IN (SELECT maker FROM product WHERE type='Laptop') GROUP BY maker;
