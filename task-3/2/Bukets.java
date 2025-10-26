import java.util.*;
class Bukets{
    private List<Flower> bukets = new ArrayList<>();
    private Integer PriceBukets;

    public void setFlowerInBuket(Flower flower) {
        this.bukets.add(flower);
    }

    public List<Flower> getBukets() {
        return bukets;
    }
    public void  deleteBuket(Flower flower){
         if(!bukets.isEmpty()) bukets.remove(flower);  
    }
    public Integer getPriceBuket(){
        PriceBukets = 0;
        for (Flower flower : bukets) {
            PriceBukets+=flower.getPrice();
        }
        return PriceBukets;
    }

    
}