SELECT maker, AVG(screen) from product LEFT JOIN laptop ON laptop.model=product.model GROUP BY maker;
