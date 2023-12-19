import java.util.List;
import java.util.Map;

public class Table {
    private String name;
    private List<String> columnList;
    private Map<String, List<String>> columnsAndValues;
    private Map<String, String> metaData;
    private String tablePath;

    public Table() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<String> columnList) {
        this.columnList = columnList;
    }

    public Map<String, List<String>> getColumnsAndValues() {
        return columnsAndValues;
    }

    public void setColumnsAndValues(Map<String, List<String>> columnsAndValues) {
        this.columnsAndValues = columnsAndValues;
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public String getTablePath() {
        return tablePath;
    }

    public void setTablePath(String tablePath) {
        this.tablePath = tablePath;
    }
}
