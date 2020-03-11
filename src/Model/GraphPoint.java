package Model;

public class GraphPoint {

    private double x;
    private double y;
    private double denom;

    public GraphPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public GraphPoint(double x, double num, double denom){
        this.x = x;
        this.y = num/denom;
        this.denom = denom;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDenom() {return denom;}
}
