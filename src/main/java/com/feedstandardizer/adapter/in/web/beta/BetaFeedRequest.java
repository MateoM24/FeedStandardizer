package com.feedstandardizer.adapter.in.web.beta;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Raw request DTO for ProviderBeta. Both ODDS_CHANGE and BET_SETTLEMENT messages
 * share this structure; the {@code type} field discriminates between them.
 *
 * <pre>
 * ODDS_CHANGE:    { "type": "ODDS",       "event_id": "ev456",
 *                   "odds": {"home": 1.95, "draw": 3.2, "away": 4.0} }
 * BET_SETTLEMENT: { "type": "SETTLEMENT", "event_id": "ev456",
 *                   "result": "away" }
 * </pre>
 */
public record BetaFeedRequest(

        @JsonProperty("type")
        @NotBlank
        String type,

        @JsonProperty("event_id")
        @NotBlank
        String eventId,

        /* Present only for ODDS_CHANGE. */
        @JsonProperty("odds")
        BetaOdds odds,

        /* Present only for BET_SETTLEMENT. One of "home", "draw", "away". */
        @JsonProperty("result")
        String result
) {
    static final String MSG_TYPE_ODDS   = "ODDS";
    static final String MSG_TYPE_SETTLE = "SETTLEMENT";
}
