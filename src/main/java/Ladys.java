
@Table(name = "Ladys")
public enum  Ladys {
    one("one"),
    two("two"),
    three("three"),
    fourth("fourth");

    @PrimaryKey
    @Column(name = "name")
    private String name;

    Ladys(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}
