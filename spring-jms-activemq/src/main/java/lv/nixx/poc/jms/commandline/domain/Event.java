package lv.nixx.poc.jms.commandline.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {
    private Long id;
    private String body;
    private EventType eventType;
}
