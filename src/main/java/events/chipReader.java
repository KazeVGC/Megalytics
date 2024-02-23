package events;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class chipReader {

    public chipReader(){}

    public static void main(String[] args) {
        List<JSONObject> chips = new ArrayList<>();
        JSONObject currentChip = null;

        try (BufferedReader br = new BufferedReader(new FileReader("chipSearch/alle chips raw"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.matches("\\d{3} .*")) {
                    // this line is the start of a new chip
                    if (currentChip != null) {
                        // if there was a previous chip, add it to the list
                        chips.add(currentChip);
                    }
                    currentChip = new JSONObject();
                    currentChip.put("name", line.substring(4));
                    currentChip.put("codes", new JSONArray());
                    currentChip.put("traders", new JSONArray());
                } else if (currentChip != null) {
                    if (line.isEmpty()) {
                        // this line is a separator between the name and the codes
                        continue;
                    }
                    if (line.startsWith("Traders: ")) {
                        // this line has the traders information for the current chip
                        String[] traders = line.substring(9).split(",");
                        JSONArray tradersArray = new JSONArray();
                        for (String trader : traders) {
                            tradersArray.put(trader.trim());
                        }
                        currentChip.put("traders", tradersArray);
                    } else {
                        // this line is a code for the current chip
                        currentChip.getJSONArray("codes").put(line);
                    }
                }
            }
            // add the last chip to the list
            if (currentChip != null) {
                chips.add(currentChip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // write the chips list as a JSON file
        try (FileWriter fw = new FileWriter("chipSearch/allChips.json")) {
            fw.write(new JSONArray(chips).toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}



