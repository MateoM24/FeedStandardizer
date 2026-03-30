package com.feedstandardizer.adapter.in.web.alpha;

import com.feedstandardizer.adapter.in.web.exception.UnknownMessageTypeException;
import com.feedstandardizer.domain.model.Outcome;
import com.feedstandardizer.domain.model.StandardBetSettlement;
import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.model.StandardOddsChange;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Maps ProviderAlpha raw requests to standardized domain messages.
 */
@Component
public class AlphaFeedMapper {

    /**
     * Converts an {@link AlphaFeedRequest} into a {@link StandardFeedMessage}.
     *
     * @throws UnknownMessageTypeException if {@code msgType} is not recognized.
     * @throws IllegalArgumentException    if required fields for the given type are missing.
     */
    public StandardFeedMessage map(AlphaFeedRequest request) {
        return switch (request.msgType()) {
            case AlphaFeedRequest.MSG_TYPE_ODDS   -> toOddsChange(request);
            case AlphaFeedRequest.MSG_TYPE_SETTLE -> toSettlement(request);
            default -> throw new UnknownMessageTypeException(
                    "ProviderAlpha", request.msgType());
        };
    }

    private StandardOddsChange toOddsChange(AlphaFeedRequest request) {
        Map<String, Double> values = request.values();
        if (values == null || !values.containsKey("1") || !values.containsKey("X") || !values.containsKey("2")) {
            throw new IllegalArgumentException(
                    "ProviderAlpha ODDS_CHANGE is missing required odds values (expected keys: '1', 'X', '2')");
        }
        return new StandardOddsChange(
                request.eventId(),
                Instant.now(),
                values.get("1"),
                values.get("X"),
                values.get("2")
        );
    }

    private StandardBetSettlement toSettlement(AlphaFeedRequest request) {
        if (request.outcome() == null || request.outcome().isBlank()) {
            throw new IllegalArgumentException(
                    "ProviderAlpha BET_SETTLEMENT is missing required field 'outcome'");
        }
        return new StandardBetSettlement(
                request.eventId(),
                Instant.now(),
                mapAlphaOutcome(request.outcome())
        );
    }

    /**
     * ProviderAlpha uses "1" / "X" / "2" notation.
     */
    private Outcome mapAlphaOutcome(String raw) {
        return switch (raw) {
            case "1" -> Outcome.HOME;
            case "X" -> Outcome.DRAW;
            case "2" -> Outcome.AWAY;
            default  -> throw new IllegalArgumentException(
                    "Unknown ProviderAlpha outcome value: '" + raw + "'. Expected '1', 'X', or '2'.");
        };
    }
}
