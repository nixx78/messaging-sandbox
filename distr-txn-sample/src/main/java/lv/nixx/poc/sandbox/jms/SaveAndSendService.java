package lv.nixx.poc.sandbox.jms;

import jakarta.transaction.Transactional;
import lv.nixx.poc.sandbox.db.AuditEntity;
import lv.nixx.poc.sandbox.db.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SaveAndSendService {

    private final JmsSender jmsSender;
    private final AuditRepository auditRepository;

    public SaveAndSendService(JmsSender jmsSender, AuditRepository auditRepository) {
        this.jmsSender = jmsSender;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void saveDataAndSendMessage(String message) {
        auditRepository.save(new AuditEntity()
                .setMessage(message)
                .setLocalDateTime(LocalDateTime.now())
        );
        jmsSender.sendMessageToQueue(System.currentTimeMillis() + ":" + message);

        if (message.equalsIgnoreCase("ErrorMessage")) {
            throw new IllegalStateException("Error inside transactional method");
        }
    }


}
