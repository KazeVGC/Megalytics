package events;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class dataReader {
    static int fileNumber = 0;

    public static void main(String[] args) {
        File folder = new File("Folders");
        readCsvFilesInDirectory(folder);
    }


    private static void readCsvFilesInDirectory(File folder) {
        String gameVersion;
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                readCsvFilesInDirectory(file);
            } else {
                String fileName = file.getName();
                if (fileName.matches("\\d+\\.csv")) { // Only consider files with numeric names ending in ".csv"
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        // Read the first line to get the name
                        String firstLine = br.readLine();
                        String[] nameFields = firstLine.split(",");
                        String name;
                        try{
                            name = nameFields[0];
                        }catch(Exception e){
                            name = "unknown";
                        }

                        gameVersion = null;
                        // Determine the game version from the second line
                        String line = br.readLine();
                        if (line.contains("Gregar") || line.contains("gregar") ) {
                            gameVersion = "Gregar";
                        } else if (line.contains("Falzar")||line.contains("Falzar")) {
                            gameVersion = "Falzar";
                        }

                        br.readLine();
                        // Read the rest of the lines as JSON objects
                        String[] headers = null;
                        List<JSONObject> jsonList = new ArrayList<>();

                        while ((line = br.readLine()) != null) {
                            String[] fields = line.split(",");
                            if (headers == null) {
                                headers = fields;
                            } else {
                                JSONObject jsonObject = new JSONObject();
                                for (int i = 0; i < fields.length; i++) {
                                    jsonObject.put(headers[i], fields[i]);
                                }
                                jsonList.add(jsonObject);
                            }
                        }

                        if(!jsonList.isEmpty()){
                            // Write data to JSON file
                            try (FileWriter fileWriter = new FileWriter("JSON/" + gameVersion + "_" + name + "_" + fileNumber + ".json")) {
                                JSONArray jsonArray = new JSONArray(jsonList);
                                fileWriter.write(jsonArray.toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            fileNumber++;
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

