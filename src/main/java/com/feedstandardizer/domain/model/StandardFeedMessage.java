package com.feedstandardizer.domain.model;

import java.time.Instant;

/**
 * Sealed interface representing any standardized feed message produced by this service.
 * All provider-specific formats are normalized into one of its permitted subtypes.
 */
public sealed interface StandardFeedMessage
        permits StandardOddsChange, StandardBetSettlement {

    MessageType messageType();

    String eventId();

    Instant timestamp();
}
