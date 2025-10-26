public class BarrelStepLine implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Выполнено изготовление стержня ручки.");
        return new Barrel();
    }
}