package com.polezhaiev.carsharingapp.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class CarRentalBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    @Value("${chat.id}")
    private long chatId;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                startCommandReceived(
                        update.getMessage().getChat().getFirstName()
                );
            } else {
                sendMessage("Command is not recognized");
            }
        }
    }

    private void startCommandReceived(String name) {
        String answer = "Hi, " + name + "!"
                + "\nI am car-sharing bot."
                + "\nI will send you information about:"
                + "\n- new rentals created;"
                + "\n- overdue rentals;"
                + "\n- successful payments;";
        sendMessage(answer);
    }

    public void sendMessage(String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message: " + message + ". Because " + e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
