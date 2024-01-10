package com.company.aggregator.rabbitmq.services.scheduled;

import com.company.aggregator.rabbitmq.properties.RabbitMqProperties;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MessagesCounterService {

    private static final long FIXED_DELAY = 1000;

    private final Channel channel;

    private final RabbitMqProperties properties;

    //@Scheduled(fixedDelay = FIXED_DELAY)
    public void extractMessageCount() throws IOException {
        System.out.println(channel.messageCount(properties.getQueueToSend()));
    }

}
