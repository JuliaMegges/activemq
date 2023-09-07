package de.jm.activemq.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class MsgService
{
    JmsTemplate   jmsTemplateSsl;
    ActiveMQQueue queueSsl;


    public MsgService(
        final JmsTemplate jmsTemplateSsl,
        final ActiveMQQueue queueSsl)
    {
        this.jmsTemplateSsl = jmsTemplateSsl;
        this.queueSsl = queueSsl;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long sendMsgSsl(String message)
    {
        var startTime = System.currentTimeMillis();

        jmsTemplateSsl.convertAndSend(queueSsl, message);

        return System.currentTimeMillis() - startTime;
    }
}
