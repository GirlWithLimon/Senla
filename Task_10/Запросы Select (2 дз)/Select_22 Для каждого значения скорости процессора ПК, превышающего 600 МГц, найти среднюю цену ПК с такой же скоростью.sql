SELECT speed, AVG(price::numeric) from pc WHERE speed > 600 GROUP BY speed;
