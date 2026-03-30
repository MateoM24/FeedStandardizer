package com.feedstandardizer.adapter.in.web.beta;

import com.feedstandardizer.adapter.in.web.exception.UnknownMessageTypeException;
import com.feedstandardizer.domain.model.Outcome;
import com.feedstandardizer.domain.model.StandardBetSettlement;
import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.model.StandardOddsChange;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetaFeedMapperTest {

    private final BetaFeedMapper mapper = new BetaFeedMapper();

    @Test
    void mapsOddsChangeCorrectly() {
        BetaFeedRequest request = new BetaFeedRequest(
                "ODDS", "ev456",
                new BetaOdds(1.95, 3.2, 4.0),
                null
        );

        StandardFeedMessage result = mapper.map(request);

        assertThat(result).isInstanceOf(StandardOddsChange.class);
        StandardOddsChange oddsChange = (StandardOddsChange) result;
        assertThat(oddsChange.eventId()).isEqualTo("ev456");
        assertThat(oddsChange.homeOdds()).isEqualTo(1.95);
        assertThat(oddsChange.drawOdds()).isEqualTo(3.2);
        assertThat(oddsChange.awayOdds()).isEqualTo(4.0);
        assertThat(oddsChange.timestamp()).isNotNull();
    }

    @Test
    void mapsSettlementHomeOutcomeCorrectly() {
        BetaFeedRequest request = new BetaFeedRequest("SETTLEMENT", "ev456", null, "home");

        StandardFeedMessage result = mapper.map(request);

        assertThat(result).isInstanceOf(StandardBetSettlement.class);
        StandardBetSettlement settlement = (StandardBetSettlement) result;
        assertThat(settlement.eventId()).isEqualTo("ev456");
        assertThat(settlement.outcome()).isEqualTo(Outcome.HOME);
    }

    @Test
    void mapsSettlementDrawOutcomeCorrectly() {
        BetaFeedRequest request = new BetaFeedRequest("SETTLEMENT", "ev456", null, "draw");
        StandardBetSettlement result = (StandardBetSettlement) mapper.map(request);
        assertThat(result.outcome()).isEqualTo(Outcome.DRAW);
    }

    @Test
    void mapsSettlementAwayOutcomeCorrectly() {
        BetaFeedRequest request = new BetaFeedRequest("SETTLEMENT", "ev456", null, "away");
        StandardBetSettlement result = (StandardBetSettlement) mapper.map(request);
        assertThat(result.outcome()).isEqualTo(Outcome.AWAY);
    }

    @Test
    void throwsForUnknownType() {
        BetaFeedRequest request = new BetaFeedRequest("UNKNOWN", "ev456", null, null);
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(UnknownMessageTypeException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    void throwsWhenOddsAreMissing() {
        BetaFeedRequest request = new BetaFeedRequest("ODDS", "ev456", null, null);
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing required field 'odds'");
    }

    @Test
    void throwsWhenResultIsInvalid() {
        BetaFeedRequest request = new BetaFeedRequest("SETTLEMENT", "ev456", null, "winner");
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown ProviderBeta result value");
    }
}
