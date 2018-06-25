public class ObjectAlreadyExistSQL extends Exception {
    private String nameTable;
    ObjectAlreadyExistSQL(String nameTable){
        this.nameTable=nameTable;
        System.out.println(getMessage());
    }

    @Override
    public String getMessage(){
        return "Такой объект в таблице "+nameTable+" уже существует";
    }
}
