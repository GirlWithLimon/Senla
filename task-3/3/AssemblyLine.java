public class AssemblyLine implements IAssemblyLine {
    private final ILineStep bodyStep;
    private final  ILineStep springStep;
    private final ILineStep barrelStep;
    
    public AssemblyLine(ILineStep bodyStep, ILineStep springStep, ILineStep barrelStep) {
        this.bodyStep = bodyStep;
        this.springStep = springStep;
        this.barrelStep = barrelStep;
    }
    
    @Override
    public IProduct assembleProduct(IProduct product) {
        System.out.println("Сборка ручки: ");
        
        IProductPart body = bodyStep.buildProductPart();
        product.installFirstPart(body);
        
        IProductPart spring = springStep.buildProductPart();
        product.installSecondPart(spring);
        
        IProductPart barrel = barrelStep.buildProductPart();
        product.installThirdPart(barrel);
        
        System.out.println("Сборка выполнена!");
        return product;
    }
}