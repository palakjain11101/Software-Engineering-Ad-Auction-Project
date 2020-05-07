package Model;

public class GraphPoint {

    private double x;
    private double y;
    private double Ydenom;
    private double Ynum;
    private boolean outlier;

    public GraphPoint(double x, double y){
        this.x = x;
        this.y = y;
        this.Ynum = y;
        this.Ydenom = 0;
        this.outlier = false;
    }

    public GraphPoint(double x, double num, double denom){
        this.x = x;
        this.y = denom == 0 ? 0 : num/denom;
        this.Ydenom = denom;
        this.Ynum = num;
        this.outlier = false;
    }

    public boolean getOutlier() { return outlier; }

    public void setOutlier() { this.outlier = true; }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getYdenom() {return Ydenom;}

    public double getYnum() {return Ynum;}
}
