package com.feedstandardizer.application.service;

import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.port.in.FeedProcessingUseCase;
import com.feedstandardizer.domain.port.out.MessagePublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Application service that orchestrates feed processing.
 * Implements the inbound port and delegates publishing to the outbound port.
 */
@Service
public class FeedProcessingService implements FeedProcessingUseCase {

    private static final Logger log = LoggerFactory.getLogger(FeedProcessingService.class);

    private final MessagePublisherPort messagePublisher;

    public FeedProcessingService(MessagePublisherPort messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void process(StandardFeedMessage message) {
        log.info("Processing standardized message: type={}, eventId={}",
                message.messageType(), message.eventId());
        messagePublisher.publish(message);
    }
}
