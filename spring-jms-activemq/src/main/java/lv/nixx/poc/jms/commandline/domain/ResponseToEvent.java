package lv.nixx.poc.jms.commandline.domain;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResponseToEvent implements Serializable {
    private Long eventId;
    private String response;
}
