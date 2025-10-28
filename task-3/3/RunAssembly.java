public  class  RunAssembly{
    public static void main(String[] args) {
        ILineStep bodyStep = new BodyStepLine();
        ILineStep springStep = new SpringStepLine();
        ILineStep barrelStep = new BarrelStepLine();
        IAssemblyLine assemblyLine = new AssemblyLine(bodyStep,springStep,barrelStep);
        IProduct pen = new Pen();
        IProduct createdPen = assemblyLine.assembleProduct(pen);
        System.out.println("Изготовлено: " + createdPen);

    }
}