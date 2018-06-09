import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

interface ORMInterface<T>{

    ResultSet executeQuery(String query);

    void create();

    boolean insert(T object);

    boolean update(T object);

    boolean delete(T object);

    void dropTable();

}


abstract class AbstractManagerORM<T> implements ORMInterface<T>{
    private Connection connection;
    private String nameTable;
    private  List<String> columns;
    private  List<Field> fields;
    private String primaryKey;
    private Field fieldPrimaryKey;
    private Map<Field, String> fieldsWithType;
    private Gson gson;


    public AbstractManagerORM(Class<?> tClass, String url, String login, String password){
        try{
            connection=DriverManager.getConnection(url, login, password);
            AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(tClass);
            this.nameTable=annotationAnalyzer.getNameTable();
            this.fields=annotationAnalyzer.getFields();
            this.columns=annotationAnalyzer.getNameColumns();
            this.primaryKey=annotationAnalyzer.getPrimaryKey();
            this.fieldPrimaryKey=annotationAnalyzer.getFieldPrimaryKey();
            this.fieldsWithType = new HashMap<>();
            this.gson=new Gson();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public List<String> getColumns() {
        return columns;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getNameTable() {
        return nameTable;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Field getFieldPrimaryKey() {
        return fieldPrimaryKey;
    }

    public Map<Field, String> getFieldsWithType() {
        return fieldsWithType;
    }

    public Gson getGson() {
        return gson;
    }
}



public class ManagerORM<T> extends AbstractManagerORM<T> {

    ManagerORM(Class<?> tClass, String url, String login, String password){
        super(tClass, url, login, password);
    }


    @Override
    public void create(){
        String query = "create table "+getNameTable()+"(\n";

        for(int i=0; i<getFields().size(); i++){

            String type = getType(getFields().get(i));
            String nameTableReferences = getFieldsWithType().get(getFields().get(i));

            if(nameTableReferences!=null) {

                query += getColumns().get(i) + " " + type + " references "+nameTableReferences+" on delete cascade,\n";


            }else {
                query += getColumns().get(i) + " " + type + " ,\n";
            }

        }

        if(getPrimaryKey()!=null) {
            query += "primary key (" + getPrimaryKey() + "))";
        }else {
            query=query.substring(0,query.length()-2)+")";
        }
        System.out.println(query);
        try {
            getConnection().createStatement().execute(query);
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Таблица "+getNameTable()+" уже существует");
        }
    }

    @Override
    public ResultSet executeQuery(String query) {
        try{
            return getConnection().createStatement().executeQuery(query);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insert(T object) {
        StringBuilder result = new StringBuilder();
        result.append("insert into "+getNameTable()+" values(");

        getFields().stream().forEach(
                e-> {
                    e.setAccessible(true);
                    result.append(getValue(e, object)+",");
                }
        );

        result.deleteCharAt(result.length()-1);
        result.append(")");
        try {
            System.out.println(result.toString());
            getConnection().createStatement().executeUpdate(result.toString());
            return true;
        }catch (SQLException e){
            //e.printStackTrace();
            System.out.println("Объект в таблице "+getNameTable()+" уже существует");
        }
        return false;
    }

    @Override
    public boolean update(T object) {
       return true;
    }

    @Override
    public boolean delete(T object) {
        StringBuilder result = new StringBuilder();

        result.append("delete from "+getNameTable()+" where "+getPrimaryKey()+" = "+getValue(getFieldPrimaryKey(), object));

        try {
            getConnection().createStatement().executeUpdate(result.toString());

            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void dropTable() {
        try {
            getConnection().createStatement().executeUpdate("drop table "+getNameTable());
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public String getType(Field field){
        Class type = field.getType();

        if(type==String.class){
            return "varchar(30)";
        }else if (type==int.class){
            return "integer";
        }else if (type==double.class){
            return "double precision";
        }else{
            AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(type);

            if(annotationAnalyzer.getNameTable()!=null){
                ManagerORM managerORM = new ManagerORM(type,DatabaseProtocol.url,DatabaseProtocol.login,DatabaseProtocol.password);

                getFieldsWithType().put(field, managerORM.getNameTable());
                managerORM.create();
                return managerORM.getType(annotationAnalyzer.getFieldPrimaryKey());
            }else {
                return "jsonb";
            }

        }
    }

    public String getValue(Field field, T object){
        Object result =null;

        try {
            result = field.get(object);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }

        if(result instanceof String ){
            return "'"+result+"'";
        }else if (Integer.class.isInstance(result)){
            return Integer.toString((int)result);
        }else if (Double.class.isInstance(result)){
            return Double.toString((double)result);
        }else {

            AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(field.getType());
            if(annotationAnalyzer.getNameTable()!=null){

                    annotationAnalyzer.getFieldPrimaryKey().setAccessible(true);


                    ManagerORM managerORM = new ManagerORM(field.getType(),DatabaseProtocol.url,DatabaseProtocol.login,DatabaseProtocol.password);
                    managerORM.insert(result);

                    return managerORM.getValue(annotationAnalyzer.getFieldPrimaryKey(), result);

            }else {
                return "'"+getGson().toJson(result)+"'";
            }

        }

    }



}
