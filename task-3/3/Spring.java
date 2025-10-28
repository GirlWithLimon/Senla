public class Spring implements IProductPart{
    private final String name = "Пружинка";

    @Override
    public String toString(){
        return name;
    }
}