SELECT type, l.model, speed from laptop l JOIN product p ON p.model=l.model WHERE l.speed < (SELECT MIN(speed) from pc);
