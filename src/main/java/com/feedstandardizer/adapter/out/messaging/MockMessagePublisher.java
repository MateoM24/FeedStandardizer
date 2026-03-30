package com.feedstandardizer.adapter.out.messaging;

import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.port.out.MessagePublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Mock implementation of {@link MessagePublisherPort}.
 * Simulates publishing by logging the serialized message as JSON.
 * Replace with a real Kafka/RabbitMQ adapter when needed.
 */
@Component
public class MockMessagePublisher implements MessagePublisherPort {

    private static final Logger log = LoggerFactory.getLogger(MockMessagePublisher.class);

    private final ObjectMapper objectMapper;

    public MockMessagePublisher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(StandardFeedMessage message) {
        try {
            String json = objectMapper.writeValueAsString(new MessageEnvelope(message));
            log.info("[MOCK QUEUE] Published message → {}", json);
        } catch (JacksonException e) {
            log.error("[MOCK QUEUE] Failed to serialize message for eventId={}", message.eventId(), e);
        }
    }

    /**
     * Envelope that wraps the message with its type for structured queue output.
     */
    private record MessageEnvelope(String messageType, String eventId, Object payload) {
        MessageEnvelope(StandardFeedMessage message) {
            this(message.messageType().name(), message.eventId(), message);
        }
    }
}
