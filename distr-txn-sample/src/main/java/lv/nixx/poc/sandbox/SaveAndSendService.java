package lv.nixx.poc.sandbox;

import jakarta.transaction.Transactional;
import lv.nixx.poc.sandbox.db.AuditEntity;
import lv.nixx.poc.sandbox.db.AuditRepository;
import lv.nixx.poc.sandbox.jms.JmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
public class SaveAndSendService {

    private static final Logger log = LoggerFactory.getLogger(SaveAndSendService.class);

    private final JmsSender jmsSender;
    private final AuditRepository auditRepository;

    public SaveAndSendService(JmsSender jmsSender, AuditRepository auditRepository) {
        this.jmsSender = jmsSender;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void saveDataAndSendMessage(String message) {

        log.info("Try to save & send message [{}]", message);

        auditRepository.save(new AuditEntity()
                .setMessage(message)
                .setLocalDateTime(LocalDateTime.now())
        );

        jmsSender.sendMessageToQueue(message + ":" + System.currentTimeMillis());

        if (message.equalsIgnoreCase("ErrorMessage")) {
            throw new IllegalStateException("Error inside transactional method: saveDataAndSendMessage");
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("Commit success !!!");
            }
        });
    }


}
