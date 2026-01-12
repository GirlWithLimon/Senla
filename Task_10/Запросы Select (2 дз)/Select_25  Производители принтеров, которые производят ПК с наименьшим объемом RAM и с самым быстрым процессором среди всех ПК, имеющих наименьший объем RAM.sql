SELECT maker from product WHERE 
maker IN (SELECT maker from product WHERE type='Printer')
AND model IN (
   SELECT pc.model from product JOIN pc ON pc.model=product.model  
   WHERE speed = (SELECT MAX(speed) from pc WHERE ram=(SELECT MIN(ram) from pc))
);
