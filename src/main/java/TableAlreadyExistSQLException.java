import java.sql.SQLException;

public class TableAlreadyExistSQLException extends SQLException {
    private String nameTable;
    TableAlreadyExistSQLException(String nameTable){
        this.nameTable=nameTable;
        System.out.println(getMessage());
    }

    @Override
    public String getMessage(){
        return "Таблица "+getNameTable()+" уже сущестует";
    }

    public String getNameTable() {
        return nameTable;
    }
}
