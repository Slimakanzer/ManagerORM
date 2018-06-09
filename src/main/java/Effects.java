
@Table(name = "Effects")
public enum Effects {
    Fire("Fire"),
    Cold("Cold"),
    XRay("XRay");

    @PrimaryKey
    @Column(name = "name")
    private String name;
    public String getName() {
        return name;
    }
    Effects(String name){
        this.name=name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
