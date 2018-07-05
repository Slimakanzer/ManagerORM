import java.awt.*;


@Table(name = "Rays")
public  class Rays {
    @PrimaryKey
    @Column(name = "id")
    private int id;

    @Collection
    @Column(name = "collection")
    private String collection;

    @Column(name = "damage")
    private int damage;

    @Column(name = "name")
    private String name;

    @Column(name = "speed")
    private double speed;

    @Column(name = "Effects")
    private Effects effect;

    @Column(name = "float_test")
    private float fl;

    @Column(name = "boolean_test")
    private boolean bool;


    @Column(name = "x1")
    private double x1;

    @Column(name = "x2")
    private double x2;

    @Column(name = "y1")
    private double y1;

    @Column(name = "y2")
    private double y2;

    @Column(name = "color")
    public Color color = Color.RED;

    @Column(name = "Rays")
    public Rays rays = null;


    @Column(name = "Ladys")
    public Ladys ladys;


    public Rays(int id, Rays rays){
        this.id=id;
        this.collection="col";
        this.name="Базовый";
        this.speed=0.001;
        this.damage=0;
        this.effect=Effects.Fire;
        this.x1=0;
        this.y1=0;
        this.x2=1;
        this.y2=1;
        this.color= Color.BLACK;
        this.rays=rays;
        this.fl=0.35f;
        this.bool=true;
        this.ladys=Ladys.three;

    }

    public Rays(){
        this.id=1;
        this.collection="col";
        this.name="Базовый";
        this.speed=0.001;
        this.damage=0;
        this.effect=Effects.Fire;
        this.x1=0;
        this.y1=0;
        this.x2=1;
        this.y2=1;
        this.color= Color.BLACK;
        this.fl=0.35f;
        this.bool=true;

    }
    public Rays(int id){
        this.id=id;
        this.collection="col";
        this.name="Базовый";
        this.speed=1010101;
        this.damage=0;
        this.effect=Effects.Fire;
        this.x1=0;
        this.y1=0;
        this.x2=1;
        this.y2=1;
        this.color= Color.BLACK;
        this.fl=0.35f;
        this.bool=true;
    }

    public Rays(String name, int damage, Effects effect, double speed){
        this.damage=damage;
        this.name=name;
        //this.effect=effect;
        this.speed=speed;
    }
    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public Effects getEffect() {
        return effect;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public Color getColor() {
        return color;
    }

    public void setPosition(double x1, double y1, double x2, double y2){
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
    }

    public void setName(String name) {
        this.name = name;
    }
}