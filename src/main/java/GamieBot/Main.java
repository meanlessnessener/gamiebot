package GamieBot;

import GamieBot.infra.telegram.TelegramBot;

import GamieBot.adapter.controller.TelegramBotController;

public class Main {
    public static void main(String[] args) {
        TelegramBotController controller = new TelegramBotController();
        TelegramBot telegramBot = new TelegramBot();

        telegramBot.setController(controller);

        telegramBot.start();
    }
}
