package scheduls;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyUpdate {

    @Scheduled(cron = "0 0 4 ? * *")
    public void dailyUpdate(){
        
    }
}
