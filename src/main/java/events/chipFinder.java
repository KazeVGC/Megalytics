package events;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class chipFinder {
    private Map<String, Map<String, List<String>>> _chipMap;
    public chipFinder(){
        try{
            this._chipMap = jsonToChipMap();
        }catch(Exception e){
            System.out.println("Reading the 'alleChips.json' did not work!");
        }
    }

    public String[] getChipNamesWithCodes() {
        List<String> result = new ArrayList<>();

        for (String chipName : _chipMap.keySet()) {
            List<String> codes = _chipMap.get(chipName).get("codes");
            for (int i = 0; i < codes.size(); i += 2) {
                String code = codes.get(i);
                if (code.equals("✱")) {
                    result.add(chipName + " *");
                } else {
                    result.add(chipName + " " + code);
                }
            }
        }

        Collections.sort(result); // sort by chip name

        // filter out entries longer than 2 after the " "
        result.removeIf(str -> str.substring(str.indexOf(" ") + 1).length() > 1);

        return result.toArray(new String[0]);
    }



    public String[] getAllChips() {
        List<String> sortedKeys = new ArrayList<>(_chipMap.keySet());
        Collections.sort(sortedKeys);
        return sortedKeys.toArray(new String[sortedKeys.size()]);
    }


    public String findChipData(String chipName) {
        if (_chipMap.containsKey(chipName)) {
            Map<String, List<String>> chipData = _chipMap.get(chipName);

            StringBuilder builder = new StringBuilder();

            // Format codes
            builder.append(formatCodes(chipData.get("codes"))).append("\n");

            // Format traders
            builder.append("Traders: ");
            builder.append(String.join(", ", chipData.get("traders")));

            return builder.toString();
        } else {
            return "You were too fast for me or there is no Chip like that. Please try again";
        }
    }

    private String formatCodes(List<String> codes) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < codes.size(); i += 2) {
            String code = codes.get(i);
            String value = i + 1 < codes.size() ? codes.get(i + 1) : "";

            builder.append(code).append(": ").append(value);

            if (i < codes.size() - 2) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }
    private Map<String, Map<String, List<String>>> jsonToChipMap() throws IOException {
        // read JSON file into a String
        String jsonString = "";
        BufferedReader reader = new BufferedReader(new FileReader("chipSearch/allChips.json"));
        String line;
        while ((line = reader.readLine()) != null) {
            jsonString += line;
        }
        reader.close();

        // parse JSON string into JSONArray
        JSONArray jsonArray = new JSONArray(jsonString);

        // iterate over JSONArray and populate map
        Map<String, Map<String, List<String>>> map = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            jsonObject.remove("name"); // remove "name" field from JSON object
            Map<String, List<String>> innerMap = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray jsonArrayInner = jsonObject.getJSONArray(key);
                List<String> innerList = new ArrayList<>();
                for (int j = 0; j < jsonArrayInner.length(); j++) {
                    innerList.add(jsonArrayInner.getString(j));
                }
                innerMap.put(key, innerList);
            }
            map.put(name, innerMap);
        }
        return map;
    }
}

