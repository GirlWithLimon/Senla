SELECT maker FROM product p LEFT JOIN pc ON p.model=pc.model WHERE pc.speed>=450 GROUP BY maker;
