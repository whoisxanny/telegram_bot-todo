package pro.sky.telegrambot.entity;




import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id", nullable = false)
    private Long chatId;
    @Column(name = "notification_text", nullable = false)
    private String notificationText;
    @Column(name = "notification_date_time", nullable = false)
    private LocalDateTime notificationDateTime;

    public NotificationTask(Long id, Long chatId, String notificationText, LocalDateTime notificationDateTime) {
        this.id = id;
        this.chatId = chatId;
        this.notificationText = notificationText;
        this.notificationDateTime = notificationDateTime;
    }
    public NotificationTask() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getChatId() {
        return chatId;
    }
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }
    public String getNotificationText() {
        return notificationText;
    }
    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }
    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }


}
