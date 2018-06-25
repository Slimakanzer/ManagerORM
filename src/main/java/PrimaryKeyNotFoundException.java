import java.sql.SQLException;

public class PrimaryKeyNotFoundException extends NullPointerException {
    private ManagerORM manager;
    private String nameSecondTable;
    PrimaryKeyNotFoundException(ManagerORM manager, String nameSecondTable){
        this.nameSecondTable=nameSecondTable;
        this.manager=manager;
        System.out.println(getMessage());
    }

    @Override
    public String getMessage(){
        manager.dropTable();
        return "Отсутствует PK у таблицы "+getNameTable()+" для связи с таблицей "+nameSecondTable+". Таблица удалена.";
    }

    public String getNameTable() {
        return manager.getNameTable();
    }
}