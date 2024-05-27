package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskRepository notificationTaskRepository;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }


    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotifications() {
        LocalDateTime dateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> taskCollection = notificationTaskRepository.findAllByNotificationDateTime(dateTime);
        taskCollection.forEach(notificationTask -> {
            System.out.println("Task: " + notificationTask.getNotificationText());
            SendMessage message = new SendMessage(notificationTask.getChatId(), "Опять работа? ДА!!! " + notificationTask.getNotificationText().toString() + ", так как время уже начинать батрачит!!! " + notificationTask.getNotificationDateTime());
            SendResponse response = telegramBot.execute(message);
        });
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                Long chatId = update.message().chat().id();
                String messageGot = update.message().text();
                if (messageGot.equals("/start")) {
                    SendMessage message = new SendMessage(chatId, "Хала, я бот - напоминалка. Буду надоедать своими напоминаниями, чтобы я начал работу, назначь напоминалку типа: 01.01.2022 20:00 Сделать домашнюю работу");
                    SendResponse response = telegramBot.execute(message);
                }else if (messageGot.matches("^\\d+.*")) {
                    Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})([\\s])(.+)");
                    Matcher matcher = pattern.matcher(update.message().text());
                    if (matcher.find()) {
                        String dateTimeStr = matcher.group(1);
                        String messageGotNotified = matcher.group(3);
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                        NotificationTask task = new NotificationTask(0L, chatId, messageGotNotified, dateTime);
                        String notificationMessage = "Записал, что: " + task.getNotificationDateTime() + " " + task.getNotificationText();
                        SendMessage notificationSendMessage = new SendMessage(chatId, notificationMessage);
                        SendResponse response = telegramBot.execute(notificationSendMessage);
                        NotificationTask save = notificationTaskRepository.save(task);
                    } else {
                        String notificationMessageMiss = "Я же показывал как надо, ты забыл? Ладно, напомню: 01.01.2022 20:00 Сделать домашнюю работу";
                        SendMessage notificationSendMessageMiss = new SendMessage(chatId, notificationMessageMiss);
                        SendResponse response = telegramBot.execute(notificationSendMessageMiss);
                    }
                }

            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;


    }

}