package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;


import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class interactionEventListener extends ListenerAdapter {
    analytics anly = new analytics();
    folderFinder folFi = new folderFinder();
    chipFinder chipFi = new chipFinder();
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        List<String> chipList;
        int maxPage;
        String[] chart;
        String chipNames;
        String chipCoverage;



        //event.deferReply().queue();

        //Embed Builder for the Reply
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode("#AAFF00"));
        // TODO: Title based on function used!!
        builder.setTitle("Test-Title");
        List<Button> buttons = new ArrayList<Button>();



        switch (event.getName()){
            case "gregar-by-coverage":
                buttons.add(Button.primary("gregar_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("gregar_coverage_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("gregar_coverage_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("gregar_coverage_2", Emoji.fromUnicode("▶")));
                chipList = anly.getGregarCoverageList();
                maxPage = chipList.size()/10;

                builder.setTitle("Top Gregar Chips sorted by coverage");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);


                builder.addField("Coverage and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "gregar-by-count":
                buttons.add(Button.primary("gregar_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("gregar_count_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("gregar_count_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("gregar_count_2", Emoji.fromUnicode("▶")));
                chipList = anly.getGregarCountList();
                maxPage = chipList.size()/10;

                builder.setTitle("Top Falzar Chips sorted by Usage count");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Usage Count and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "falzar-by-coverage":
                buttons.add(Button.primary("falzar_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("falzar_coverage_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("falzar_coverage_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("falzar_coverage_2", Emoji.fromUnicode("▶")));

                chipList = anly.getFalzarCoverageList();
                maxPage = chipList.size() / 10;

                builder.setTitle("Top Falzar Chips sorted by Coverage");
                builder.setFooter("Page: 1/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Chip Name and Coverage", chipNames, true);

                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();

                break;
            case "falzar-by-count":
                buttons.add(Button.primary("falzar_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("falzar_count_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("falzar_count_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("falzar_count_2", Emoji.fromUnicode("▶")));
                chipList = anly.getFalzarCountList();
                maxPage = chipList.size()/10;

                builder.setTitle("Top Falzar Chips sorted by Usage count");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Usage Count and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "combos":
                OptionMapping messageOption1 = event.getOption("chip");

                buttons.add(Button.primary("combo_" + messageOption1.getAsString() + "_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("combo_" + messageOption1.getAsString() + "_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("combo_" + messageOption1.getAsString() + "_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("combo_" + messageOption1.getAsString() + "_2", Emoji.fromUnicode("▶")));
                chipList = anly.combos(messageOption1.getAsString());
                maxPage = chipList.size()/10;

                builder.setTitle("Top Chips used with " + messageOption1.getAsString() + " in Folders");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Coverage and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "top-coverage":
                buttons.add(Button.primary("top_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("top_coverage_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("top_coverage_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("top_coverage_2", Emoji.fromUnicode("▶")));
                chipList = anly.getTotalCoverageList();
                maxPage = chipList.size()/10;

                builder.setTitle("Top Chips sorted by Coverage");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Coverage and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "top-count":
                buttons.add(Button.primary("top_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("top_count_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("top_count_1", Emoji.fromUnicode("◀")).asDisabled());
                buttons.add(Button.primary("top_count_2", Emoji.fromUnicode("▶")));
                chipList = anly.getTotalCountList();
                maxPage = chipList.size()/10;

                builder.setTitle("Top Chips sorted by Usage count");
                builder.setFooter("Page: " + "1" + "/" + maxPage);

                chipNames = getChipListSection(chipList, 1);

                builder.addField("Usage Count and Chip Name", chipNames, true);
                event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                break;
            case "chip-info":
                OptionMapping searchChip = event.getOption("chip");
                builder.setTitle("Chipinfo");
                builder.addField(searchChip.getAsString(), chipFi.findChipData(searchChip.getAsString()), true);
                event.replyEmbeds(builder.build()).queue();
                break;
            case "search-for-folders":
                OptionMapping chip1 = event.getOption("chip1");
                OptionMapping chip2 = event.getOption("chip2");
                OptionMapping chip3 = event.getOption("chip3");
                OptionMapping chip4 = event.getOption("chip4");
                OptionMapping chip5 = event.getOption("chip5");

                String chipName2 = null;
                String chipName3 = null;
                String chipName4 = null;
                String chipName5 = null;

                if (chip2 != null) {
                    chipName2 = chip2.getAsString();
                }

                if (chip3 != null) {
                    chipName3 = chip3.getAsString();
                }

                if (chip4 != null) {
                    chipName4 = chip4.getAsString();
                }

                if (chip5 != null) {
                    chipName5 = chip5.getAsString();
                }
                String[] chipData = {chip1.getAsString(), chipName2, chipName3, chipName4, chipName5};


                String id = String.join("_", Arrays.stream(chipData)
                        .filter(Objects::nonNull)
                        .toArray(String[]::new));


                List<String[]> formattedFolders = folFi.formatAllFilesWithChips(chipData);
                maxPage = formattedFolders.size();


                buttons.add(Button.primary("folder_" + id +"_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("folder_" + id +"_last", Emoji.fromUnicode("⏩")));
                buttons.add(Button.primary("folder_" + id +"_1", Emoji.fromUnicode("◀")).asDisabled());
                if (1 < maxPage) {
                    buttons.add(Button.primary("folder_" + id +"_" + (2), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("folder_" + id +"_" + (2), Emoji.fromUnicode("▶")).asDisabled());
                }
                String[] folder = getStringArrayAtIndex(formattedFolders, 1);

                builder.setFooter("Page: " + "1" + "/" + maxPage);
                if(folder == null){
                    event.reply("Something went wrong, please try again!").queue();
                }
                else {

                    builder.setTitle("Foldersearch");
                    builder.addField(folder[0], "", true);
                    builder.addField(folder[1], "", true);
                    builder.addField("Chips", folder[2], false);
                    builder.addField("Programs", folder[3], true);
                    event.replyEmbeds(builder.build()).addActionRow(buttons).queue();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + event.getName());
        }
    }

    private String[] getStringArrayAtIndex(List<String[]> list, int index) {
        if (index > 0 && index <= list.size()) {
            return list.get(index-1);
        } else {
            return null;
        }
    }


    private String getChipListSection(List<String> chipList, int page) {
        int startIndex = (page-1) * 10;
        int endIndex = Math.min(startIndex + 10, chipList.size());

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            String entry = chipList.get(i);
            stringBuilder.append(entry).append("\n");
        }

        return stringBuilder.toString().trim();
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        int pageNum;
        int maxPage;
        String[] args = event.getButton().getId().split("_");

        if (args[0].equalsIgnoreCase("gregar")) {
            if(args[1].equalsIgnoreCase("coverage")){

                List<String> gregarCoverageList = anly.getGregarCoverageList();
                maxPage = gregarCoverageList.size()/10;
                if(args[2].equalsIgnoreCase("first")){
                    pageNum = 1;
                }
                else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                }
                else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(gregarCoverageList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Gregar Chips sorted by coverage");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("gregar_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("gregar_coverage_last", Emoji.fromUnicode("⏩")));

                msg.addField("Coverage and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("gregar_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("gregar_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("gregar_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("gregar_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();

            }
            if(args[1].equalsIgnoreCase("count")){
                List<String> gregarCountList = anly.getGregarCountList();
                maxPage = gregarCountList.size()/10;
                if(args[2].equalsIgnoreCase("first")){
                    pageNum = 1;
                }
                else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                }
                else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(gregarCountList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Gregar Chips sorted by Usage count");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("gregar_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("gregar_count_last", Emoji.fromUnicode("⏩")));

                msg.addField("Usage count and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("gregar_count_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("gregar_count_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("gregar_count_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("gregar_count_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();
            }
        }
        if (args[0].equalsIgnoreCase("falzar")) {
            if(args[1].equalsIgnoreCase("coverage")){

                List<String> falzarCoverageList = anly.getFalzarCoverageList();
                maxPage = falzarCoverageList.size()/10;
                if(args[2].equalsIgnoreCase("first")){
                    pageNum = 1;
                }
                else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                }
                else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(falzarCoverageList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Falzar Chips sorted by coverage");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("falzar_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("falzar_coverage_last", Emoji.fromUnicode("⏩")));

                msg.addField("Coverage and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("falzar_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("falzar_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("falzar_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("falzar_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();

            }
            if(args[1].equalsIgnoreCase("count")){
                List<String> falzarCountList = anly.getFalzarCountList();
                maxPage = falzarCountList.size()/10;
                if(args[2].equalsIgnoreCase("first")){
                    pageNum = 1;
                }
                else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                }
                else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(falzarCountList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Falzar Chips sorted by Usage count");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("falzar_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("falzar_count_last", Emoji.fromUnicode("⏩")));

                msg.addField("Usage count and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("falzar_count_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("falzar_count_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("falzar_count_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("falzar_count_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();
            }

        }
        if (args[0].equalsIgnoreCase("top")) {
            if (args[1].equalsIgnoreCase("coverage")) {

                List<String> totalCoverageList = anly.getTotalCoverageList();
                maxPage = totalCoverageList.size() / 10;
                if (args[2].equalsIgnoreCase("first")) {
                    pageNum = 1;
                } else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                } else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(totalCoverageList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Chips sorted by coverage");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("top_coverage_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("top_coverage_last", Emoji.fromUnicode("⏩")));

                msg.addField("Coverage and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("top_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("top_coverage_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("top_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("top_coverage_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();

            }
            if (args[1].equalsIgnoreCase("count")){
                List<String> totalCountList = anly.getTotalCountList();
                maxPage = totalCountList.size()/10;
                if(args[2].equalsIgnoreCase("first")){
                    pageNum = 1;
                }
                else if (args[2].equalsIgnoreCase("last")) {
                    pageNum = maxPage;
                }
                else {
                    pageNum = Integer.valueOf(args[2]);
                }

                String chipSection = getChipListSection(totalCountList, pageNum);

                //Build new Embed for the Edit
                EmbedBuilder msg = new EmbedBuilder();
                msg.setTitle("Top Chips sorted by Usage count");
                msg.setColor(Color.decode("#AAFF00"));
                List<Button> buttons = new ArrayList<Button>();
                buttons.add(Button.primary("top_count_first", Emoji.fromUnicode("⏪")));
                buttons.add(Button.primary("top_count_last", Emoji.fromUnicode("⏩")));

                msg.addField("Usage count and Chip Name", chipSection, true);
                msg.setFooter("Page: " + pageNum + "/" + (maxPage));

                //Check for the page number
                if (pageNum > 1) {
                    buttons.add(Button.primary("top_count_" + (pageNum - 1), Emoji.fromUnicode("◀")));
                } else {
                    buttons.add(Button.primary("top_count_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
                }

                if (pageNum < maxPage) {
                    buttons.add(Button.primary("top_count_" + (pageNum + 1), Emoji.fromUnicode("▶")));
                } else {
                    buttons.add(Button.primary("top_count_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
                }
                event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();
            }
        }
        if (args[0].equalsIgnoreCase("combo")){

            List<String> combos = anly.combos(args[1]);
            maxPage = combos.size() / 10;
            if (args[2].equalsIgnoreCase("first")) {
                pageNum = 1;
            } else if (args[2].equalsIgnoreCase("last")) {
                pageNum = maxPage;
            } else {
                pageNum = Integer.valueOf(args[2]);
            }

            String chipSection = getChipListSection(combos, pageNum);

            //Build new Embed for the Edit
            EmbedBuilder msg = new EmbedBuilder();
            msg.setTitle("Top Chips used with " + args[1] + " in Folders");
            msg.setColor(Color.decode("#AAFF00"));
            List<Button> buttons = new ArrayList<Button>();
            buttons.add(Button.primary("combo_" + args[1] + "_first", Emoji.fromUnicode("⏪")));
            buttons.add(Button.primary("combo_" + args[1] + "_last", Emoji.fromUnicode("⏩")));

            msg.addField("Coverage and Chip Name", chipSection, true);
            msg.setFooter("Page: " + pageNum + "/" + (maxPage));

            //Check for the page number
            if (pageNum > 1) {
                buttons.add(Button.primary("combo_" + args[1] + "_" + (pageNum - 1), Emoji.fromUnicode("◀")));
            } else {
                buttons.add(Button.primary("combo_" + args[1] + "_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
            }

            if (pageNum < maxPage) {
                buttons.add(Button.primary("combo_" + args[1] + "_" + (pageNum + 1), Emoji.fromUnicode("▶")));
            } else {
                buttons.add(Button.primary("combo_" + args[1] + "_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
            }
            event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();
        }
        if (args[0].equalsIgnoreCase("folder")){


            String[] chipList = new String[args.length - 2];
            System.arraycopy(args, 1, chipList, 0, args.length - 2);

            List<String[]> folders = folFi.formatAllFilesWithChips(chipList);
            maxPage = folders.size();
            if(args[args.length-1].equalsIgnoreCase("first")){
                pageNum = 1;
            }
            else if (args[args.length-1].equalsIgnoreCase("last")) {
                pageNum = maxPage;
            }
            else {
                pageNum = Integer.valueOf(args[args.length-1]);
            }
            String[] folderList = getStringArrayAtIndex(folders, pageNum);
            String id = String.join("_", Arrays.stream(chipList)
                    .filter(Objects::nonNull)
                    .toArray(String[]::new));
            EmbedBuilder msg = new EmbedBuilder();
            msg.setTitle("Foldersearch");
            msg.setColor(Color.decode("#AAFF00"));
            List<Button> buttons = new ArrayList<Button>();
            buttons.add(Button.primary("folder_" + id +"_first", Emoji.fromUnicode("⏪")));
            buttons.add(Button.primary("folder_" + id +"_last", Emoji.fromUnicode("⏩")));

            msg.addField(folderList[0], "", true);
            msg.addField(folderList[1], "", true);
            msg.addField("Chips", folderList[2], false);
            msg.addField("Programs", folderList[3], true);
            msg.setFooter("Page: " + pageNum + "/" + (maxPage));

            //Check for the page number
            if (pageNum > 1) {
                buttons.add(Button.primary("folder_" + id +"_" + (pageNum - 1), Emoji.fromUnicode("◀")));
            } else {
                buttons.add(Button.primary("folder_" + id +"_" + (pageNum - 1), Emoji.fromUnicode("◀")).asDisabled());
            }

            if (pageNum < maxPage) {
                buttons.add(Button.primary("folder_" + id +"_" + (pageNum + 1), Emoji.fromUnicode("▶")));
            } else {
                buttons.add(Button.primary("folder_" + id +"_" + (pageNum + 1), Emoji.fromUnicode("▶")).asDisabled());
            }
            event.editMessageEmbeds(msg.build()).setActionRow(buttons).queue();
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        super.onGuildReady(event);
        List<CommandData> commandData = new ArrayList<>();
        OptionData option1 = new OptionData(OptionType.STRING, "chip", "Specify a Chip that you want to see combos with.", true, true);

        OptionData chip1 = new OptionData(OptionType.STRING, "chip1", "Choose a Chip.", true, true);
        OptionData chip2 = new OptionData(OptionType.STRING, "chip2", "Choose a Chip.", false, true);
        OptionData chip3 = new OptionData(OptionType.STRING, "chip3", "Choose a Chip.", false, true);
        OptionData chip4 = new OptionData(OptionType.STRING, "chip4", "Choose a Chip.", false, true);
        OptionData chip5 = new OptionData(OptionType.STRING, "chip5", "Choose a Chip.", false, true);


        commandData.add(Commands.slash("combos", "Top Chips used with specified Chip.").addOptions(option1));
        commandData.add(Commands.slash("search-for-folders", "Search for N1GP Folders that include the specified Chips.").addOptions(chip1, chip2, chip3, chip4, chip5));


        OptionData searchChip = new OptionData(OptionType.STRING, "chip", "Specify the Name of the Chip.", true, true);
        commandData.add(Commands.slash("chip-info", "Search for a Chip, their Codes and how to get them.").addOptions(searchChip));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        super.onCommandAutoCompleteInteraction(event);
        if (event.getName().equals("combos") && event.getFocusedOption().getName().equals("chip")) {
            List<Command.Choice> options = Stream.of(anly.getUsedChips())
                    .filter(word -> word.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());

            if(options.size() > 25){
                List<Command.Choice> displayedOptions = options.subList(0, 24);
                event.replyChoices(displayedOptions).queue();
            }else{
                event.replyChoices(options).queue();
            }
        }
        else if (event.getName().equals("search-for-folders")) {
            List<Command.Choice> options = Stream.of(anly.getUsedChips())
                    .filter(word -> word.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase())) // only display words that start with the user's current input
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());

            if(options.size() > 25){
                List<Command.Choice> displayedOptions = options.subList(0, 24);
                event.replyChoices(displayedOptions).queue();
            }else{
                event.replyChoices(options).queue();
            }
        }
        else if (event.getName().equals("chip-info")) {
            List<Command.Choice> options = Stream.of(chipFi.getAllChips())
                    .filter(word -> word.toLowerCase().startsWith(event.getFocusedOption().getValue().toLowerCase())) // case-insensitive filter
                    .map(word -> new Command.Choice(word, word)) // map the words to choices
                    .collect(Collectors.toList());

            if(options.size() > 25){
                List<Command.Choice> displayedOptions = options.subList(0, 24);
                event.replyChoices(displayedOptions).queue();
            }else{
                event.replyChoices(options).queue();
            }
        }

    }
}
