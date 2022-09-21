package org.Main;

import org.Service.CategoryService;
import org.Service.UserService;
import org.bot.ApplicationBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi=new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new ApplicationBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
//        UserService userService=new UserService();

//        System.out.println(userService.findUser(5094739326L));
    }


}