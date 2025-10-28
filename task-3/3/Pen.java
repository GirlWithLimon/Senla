public class Pen implements IProduct{
    IProductPart body;
    IProductPart spring;
    IProductPart barrel;
    @Override
    public void installFirstPart(IProductPart part) {
       this.body = part;
        System.out.println("Установка: "+ part);
    }

    @Override
    public void installSecondPart(IProductPart part) {
      this.spring = part;
      System.out.println("Установка: "+ part);
    }

    @Override
    public void installThirdPart(IProductPart part) {
       this.barrel = part;
       System.out.println("Установка: "+ part);
    }
    
    

    @Override
    public String toString(){
        return "Ручка c " +body.toString()+ ", "+ spring.toString()+", "+barrel.toString();
    }
}