package events;


public class megalytics {
    public static void main(String[] args) throws Exception {
        analytics anly = new analytics();
        folderFinder folderFinder = new folderFinder();
        chipReader cr = new chipReader();
        chipFinder cf = new chipFinder();

        //cf.convertToJSON("D:\\Megalytics\\untitled\\chipSearch\\allChips.txt", "D:\\Megalytics\\untitled\\chipSearch\\sorted.json");

        //System.out.println(cf.formatChipData("ColArmy"));

        String[] allChips = anly.getUsedChips();

        for(String string : allChips)
        System.out.println(string);
    }
}
