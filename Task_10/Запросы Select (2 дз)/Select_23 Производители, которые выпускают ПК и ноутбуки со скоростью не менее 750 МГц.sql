SELECT maker from product WHERE 
maker IN (SELECT maker from product JOIN pc ON pc.model=product.model WHERE pc.speed>=750)
AND maker IN (SELECT maker from product JOIN laptop ON laptop.model=product.model WHERE laptop.speed>=750) 
GROUP BY maker;
