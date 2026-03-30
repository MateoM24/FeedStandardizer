package com.feedstandardizer.domain.port.in;

import com.feedstandardizer.domain.model.StandardFeedMessage;

/**
 * Inbound port: the primary use case exposed to the outside world.
 * Driving adapters (e.g., REST controllers) call this to process a standardized message.
 */
public interface FeedProcessingUseCase {

    /**
     * Processes a standardized feed message by forwarding it to the message queue.
     *
     * @param message A fully normalized {@link StandardFeedMessage}.
     */
    void process(StandardFeedMessage message);
}
