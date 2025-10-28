public class SpringStepLine implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Выполнено изготовление пружины ручки.");
        return new Spring();
    }
}