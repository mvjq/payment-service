package org.example.paymentservice.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.infrastructure.config.RABBITMQ_CONSTANTS;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DLQScheduler {

    private final RabbitTemplate rabbitTemplate;

    // 1 hour
    @Scheduled(fixedDelay = 3600000, initialDelay = 60000) // initial delay 1 min
    public void retryDlqMessages() {
        try {
            Long messageCount = rabbitTemplate.execute(channel -> channel.messageCount(RABBITMQ_CONSTANTS.PAYMENT_DLQ));

            if (messageCount == null || messageCount == 0) {
                log.info("No messages in DLQ");
                return;
            }

            log.info("Found messages #{} in DLQ, retrying", messageCount);

            for (int i = 0; i < messageCount && i < 100; i++) { // Max 100 per run
                processOneDlqMessage();
            }

        } catch (Exception e) {
            log.error("Error in DLQ retry scheduler", e);
        }
    }

    private void processOneDlqMessage() {
        try {
            Message message = rabbitTemplate.receive(RABBITMQ_CONSTANTS.PAYMENT_QUEUE, 1000);

            if (message == null) {
                return;
            }

            Integer dlqRetryCount = (Integer) message.getMessageProperties()
                    .getHeader("x-dlq-retry-count");

            if (dlqRetryCount == null) {
                dlqRetryCount = 0;
            }

            if (dlqRetryCount >= RABBITMQ_CONSTANTS.MAX_DLQ_RETRIES) {
                rabbitTemplate.send(RABBITMQ_CONSTANTS.PAYMENT_DLQ, message);
                // FUTURE: implement alarm system
                // send a POST to oncall services (page duty/opsgenie)
                return;
            }

            message.getMessageProperties().setHeader("x-dlq-retry-count", dlqRetryCount++);
            rabbitTemplate.send(RABBITMQ_CONSTANTS.PAYMENT_QUEUE, message);
            log.info("Retried DLQ message, new retry count: {}", dlqRetryCount);
        } catch (Exception e) {
            log.error("Error processing DLQ message", e);
        }
    }
}