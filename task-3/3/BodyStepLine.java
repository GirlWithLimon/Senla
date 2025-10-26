public class BodyStepLine implements ILineStep {
    @Override
    public IProductPart buildProductPart() {
        System.out.println("Выполнено изготовление корпуса ручки.");
        return new Body();
    }
}