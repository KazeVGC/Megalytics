package events;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class folderFinder {

    public folderFinder(){}

    public List<String[]> formatAllFilesWithChips(String[] chipData) {
        // Call the private method to search for the chip data
        Map<String, List<Map<String, Object>>> fileDataMap = searchJsonFilesForChips(chipData);

        List<String[]> outputList = new ArrayList<>();

        // Iterate over each file containing all the chip data
        for (Map.Entry<String, List<Map<String, Object>>> entry : fileDataMap.entrySet()) {
            String fileName = entry.getKey();
            List<Map<String, Object>> fileDataList = entry.getValue();



            // Format the data for the file into a string

            String currentVersion = "";
            String currentCreator = "";

            List<String[]> sectionList = new ArrayList<>();
            StringBuilder chipInfoBuilder = new StringBuilder();
            List<String> programNameList = new ArrayList<>();

// Iterate over the data for the file
            for (Map<String, Object> dataMap : fileDataList) {
                String version = fileName.startsWith("null") ? "Not Specified" : fileName.split("_")[0];
                String creator = fileName.startsWith("null") ? "Not Specified" : fileName.split("_")[1];

                if (!version.equals(currentVersion) || !creator.equals(currentCreator)) {
                    // Start of a new section, add the previous section to the output list
                    if (!sectionList.isEmpty()) {
                        String[] sectionArray = sectionList.stream().flatMap(Arrays::stream).toArray(String[]::new);
                        outputList.add(sectionArray);
                        sectionList.clear();
                    }

                    // Update the current version and creator
                    currentVersion = version;
                    currentCreator = creator;

                    // Add version and creator as separate strings in the section
                    sectionList.add(new String[]{"Version: " + currentVersion});
                    sectionList.add(new String[]{"Creator: " + currentCreator});
                }

                String chipNameCode = String.format("%s %s", dataMap.get("Chip Name"), dataMap.get("Code"));
                String regTag = dataMap.containsKey("Reg/Tag") ? dataMap.get("Reg/Tag").toString() : "";

                String chipInfo = String.format("`%s` - %s %s", dataMap.get("#"), chipNameCode, regTag);
                chipInfoBuilder.append(chipInfo).append("\n");

                List<String> programNames = new ArrayList<>();

                if (dataMap.containsKey("Program Name")) {
                    String programNameStr = dataMap.get("Program Name").toString();
                    if (!programNameStr.equals("CellImage")) {
                        programNames = new ArrayList<>(Arrays.asList(programNameStr.split(", ")));
                        programNameList.addAll(programNames);
                    }
                }

                if (dataMap.containsKey("Link")) {
                    String linkStr = dataMap.get("Link").toString();
                    if (!linkStr.equals("image") && !linkStr.equals("Link to URL") && !linkStr.startsWith("https")) {
                        programNames.add(linkStr);
                        programNameList.add(linkStr);
                    }
                }
            }

// Remove empty entries from the programNameList
            programNameList.removeIf(String::isEmpty);

// Combine sectionList, chipInfoBuilder, and programNameList into a single string array
            String[] combinedSectionArray = new String[sectionList.size() + 2];
            for (int i = 0; i < sectionList.size(); i++) {
                combinedSectionArray[i] = sectionList.get(i)[0];
            }

            combinedSectionArray[sectionList.size()] = chipInfoBuilder.toString();
            combinedSectionArray[sectionList.size() + 1] = String.join("\n", programNameList);

// Add the combined content as a single section to the output list
            outputList.add(combinedSectionArray);

        }
        return outputList;

    }












    private Map<String, List<Map<String, Object>>> searchJsonFilesForChips(String[] chipData) {
        String[] searchData = removeNullValues(chipData);
        String[][] chipDataSplit = new String[searchData.length][2];
        for (int i = 0; i < searchData.length; i++) {
            String[] split = searchData[i].split(" ");
            chipDataSplit[i][0] = split[0]; // Chipname
            chipDataSplit[i][1] = split[1]; // Code
        }
        // Define the directory containing the JSON files
        File jsonDir = new File("JSON");

        // Initialize a map to store the data for each file containing all datasets
        Map<String, List<Map<String, Object>>> fileDataMap = new HashMap<>();

        // Iterate over each file in the directory
        for (File jsonFile : jsonDir.listFiles()) {
            // Ignore any files that aren't JSON files or whose name starts with "null"
            if (!jsonFile.getName().endsWith(".json") || jsonFile.getName().startsWith("null")) {
                continue;
            }

            // Read the JSON file into a JSONArray
            JSONArray jsonArray = null;
            try (FileReader reader = new FileReader(jsonFile)) {
                jsonArray = new JSONArray(new JSONTokener(reader));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // Initialize a map to store the data for each dataset in the file
            Map<Integer, Map<String, Object>> fileChipDataMap = new HashMap<>();

            // Iterate over the JSON array and collect the data for each dataset in the input
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Map<String, Object> dataMap = jsonObj.toMap();
                String chipNameInJson = dataMap.get("Chip Name").toString().trim().toLowerCase();
                String codeInJson;
                try {
                    codeInJson = dataMap.get("Code").toString().toLowerCase();
                    if(codeInJson == ""){
                        codeInJson = "*";
                    }
                } catch (Exception e) {
                    codeInJson = "*";
                }

                // Check if the current dataset matches any of the input datasets
                for (int j = 0; j < chipDataSplit.length; j++) {
                    String[] dataset = chipDataSplit[j];
                    String chipName = dataset[0].toLowerCase().trim();
                    String code = dataset[1].toLowerCase().trim();

                    if (chipNameInJson.equals(chipName) && (codeInJson.equals(code) || code.equals("*"))) {
                        fileChipDataMap.put(j, dataMap);
                    }
                }
            }

            // Check if all datasets were found in the file and add it to the fileDataMap if so
            if (fileChipDataMap.size() == chipDataSplit.length) {
                List<Map<String, Object>> fileDataList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    Map<String, Object> dataMap = jsonObj.toMap();
                    fileDataList.add(dataMap);
                }
                fileDataMap.put(jsonFile.getName(), fileDataList);
            }
        }

        // Return the collected data for each file containing all datasets
        return fileDataMap;
    }



    private String[] removeNullValues(String[] data) {
        List<String> dataList = new ArrayList<>();

        for (String value : data) {
            if (value != null && !value.trim().isEmpty()) {
                dataList.add(value);
            }
        }

        String[] nonNullData = new String[dataList.size()];
        for (int i = 0; i < dataList.size(); i++) {
            nonNullData[i] = dataList.get(i);
        }

        return nonNullData;
    }






}
