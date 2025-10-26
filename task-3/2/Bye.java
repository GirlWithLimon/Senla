class bye{
    public static void main(String[] args) {
        Flower whiteRose = new WhiteRose();
        Flower redRose = new RedRose();
        Flower whiteLilies = new WhiteLilies();
        Bukets buketsSummer = new Bukets();
        buketsSummer.setFlowerInBuket(whiteRose);
        buketsSummer.setFlowerInBuket(whiteRose);
        buketsSummer.setFlowerInBuket(whiteRose);
        buketsSummer.setFlowerInBuket(whiteRose);
        buketsSummer.setFlowerInBuket(redRose);
        buketsSummer.setFlowerInBuket(redRose);
        buketsSummer.setFlowerInBuket(redRose);
        buketsSummer.setFlowerInBuket(whiteLilies);
       System.out.println("Цена букета: "+buketsSummer.getPriceBuket());
    }
}