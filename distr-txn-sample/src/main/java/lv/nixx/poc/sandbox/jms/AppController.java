package lv.nixx.poc.sandbox.jms;

import lv.nixx.poc.sandbox.db.AuditEntity;
import lv.nixx.poc.sandbox.db.AuditRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class AppController {

    private final SaveAndSendService saveAndSendService;
    private final AuditRepository auditRepository;

    public AppController(SaveAndSendService saveAndSendService, AuditRepository auditRepository) {
        this.saveAndSendService = saveAndSendService;
        this.auditRepository = auditRepository;
    }

    @GetMapping("/saveAndSend")
    public void sendMessage(@RequestParam String message) {
        saveAndSendService.saveDataAndSendMessage(message);
    }

    @GetMapping("/audit")
    public Collection<AuditEntity> getAllAutditMessages() {
        return auditRepository.findAll();
    }


}
