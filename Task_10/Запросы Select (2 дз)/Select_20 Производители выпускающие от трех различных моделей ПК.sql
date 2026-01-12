SELECT maker, COUNT(model) from product WHERE type='PC' GROUP BY maker HAVING  COUNT(model)>=3;
