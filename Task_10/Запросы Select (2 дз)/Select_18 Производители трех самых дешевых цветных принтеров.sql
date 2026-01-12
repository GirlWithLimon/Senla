SELECT maker, price from printer JOIN product ON printer.model=product.model 
WHERE color='y' ORDER BY price LIMIT 3;
