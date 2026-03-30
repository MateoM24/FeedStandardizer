package com.feedstandardizer.domain.model;

import java.time.Instant;

/**
 * Standardized representation of a bet settlement (final result) for a 1X2 market.
 *
 * @param eventId   Unique identifier for the sports event.
 * @param timestamp When this message was received and processed.
 * @param outcome   The final result: HOME, DRAW, or AWAY.
 */
public record StandardBetSettlement(
        String eventId,
        Instant timestamp,
        Outcome outcome
) implements StandardFeedMessage {

    @Override
    public MessageType messageType() {
        return MessageType.BET_SETTLEMENT;
    }
}
