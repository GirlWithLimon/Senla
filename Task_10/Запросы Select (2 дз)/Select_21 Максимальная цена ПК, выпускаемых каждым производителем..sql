SELECT maker, MAX(price) from product JOIN pc ON pc.model=product.model GROUP BY maker;
