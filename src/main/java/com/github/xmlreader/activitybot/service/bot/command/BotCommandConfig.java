package com.github.xmlreader.activitybot.service.bot.command;

import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class BotCommandConfig {
    
    private final Map<String, BotCommand> commands;

    public BotCommandConfig(List<BotCommand> commandList) {
        this.commands = commandList.stream()
                .collect(Collectors.toMap(BotCommand::getCommand, cmd -> cmd));
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }
}
