package events;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class analytics {
    private Map<String, int[]> chipCountMap;
    private String[] usedChips;
    private int totalFileCount;
    private Map<String, int[]> chipCountMapGregar;
    private Map<String, int[]> chipCountMapFalzar;
    private int fileCountGregar = 0;
    private int fileCountFalzar = 0;
    private chipFinder chipFi = new chipFinder();
    private final int maxEntries = 10;

    public analytics() {
        // Define the directory containing the JSON files
        File jsonDir = new File("JSON/");

// Initialize a map to store the chip count and file count for each chip name
        chipCountMap = new HashMap<>();
        chipCountMapGregar = new HashMap<>();
        chipCountMapFalzar = new HashMap<>();

// Get the total number of JSON files in the directory
        totalFileCount = Objects.requireNonNull(jsonDir.listFiles((dir, name) -> name.endsWith(".json"))).length;

// Iterate over each file in the directory
        for (File jsonFile : jsonDir.listFiles()) {
            // Ignore any files that aren't JSON files
            if (!jsonFile.getName().endsWith(".json")) {
                continue;
            }

            // Extract the game version from the filename
            String fileName = jsonFile.getName();
            String gameVersion = null;
            if (fileName.contains("Gregar")) {
                gameVersion = "Gregar";
                fileCountGregar++;
            } else if (fileName.contains("Falzar")) {
                gameVersion = "Falzar";
                fileCountFalzar++;
            }

            // Read the JSON file into a JSONArray
            JSONArray jsonArray = null;
            try (FileReader reader = new FileReader(jsonFile)) {
                jsonArray = new JSONArray(new JSONTokener(reader));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // Iterate over the JSON array and count the chips
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Map<String, Object> dataMap = jsonObj.toMap();
                String code;
                int count;
                String chipName = "";
                try{
                    chipName = dataMap.get("Chip Name").toString().trim().toLowerCase();
                }catch(Exception e){
                    System.out.println("test");
                }
                try {
                    code = dataMap.get("Code").toString();
                    if(code == ""){
                        code = "*";
                    }
                } catch (Exception e) {
                    code = "*";
                }

                try {
                    count = Integer.parseInt(dataMap.get("#").toString());
                } catch (Exception e) {
                    count = 1;
                }

                // Update the chip count and file count for the current chip name
                String fullName = chipName + " " + code;
                String fullNameFromChipNames = getFullNameFromChipNames(fullName);
                int[] currentCount;
                if (gameVersion != null) {
                    if (gameVersion.equalsIgnoreCase("Gregar")) {
                        currentCount = chipCountMapGregar.getOrDefault(fullNameFromChipNames, new int[]{0, 0});
                        currentCount[0] += count;
                        currentCount[1]++;
                        chipCountMapGregar.put(fullNameFromChipNames, currentCount);
                    } else if (gameVersion.equalsIgnoreCase("Falzar")) {
                        currentCount = chipCountMapFalzar.getOrDefault(fullNameFromChipNames, new int[]{0, 0});
                        currentCount[0] += count;
                        currentCount[1]++;
                        chipCountMapFalzar.put(fullNameFromChipNames, currentCount);
                    }
                }
                currentCount = chipCountMap.getOrDefault(fullNameFromChipNames, new int[]{0, 0});
                currentCount[0] += count;
                currentCount[1]++;
                chipCountMap.put(fullNameFromChipNames, currentCount);
            }
        }
        usedChips = allUsedChips();
    }


    private String getFullNameFromChipNames(String chipName) {
        String[] chipNames = chipFi.getChipNamesWithCodes();
        for (String name : chipNames) {
            if (name.equalsIgnoreCase(chipName)) {
                return name;
            }
        }
        return chipName; // Return the lowercase chipName if no matching fullName found in chipNames
    }


    private List<Map.Entry<String, String>> formatGregarCoverageList() {
        List<Map.Entry<String, String>> percentageList = new ArrayList<>();
        List<Map.Entry<String, int[]>> sortedList = getSortedGregarListByUsage();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.'); // Dezimaltrennzeichen auf Punkt setzen
        DecimalFormat decimalFormat = new DecimalFormat("#.###", symbols); // Format für maximal 3 Stellen nach dem Komma

        for (Map.Entry<String, int[]> entry : sortedList) {
            String str = entry.getKey();
            int fileCount = entry.getValue()[1];

            double usagePercentage = (double) fileCount / fileCountGregar * 100.0;
            String formattedPercentage = decimalFormat.format(usagePercentage); // Begrenzung auf maximal 3 Stellen nach dem Komma

            // Füge fehlende Nullen nach dem Komma hinzu
            if (!formattedPercentage.contains(".")) {
                formattedPercentage += ".000";
            } else {
                int decimalIndex = formattedPercentage.indexOf(".");
                int numDecimals = formattedPercentage.length() - decimalIndex - 1;
                if (numDecimals < 3) {
                    formattedPercentage = String.format("%s%s", formattedPercentage, "0".repeat(3 - numDecimals));
                }
            }

            Map.Entry<String, String> percentageEntry = new AbstractMap.SimpleEntry<>(str, formattedPercentage);
            percentageList.add(percentageEntry);
        }
        return percentageList;
    }

    private List<Map.Entry<String, String>> formatFalzarCoverageList() {
        List<Map.Entry<String, String>> percentageList = new ArrayList<>();
        List<Map.Entry<String, int[]>> sortedList = getSortedFalzarListByUsage();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.'); // Dezimaltrennzeichen auf Punkt setzen
        DecimalFormat decimalFormat = new DecimalFormat("#.###", symbols); // Format für maximal 3 Stellen nach dem Komma

        for (Map.Entry<String, int[]> entry : sortedList) {
            String str = entry.getKey();
            int fileCount = entry.getValue()[1];

            double usagePercentage = (double) fileCount / fileCountFalzar * 100.0;
            String formattedPercentage = decimalFormat.format(usagePercentage); // Begrenzung auf maximal 3 Stellen nach dem Komma

            // Füge fehlende Nullen nach dem Komma hinzu
            if (!formattedPercentage.contains(".")) {
                formattedPercentage += ".000";
            } else {
                int decimalIndex = formattedPercentage.indexOf(".");
                int numDecimals = formattedPercentage.length() - decimalIndex - 1;
                if (numDecimals < 3) {
                    formattedPercentage = String.format("%s%s", formattedPercentage, "0".repeat(3 - numDecimals));
                }
            }

            Map.Entry<String, String> percentageEntry = new AbstractMap.SimpleEntry<>(str, formattedPercentage);
            percentageList.add(percentageEntry);
        }
        return percentageList;
    }


    private List<Map.Entry<String, String>> formatTotalCoverageList() {
        List<Map.Entry<String, String>> percentageList = new ArrayList<>();
        List<Map.Entry<String, int[]>> sortedList = new ArrayList<>(chipCountMap.entrySet());
        sortedList.sort(Comparator.comparing((Map.Entry<String, int[]> entry) -> entry.getValue()[1]).reversed());
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator('.'); // Dezimaltrennzeichen auf Punkt setzen
        DecimalFormat decimalFormat = new DecimalFormat("#.###", symbols); // Format für immer genau 3 Stellen nach dem Komma

        for (Map.Entry<String, int[]> entry : sortedList) {
            String str = entry.getKey();
            int fileCount = entry.getValue()[1];

            double usagePercentage = (double) fileCount / totalFileCount * 100.0;
            String formattedPercentage = decimalFormat.format(usagePercentage); // Begrenzung auf immer genau 3 Stellen nach dem Komma

            // Füge fehlende Nullen nach dem Komma hinzu
            if (!formattedPercentage.contains(".")) {
                formattedPercentage += ".000";
            } else {
                int decimalIndex = formattedPercentage.indexOf(".");
                int numDecimals = formattedPercentage.length() - decimalIndex - 1;
                if (numDecimals < 3) {
                    formattedPercentage = String.format("%s%s", formattedPercentage, "0".repeat(3 - numDecimals));
                }
            }

            Map.Entry<String, String> percentageEntry = new AbstractMap.SimpleEntry<>(str, formattedPercentage);
            percentageList.add(percentageEntry);
        }
        return percentageList;
    }

    public List<String> getFalzarCoverageList() {
        List<String> percentageList = new ArrayList<>();
        List<Map.Entry<String, String>> sortedList = new ArrayList<>(formatFalzarCoverageList());

        for (Map.Entry<String, String> entry : sortedList) {
            String str = entry.getKey();
            String coverage = entry.getValue();

            if (str.endsWith("*")) {
                str = str.substring(0, str.length() - 1) + "\\*"; // Ersetze das *-Zeichen durch \*
            }

            String formattedEntry = "`" + coverage + "%` - " + str;
            percentageList.add(formattedEntry);
        }

        return percentageList;
    }

    public List<String> getGregarCoverageList() {
        List<String> percentageList = new ArrayList<>();
        List<Map.Entry<String, String>> sortedList = new ArrayList<>(formatGregarCoverageList());

        for (Map.Entry<String, String> entry : sortedList) {
            String str = entry.getKey();
            String coverage = entry.getValue();

            if (str.endsWith("*")) {
                str = str.substring(0, str.length() - 1) + "\\*"; // Ersetze das *-Zeichen durch \*
            }

            String formattedEntry = "`" + coverage + "%` - " + str;
            percentageList.add(formattedEntry);
        }

        return percentageList;
    }


    public List<String> getTotalCoverageList() {
        List<String> percentageList = new ArrayList<>();
        List<Map.Entry<String, String>> sortedList = new ArrayList<>(formatTotalCoverageList());

        for (Map.Entry<String, String> entry : sortedList) {
            String str = entry.getKey();
            String coverage = entry.getValue();

            if (str.endsWith("*")) {
                str = str.substring(0, str.length() - 1) + "\\*"; // Ersetze das *-Zeichen durch \*
            }

            String formattedEntry = "`" + coverage + "%` - " + str;
            percentageList.add(formattedEntry);
        }

        return percentageList;
    }




    public String[] getUsedChips()
    {

        return intersection();

    }

    private String[] intersection() {
        String[] arr1 = chipFi.getChipNamesWithCodes();
        List<String> resultList = new ArrayList<String>();
        for (String s : arr1) {
            if (Arrays.asList(usedChips).stream().anyMatch(s2 -> s2.toLowerCase().equals(s.toLowerCase()))) {
                resultList.add(s);
            }
        }
        String[] result = new String[resultList.size()];
        result = resultList.toArray(result);
        return result;
    }



    private String[] allUsedChips(){
        String[] result = new String[chipCountMap.size()];
        int i = 0;
        for (String key : chipCountMap.keySet()) {
            result[i++] = key;
        }
        return result;
    }

    public List<String> getGregarCountList() {
        List<Map.Entry<String, int[]>> sortedList = getSortedGregarListByCount();
        return formatListByCount(sortedList);
    }

    public List<String> getFalzarCountList() {
        List<Map.Entry<String, int[]>> sortedList = getSortedFalzarListByCount();
        return formatListByCount(sortedList);
    }

    public List<String> getTotalCountList() {
        List<Map.Entry<String, int[]>> sortedList = new ArrayList<>(chipCountMap.entrySet());
        sortedList.sort(Comparator.comparing((Map.Entry<String, int[]> entry) -> entry.getValue()[0]).reversed());
        return formatListByCount(sortedList);
    }



    private List<String> formatListByCount(List<Map.Entry<String, int[]>> list) {
        List<String> formattedList = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : list) {
            String str = entry.getKey();
            int count = entry.getValue()[0];

            String formattedEntry = "`" + count + "` - " + str;
            formattedList.add(formattedEntry);
        }

        return formattedList;
    }



    private List<Map.Entry<String, int[]>> getSortedFalzarListByUsage() {
        List<Map.Entry<String, int[]>> list = new ArrayList<>(chipCountMapFalzar.entrySet());
        list.sort((o1, o2) -> Double.compare(
                (double) o2.getValue()[1] / fileCountFalzar,
                (double) o1.getValue()[1] / fileCountFalzar
        ));
        return list;
    }

    private List<Map.Entry<String, int[]>> getSortedFalzarListByCount() {
        List<Map.Entry<String, int[]>> list = new ArrayList<>(chipCountMapFalzar.entrySet());
        list.sort((o1, o2) -> Integer.compare(o2.getValue()[0], o1.getValue()[0]));
        return list;
    }


    private List<Map.Entry<String, int[]>> getSortedGregarListByUsage() {
        List<Map.Entry<String, int[]>> sortedList = new ArrayList<>(chipCountMapGregar.entrySet());
        sortedList.sort(Comparator.comparing((Map.Entry<String, int[]> entry) -> {
            if (entry.getKey() != null) {
                double usagePercentage = (double) entry.getValue()[1] / fileCountGregar * 100.0;
                return usagePercentage;
            } else {
                return null;
            }
        }).reversed());
        return sortedList;
    }

    private List<Map.Entry<String, int[]>> getSortedGregarListByCount() {
        List<Map.Entry<String, int[]>> sortedList = new ArrayList<>(chipCountMapGregar.entrySet());
        sortedList.sort(Comparator.comparingInt((Map.Entry<String, int[]> entry) -> entry.getValue()[0]).reversed());
        return sortedList;
    }


     public List<String> combos(String searchChipAndCode) {
         String[] parts = searchChipAndCode.split(" ");
         String searchChipName = parts[0];
         String code = parts[1];
         // Make sure the search chip name and code are lowercase
         searchChipName = searchChipName.toLowerCase();
         code = code.equals("*") ? code : code.toLowerCase();
         // Search for data for the specified chip name and code in the JSON files
         Map<String, Map<String, Object>> searchDataMap = searchJsonFilesForChip(searchChipName, code);

         // Initialize a map to count the chips
         Map<String, int[]> chipCountMapCombos = new HashMap<>();

         // Iterate over the search results and count the chips
         for (Map.Entry<String, Map<String, Object>> outerEntry : searchDataMap.entrySet()) {
             Map<String, Object> outerDataMap = outerEntry.getValue();

             Set<String> processedChips = new HashSet<>(); // keep track of processed chip names
         for (Map.Entry<String, Object> innerEntry : outerDataMap.entrySet()) {

             Map<String, Object> dataMap = (Map<String, Object>) innerEntry.getValue();
             String chipNameInJson = dataMap.get("Chip Name") != null ? dataMap.get("Chip Name").toString().trim().toLowerCase() : "";
             String codeInJson;
         try {
             codeInJson = dataMap.get("Code").toString().trim().toLowerCase();
             if (codeInJson.equals("")) {
                 codeInJson = "*";
             }
             } catch (Exception e) {
             codeInJson = "*";
            }
            String fullName = chipNameInJson + " " + codeInJson;
            String formattedName = getFullNameFromChipNames(fullName);
         if (processedChips.contains(fullName)) {
            continue; // skip if the chip name and code combination has already been processed in the same file
         }
             processedChips.add(fullName);
             int[] currentCount = chipCountMapCombos.getOrDefault(fullName, new int[]{0, 0});
             currentCount[0] += 1;
             currentCount[1] += 1;
             chipCountMapCombos.put(fullName, currentCount);
             }
         }
         // Sort by count
         List<Map.Entry<String, int[]>> sortedChipCounts = new ArrayList<>(chipCountMapCombos.entrySet());
         Collections.sort(sortedChipCounts, new Comparator<Map.Entry<String, int[]>>() {
             public int compare(Map.Entry<String, int[]> a, Map.Entry<String, int[]> b) {
                 return b.getValue()[0] - a.getValue()[0];
             }
         });

         List<Map.Entry<String, int[]>> modifiedList = new ArrayList<>();

         for (Map.Entry<String, int[]> entry : sortedChipCounts) {
             String key = entry.getKey();
             int[] value = entry.getValue();

             String fullName = getFullNameFromChipNames(key); // Funktion zur Ermittlung des vollständigen Namens

             modifiedList.add(new AbstractMap.SimpleEntry<>(fullName, value));
         }

         List<String> result = new ArrayList<>();

         for (Map.Entry<String, int[]> entry : modifiedList) {
             String key = entry.getKey();

             String[] nameParts = key.split(" ");
             String chipNameToPrint = nameParts[0];
             String codeToPrint = nameParts[1].equals("*") ? "*" : nameParts[1].toUpperCase();
             int usedCount = entry.getValue()[1];
             double percentage = usedCount * 100.0 / searchDataMap.size();
             String percentageStr = String.format("%.3f", percentage); // Auffüllen auf drei Stellen hinter dem Komma
             String formattedEntry = "`" + percentageStr + "%` - " + chipNameToPrint + " " + codeToPrint;
             result.add(formattedEntry);
         }

         return result;

     }



    private Map<String, Map<String, Object>> searchJsonFilesForChip(String chipName, String code) {
        // Define the directory containing the JSON files
        File jsonDir = new File("JSON");

        // Initialize a map to store the data for the specified chip name and code
        Map<String, Map<String, Object>> chipDataMap = new HashMap<>();

        // Iterate over each file in the directory
        for (File jsonFile : jsonDir.listFiles()) {
            // Ignore any files that aren't JSON files
            if (!jsonFile.getName().endsWith(".json")) {
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

            // Iterate over the JSON array and collect the data for the specified chip name and code
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
                if (chipNameInJson.equals(chipName) && (codeInJson.equals(code) || code.equals("*"))) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        Map<String, Object> otherDataMap = jsonObject.toMap();
                        if (!jsonObject.equals(jsonObj)) {
                            chipDataMap.computeIfAbsent(jsonFile.getName(), k -> new HashMap<>()).put(Integer.toString(j), otherDataMap);
                        }
                    }
                    break;
                }
            }
        }

        // Return the collected data for the specified chip name and code
        return chipDataMap;
    }
}

