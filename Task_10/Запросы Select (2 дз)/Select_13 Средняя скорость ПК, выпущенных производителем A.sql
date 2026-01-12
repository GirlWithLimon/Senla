SELECT AVG(pc.speed) from pc LEFT JOIN product p ON pc.model=p.model WHERE p.maker='A';
