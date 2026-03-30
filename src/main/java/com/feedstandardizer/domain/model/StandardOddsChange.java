package com.feedstandardizer.domain.model;

import java.time.Instant;

/**
 * Standardized representation of an odds update for a 1X2 market.
 *
 * @param eventId   Unique identifier for the sports event.
 * @param timestamp When this message was received and processed.
 * @param homeOdds  Decimal odds for the home team win (outcome "1").
 * @param drawOdds  Decimal odds for a draw (outcome "X").
 * @param awayOdds  Decimal odds for the away team win (outcome "2").
 */
public record StandardOddsChange(
        String eventId,
        Instant timestamp,
        double homeOdds,
        double drawOdds,
        double awayOdds
) implements StandardFeedMessage {

    @Override
    public MessageType messageType() {
        return MessageType.ODDS_CHANGE;
    }
}
