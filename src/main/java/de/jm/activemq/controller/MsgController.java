package de.jm.activemq.controller;

import de.jm.activemq.service.MsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class MsgController
{
    MsgService msgService;

    public MsgController(MsgService msgService)
    {
        this.msgService = msgService;
    }

    @GetMapping(path = "/msg/{anzahl}")
    public String sendMessages(@PathVariable Integer anzahl)
    {
        var messages = createMessages(anzahl);

        var totalTime =  messages.parallelStream()
            .map(msg -> msgService.sendMsgSsl(msg))
            .peek(time -> log.info("Nachricht gesendet. Dauer: {}ms", time))
            .reduce(0L, Long::sum);

        var averageTime = totalTime / anzahl;
        log.info("Total time: {}ms, Average time: {}ms", totalTime, averageTime);

        return "OK";
    }


    private List<String> createMessages(final Integer anzahl)
    {
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < anzahl; i++)
        {
            messages.add(message(i));
        }

        return messages;
    }

    private String message(Integer id)  {
       return RandomStringUtils.random(1000, true, true) + "_" + id;
    }

}
