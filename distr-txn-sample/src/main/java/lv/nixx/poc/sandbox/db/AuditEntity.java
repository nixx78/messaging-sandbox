package lv.nixx.poc.sandbox.db;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Table(name = "MESSAGE_AUDIT")
@Entity
@Data
@Accessors(chain = true)
public class AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "sMessage")
    private String message;

    @Column(name = "dtDatetime")
    private LocalDateTime localDateTime;

}
