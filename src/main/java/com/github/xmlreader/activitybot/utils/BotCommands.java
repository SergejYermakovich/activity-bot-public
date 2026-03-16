package com.github.xmlreader.activitybot.utils;

public class BotCommands {
    public static final String START = "/start";
    public static final String SEARCH = "/search";
    public static final String CATEGORIES = "/categories";
    public static final String UPCOMING = "/upcoming";
    public static final String HELP = "/help";
    
    public static boolean isCommand(String text) {
        return text != null && text.startsWith("/");
    }
}
