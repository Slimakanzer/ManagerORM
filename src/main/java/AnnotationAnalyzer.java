import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class AnnotationAnalyzer {
    private String nameTable;
    private ArrayList<String> nameColumns;
    private ArrayList<Field> fields;
    private String primaryKey;
    private Field fieldPrimaryKey;

    private Class<?> tClass;
    AnnotationAnalyzer(Class<?> tClass){
        this.tClass=tClass;
        nameColumns=new ArrayList<>();
        fields = new ArrayList<>();
        analyze();

    }
    private void nameTable(){
        try {
            nameTable = tClass.getDeclaredAnnotation(Table.class).name();
        }catch (NullPointerException e){

        }

    }


    /**
     * Create names of columns
     *
     *
     */
    private void nameColumns(){

        Arrays.stream(tClass.getDeclaredFields())
                .forEach(
                e ->{
                    if (e.getAnnotation(Column.class)!=null){
                        nameColumns.add(e.getAnnotation(Column.class).name());
                        fields.add(e);

                        if(e.getAnnotation(PrimaryKey.class)!=null){
                            primaryKey=e.getAnnotation(Column.class).name();
                            fieldPrimaryKey=e;
                        }
                    }
                }
        );
    }

    private void analyze(){
        nameTable();
        nameColumns();

    }

    public String getNameTable() {
        return nameTable;
    }

    public ArrayList<Field> getFields() {
        return fields;
    }

    public ArrayList<String> getNameColumns() {
        return nameColumns;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Field getFieldPrimaryKey() {
        return fieldPrimaryKey;
    }
}
