package com.feedstandardizer.domain.port.out;

import com.feedstandardizer.domain.model.StandardFeedMessage;

/**
 * Outbound port: abstracts the message queue.
 * The application core depends on this interface; the actual implementation
 * (mock, Kafka, RabbitMQ, etc.) lives in the infrastructure adapter layer.
 */
public interface MessagePublisherPort {

    /**
     * Publishes a standardized feed message to the queue.
     *
     * @param message The normalized message to publish.
     */
    void publish(StandardFeedMessage message);
}
