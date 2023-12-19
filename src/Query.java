import java.util.*;

public class Query {

    private final String DELIMITER = ";";
    private final String END_OF_LINE_DELIMITER = "\n";

//  To dispatch a query to the right method
    public void queryDispatcher(String query) {
        String[] queryParts = query.split(" ");
        String firstKeyWord = queryParts[0];

        if(firstKeyWord.equalsIgnoreCase("CREATE")){
            String secondKeyWord = queryParts[1];
            if(secondKeyWord.equalsIgnoreCase("DATABASE")){
                String databaseName = queryParts[2];
                createDB(databaseName);
            } else if(secondKeyWord.equalsIgnoreCase("TABLE")){
                String tableName = queryParts[2];
                Table tableInBuffer = createTableInBuffer(query);
                createTable(tableInBuffer);
            }
        }
        else if(firstKeyWord.equalsIgnoreCase("SELECT")){
            Table queriedTable = select(query);
            List<String> queriedOutput = convertTableToStrings(queriedTable);
            queriedOutput.forEach(System.out::println);
        }
    }

//    DDL queries

    //    create DB
    public boolean createDB(String name){
        if(FileUtils.doesDbAlreadyExist()){
            System.out.println("One Database already exists!");
            return false;
        }

        if(FileUtils.createDirectory(name)){
            System.out.println("Successfully created a new database");
            return true;
        }
        System.out.println("Database was not created with name: "+name);
        return false;
    }

//    create table
//    query: CREATE TABLE tableName COLUMNS(col1 type, col2 type, col3 type);
    public Table createTableInBuffer(String tableCreationQuery){
        Table table = new Table();
        String[] parts = tableCreationQuery.split("\\(");
        String tableName = parts[0].trim().split(" ")[2].trim();
        String columnList = parts[1].trim().split("\\)")[0];
        Map<String, String> metaDataMap = new LinkedHashMap<>();
        List<String> columns = new ArrayList<>();
        for(String column: columnList.split(",")){
            String[] columnParts = column.trim().split(" ");
            String columnName = columnParts[0];

            columns.add(columnName);
            metaDataMap.put(columnName, getColumnConstraints(columnParts));
        }
        table.setName(tableName);
        table.setColumnList(columns);
        table.setMetaData(metaDataMap);
        return table;
    }

//    create table in the memory
    public boolean createTable(Table table){
        String currentDb = FileUtils.getDbPathIfExists();
//        check if DB is present
        if(currentDb == null){
            System.out.println("No database is created, cannot add table!");
            FileUtils.logMessage("Table creation error, no database created");
            return false;
        }

//        check if another table exists with the same name
        if(FileUtils.doesTableAlreadyExist(currentDb, table.getName())){
            System.out.println("Table already exists! cannot create a new table with this name: "+table.getName());
            FileUtils.logMessage("Table creation error, table already exists");
            return false;
        }

        String columnString = toColumnString(table.getColumnList());
        String metaDataString = toMetaDataString(table.getMetaData());

        FileUtils.writeToFile(currentDb+"/"+table.getName(), columnString);
        System.out.println("Created a new table with name: "+table.getName());

        String tableMetaDataName = table.getName()+"_metaData";
        FileUtils.writeToFile(currentDb+"/"+tableMetaDataName, metaDataString);
        System.out.println("Created a table metadata at: "+tableMetaDataName);

        return true;
    }

//    SELECT operations
//    query: SELECT * FROM table WHERE col1 = val1;
    public Table select(String selectQuery){
        Table filteredTable = new Table();
        String[] parts = selectQuery.split("WHERE");
        String[] tableInfo = parts[0].split("FROM");
        String tableName = tableInfo[tableInfo.length-1].trim();
        String columnListString = tableInfo[0].split(" ")[1];

        Table originalTable = selectFromDb(tableName);
        if(columnListString.equalsIgnoreCase("*")){
            filteredTable = originalTable;
        }
        else{
            Map<String, List<String>> filtered = new LinkedHashMap<>();
            for(String col: columnListString.split(",")){
                filtered.put(col, originalTable.getColumnsAndValues().get(col));
            }
        }

        FileUtils.logMessage("Executed "+selectQuery);
        return filteredTable;
    }

    public Table selectFromDb(String tableName){
        Table currentTable = new Table();
        String dbName = FileUtils.getDbPathIfExists();
        List<String> selectData = FileUtils.fetchFileData(dbName+"/"+tableName);
//        first line is all columns
        String columns = selectData.get(0);
        currentTable.setColumnList(toColumnList(columns));
        Map<String, List<String>> columnAndValues = new LinkedHashMap<>();

        for(int i=1; i<selectData.size(); i++){
            String[] rowParts = selectData.get(i).split(DELIMITER);
            for(int j=0; j<rowParts.length; j++){
                String col = currentTable.getColumnList().get(j);
                if(!columnAndValues.containsKey(col)){
                    List<String> valuesList = new ArrayList<>();
                    columnAndValues.put(col, valuesList);
                }
                columnAndValues.get(col).add(rowParts[j]);
            }
        }

        currentTable.setColumnsAndValues(columnAndValues);
        FileUtils.logMessage("Fetched Details from table: "+tableName);
        return currentTable;
    }

//    Helper functions
    private List<String> toColumnList(String columns){
        String[] parts = columns.split(END_OF_LINE_DELIMITER)[0].split(DELIMITER);
        List<String> stringList = new ArrayList<>();
        for(String s: parts){
            stringList.add(s);
        }
        return stringList;
    }

    private String toMetaDataString(Map<String, String> metaDataMap){
        StringBuilder metaDataString = new StringBuilder();
        for(String column: metaDataMap.keySet()){
            metaDataString.append(column).append(DELIMITER).append(metaDataMap.get(column)).append(END_OF_LINE_DELIMITER);
        }
        return metaDataString.toString();
    }

    private String toColumnString(List<String> columns){
        StringBuilder column = new StringBuilder();
        for(int i=0; i<columns.size(); i++){
            column.append(columns.get(i));
            column.append(DELIMITER);
        }
        return column.toString();
    }

    private String getColumnConstraints(String[] columnParts){
        StringBuilder columnConstraints = new StringBuilder();
        for(int i=1; i< columnParts.length; i++){
            columnConstraints.append(columnParts[i]);
            if(i != columnParts.length-1 ){
                columnConstraints.append(" ");
            }
        }
        return columnConstraints.toString();
    }

    public List<String> convertTableToStrings(Table table){
        int numberOfRows = table.getColumnsAndValues().get(table.getColumnList().get(0)).size();
        List<String> rowList = new ArrayList<>();

        StringBuilder colRow = new StringBuilder();
        for(String s: table.getColumnList()){
            colRow.append(s).append(" ");
        }
        rowList.add(colRow.toString());

        for(int i=0; i<numberOfRows; i++){
            StringBuilder row = new StringBuilder();
            for(String col: table.getColumnList()){
                row.append(table.getColumnsAndValues().get(col).get(i)).append(" ");
            }
            rowList.add(row.toString());
        }
        return rowList;
    }
}
