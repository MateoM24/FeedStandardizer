package com.feedstandardizer.adapter.in.web.alpha;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/**
 * Raw request DTO for ProviderAlpha. Both ODDS_CHANGE and BET_SETTLEMENT messages
 * share this structure; the {@code msgType} field discriminates between them.
 *
 * <pre>
 * ODDS_CHANGE:    { "msg_type": "odds_update", "event_id": "ev123",
 *                   "values": {"1": 2.0, "X": 3.1, "2": 3.8} }
 * BET_SETTLEMENT: { "msg_type": "settlement",  "event_id": "ev123",
 *                   "outcome": "1" }
 * </pre>
 */
public record AlphaFeedRequest(

        @JsonProperty("msg_type")
        @NotBlank
        String msgType,

        @JsonProperty("event_id")
        @NotBlank
        String eventId,

        /** Present only for ODDS_CHANGE. Keys: "1" (home), "X" (draw), "2" (away). */
        @JsonProperty("values")
        Map<String, Double> values,

        /** Present only for BET_SETTLEMENT. One of "1", "X", "2". */
        @JsonProperty("outcome")
        String outcome
) {
    static final String MSG_TYPE_ODDS   = "odds_update";
    static final String MSG_TYPE_SETTLE = "settlement";
}
