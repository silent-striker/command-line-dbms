import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileUtils {

    private static final String DELIMITER = ";";
    private static final String NEW_LINE_DELIMITER = "\n";
    private static final String BASE_PATH = "/Users/silentstriker/Documents/Semester 1/CSCI 5408 Data Mgmt/Assignments/Assignment 1/database/";
    private static final String USER_FILE_PATH = BASE_PATH+"users.txt";
    private static final String LOG_FILE_PATH = BASE_PATH+"logs.txt";

    public static List<String> fetchFileData(String fileName){
        File file = new File(getFilePathFromName(fileName));
        List<String> fileData = new ArrayList<>();

    //        file is not a directory and doesn't exist, then we cannot fetch data
        if(file.isFile() && !file.exists()){
            return fileData;
        }

        try{
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String currentLine = bufferedReader.readLine();
            while(currentLine != null){
                fileData.add(currentLine);
                currentLine = bufferedReader.readLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return fileData;
    }

    public String readFromFile(String filePath){
        return null;
    }

//    Write operations
    public static boolean createDirectory(String name){
        try{
            String filePath = getFilePathFromName("basePath") + "/"+name;
            File file = new File(filePath);
            return file.mkdirs();
        } catch(Exception e){
            logMessage("Exception in creating Directory of name: "+name+" exception: "+ e.getMessage());
            return false;
        }
    }

    public static boolean writeToFile(String fileName, String content) {
        try {
            File file = new File(getFilePathFromName(fileName));
            String messageToWrite = content + NEW_LINE_DELIMITER;

            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(messageToWrite);
            writer.close();
            fileWriter.close();
        } catch (Exception e) {
            logMessage("Exception in writing to fileName: "+fileName+" exception: "+e.getMessage());
            return false;
        }
        return true;
    }

    public static void logMessage(String message){
        File file = new File(LOG_FILE_PATH);

        String currentTime = getCurrentTime();
        String log = currentTime+DELIMITER+message+NEW_LINE_DELIMITER;

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write(log);
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    helper functions

//    filter to get Directory based on File.isDirectory()
    private static FileFilter getDirectoryFilter(){
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isDirectory()){
                    return true;
                }
                return false;
            }
        };
    }


    public static String getDbPathIfExists(){
        File file = new File(BASE_PATH);
        File[] files = file.listFiles(getDirectoryFilter());
        return files != null && files.length > 0 ? files[0].getName(): null;
    }

    public static boolean doesDbAlreadyExist(){
        File file = new File(BASE_PATH);
//        to filter only directories as our DB is a directory
        File[] files = file.listFiles(getDirectoryFilter());
        return files != null && files.length > 0;
    }

//    check if a table already exists
    public static boolean doesTableAlreadyExist(String dbName, String tableName){
        File file = new File(BASE_PATH+"/"+dbName+"/"+tableName);
        return file.exists();
    }

//    Returns the current system time in string used for logging
    private static String getCurrentTime(){
        Instant instant = Instant.now();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss:SSS");
        return zonedDateTime.format(formatter);
    }

    public static String getFilePathFromName(String fileName){
        switch (fileName){
            case "users":
                return USER_FILE_PATH;
            case "basePath":
                return BASE_PATH;
            default:
                return BASE_PATH+"/"+fileName;
        }
    }
}
