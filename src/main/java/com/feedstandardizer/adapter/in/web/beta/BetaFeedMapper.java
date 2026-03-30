package com.feedstandardizer.adapter.in.web.beta;

import com.feedstandardizer.adapter.in.web.exception.UnknownMessageTypeException;
import com.feedstandardizer.domain.model.Outcome;
import com.feedstandardizer.domain.model.StandardBetSettlement;
import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.model.StandardOddsChange;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Maps ProviderBeta raw requests to standardized domain messages.
 */
@Component
public class BetaFeedMapper {

    /**
     * Converts a {@link BetaFeedRequest} into a {@link StandardFeedMessage}.
     *
     * @throws UnknownMessageTypeException if {@code type} is not recognized.
     * @throws IllegalArgumentException    if required fields for the given type are missing.
     */
    public StandardFeedMessage map(BetaFeedRequest request) {
        return switch (request.type()) {
            case BetaFeedRequest.MSG_TYPE_ODDS   -> toOddsChange(request);
            case BetaFeedRequest.MSG_TYPE_SETTLE -> toSettlement(request);
            default -> throw new UnknownMessageTypeException(
                    "ProviderBeta", request.type());
        };
    }

    private StandardOddsChange toOddsChange(BetaFeedRequest request) {
        if (request.odds() == null) {
            throw new IllegalArgumentException(
                    "ProviderBeta ODDS_CHANGE is missing required field 'odds'");
        }
        BetaOdds odds = request.odds();
        return new StandardOddsChange(
                request.eventId(),
                Instant.now(),
                odds.home(),
                odds.draw(),
                odds.away()
        );
    }

    private StandardBetSettlement toSettlement(BetaFeedRequest request) {
        if (request.result() == null || request.result().isBlank()) {
            throw new IllegalArgumentException(
                    "ProviderBeta BET_SETTLEMENT is missing required field 'result'");
        }
        return new StandardBetSettlement(
                request.eventId(),
                Instant.now(),
                mapBetaOutcome(request.result())
        );
    }

    /**
     * ProviderBeta uses "home" / "draw" / "away" notation.
     */
    private Outcome mapBetaOutcome(String raw) {
        return switch (raw.toLowerCase()) {
            case "home" -> Outcome.HOME;
            case "draw" -> Outcome.DRAW;
            case "away" -> Outcome.AWAY;
            default     -> throw new IllegalArgumentException(
                    "Unknown ProviderBeta result value: '" + raw + "'. Expected 'home', 'draw', or 'away'.");
        };
    }
}
