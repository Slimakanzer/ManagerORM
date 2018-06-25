import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

interface ORMInterface<T>{

    ResultSet executeQuery(String query);

    void create() throws TableAlreadyExistSQLException;

    boolean insert(T object);

    boolean update(T object);

    boolean delete(T object);

    void dropTable() throws TableAlreadyExistSQLException;

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
    private Class<?> aClass;
    protected static final String TABLE_ALREADY_EXIST_CODE="42P07";
    protected static final String OBJECT_ALREADY_EXIST_CODE="23505";


    public AbstractManagerORM(Class<?> tClass, String url, String login, String password){
        try{
            connection=DriverManager.getConnection(url, login, password);
            this.aClass = tClass;
            AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(tClass);
            this.nameTable=annotationAnalyzer.getNameTable();
            this.fields=annotationAnalyzer.getFields();
            this.columns=annotationAnalyzer.getNameColumns();
            this.primaryKey=annotationAnalyzer.getPrimaryKey();
            this.fieldPrimaryKey=annotationAnalyzer.getFieldPrimaryKey();
            this.fieldsWithType = new HashMap<>();
            this.gson=new Gson();
        }catch (SQLException e){
            System.out.println("Нет подключения к БД");
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

    public Class<?> getaClass() {
        return aClass;
    }
}



public class ManagerORM<T> extends AbstractManagerORM<T> {

    ManagerORM(Class<?> tClass, String url, String login, String password){
        super(tClass, url, login, password);
    }


    /**
     * Этот метод создаёт в базе банных таблицу
     *
     */
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
        try {
            getConnection().createStatement().execute(query);
            System.out.println(query);
        }catch (SQLException e){
            //e.printStackTrace();
            if (e.getSQLState().equals(TABLE_ALREADY_EXIST_CODE)) {

                try {
                    throw new TableAlreadyExistSQLException(getNameTable());
                } catch (TableAlreadyExistSQLException e1) {
                }
            }else {
                System.out.println("Какие-то проблемы с БД");
            }
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


    /**
     *
     *
     * @param object - подаётся объект, который нужно сохранить в БД
     * @return
     */
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

            if(e.getSQLState().equals(OBJECT_ALREADY_EXIST_CODE)){
                try{
                    throw new ObjectAlreadyExistSQLException(getNameTable());
                }catch (ObjectAlreadyExistSQLException e1){
                }
            }
        }
        return false;
    }

    /**
     * Не реализован
     * @param object
     * @return
     */
    @Override
    public boolean update(T object) {
       return true;
    }

    /**
     * Удаляется объект по PK
     * @param object
     * @return
     */
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


    /**
     *
     * Удаляется таблица
     */
    @Override
    public void dropTable(){
        try {
            getConnection().createStatement().executeUpdate("drop table "+getNameTable());
        }catch (SQLException e){
        }
    }


    /**
     *
     * Определяется тип поля для создания БД
     */
    public String getType(Field field){
        Class type = field.getType();

        if(type==String.class){
            return "text";
        }else if (type==int.class){
            return "integer";
        }else if (type==double.class) {
            return "double precision";
        }else if (type==boolean.class) {
            return "boolean";
        }else if(type==float.class){
            return "real";
        }else{
            AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(type);

            if(annotationAnalyzer.getNameTable()!=null){
                ManagerORM managerORM = new ManagerORM(type,DatabaseProtocol.url,DatabaseProtocol.login,DatabaseProtocol.password);

                getFieldsWithType().put(field, managerORM.getNameTable());
                if(getaClass() != type) {
                    managerORM.create();
                }
                try {
                    return managerORM.getType(annotationAnalyzer.getFieldPrimaryKey());
                }catch (NullPointerException e){
                    try{
                        throw new PrimaryKeyNotFoundException(managerORM, getNameTable());
                    }catch (PrimaryKeyNotFoundException e1){
                        System.exit(1);
                    }
                }
            }else {
                return "jsonb";
            }

        }
        return null;
    }


    /**
     *
     *Определяется значение поля у данного объекта
     */
    public String getValue(Field field, T object){
        Object result =null;

        try {
            result = field.get(object);
        }catch (IllegalAccessException e){
            //e.printStackTrace();
            System.out.println("Нулевое значение");
        }

        if(result instanceof String ){
            return "'"+result+"'";
        }else if (Integer.class.isInstance(result)){
            return Integer.toString((int)result);
        }else if (Double.class.isInstance(result)) {
            return Double.toString((double) result);
        }else if (Boolean.class.isInstance(result)) {
            return Boolean.toString((boolean) result);
        }else if (Float.class.isInstance(result)){
            return Float.toString((float)result);
        }else if (result==null) {
            return null;

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


    /**
     * Этот метод собирает объект по частям из БД
     * На вход метода подаётся ResultSet, где уже хранится кортеж
     *
     */
    public T getElement(ResultSet resultSet){
        Field[] fields = getaClass().getDeclaredFields();

        try {
            Object object=getaClass().newInstance();

            Arrays.stream(fields).forEach(
                    e->{
                        if(e.getAnnotation(Column.class)!=null) {
                            e.setAccessible(true);
                            Object type = e.getType();

                            try {
                                try {
                                    if (type == int.class) {
                                        e.set(object, Integer.parseInt(resultSet.getString(e.getAnnotation(Column.class).name())));
                                    } else if (type == double.class) {
                                        e.set(object, Double.parseDouble(resultSet.getString(e.getAnnotation(Column.class).name())));
                                    } else {
                                        AnnotationAnalyzer annotationAnalyzer = new AnnotationAnalyzer(e.getType());
                                        if (annotationAnalyzer.getNameTable() != null) {


                                            annotationAnalyzer.getFieldPrimaryKey().setAccessible(true);
                                            ManagerORM managerORM = new ManagerORM(e.getType(),DatabaseProtocol.url,DatabaseProtocol.login,DatabaseProtocol.password);
                                            ResultSet resultSet1;

                                                resultSet1 = managerORM.executeQuery("select * from "
                                                        + managerORM.getNameTable()
                                                        + " where "
                                                        + managerORM.getPrimaryKey()
                                                        + " = " + "'"+resultSet.getString(e.getAnnotation(Column.class).name())+"'"
                                                );

                                            while (resultSet1.next()){
                                                e.set(object, managerORM.getElement(resultSet1));

                                            }



                                        } else {
                                            e.set(object, getGson().fromJson(resultSet.getString(e.getAnnotation(Column.class).name()), e.getType()));
                                        }
                                    }
                                }catch (IllegalAccessException e1){
                                    e1.printStackTrace();
                                }
                            }catch (SQLException f){
                                f.printStackTrace();
                            }



                        }

                    }
            );

            return (T) object;


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;

    }



}
