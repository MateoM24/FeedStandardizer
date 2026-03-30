package com.feedstandardizer.adapter.in.web.alpha;

import com.feedstandardizer.adapter.in.web.exception.UnknownMessageTypeException;
import com.feedstandardizer.domain.model.Outcome;
import com.feedstandardizer.domain.model.StandardBetSettlement;
import com.feedstandardizer.domain.model.StandardFeedMessage;
import com.feedstandardizer.domain.model.StandardOddsChange;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlphaFeedMapperTest {

    private final AlphaFeedMapper mapper = new AlphaFeedMapper();

    @Test
    void mapsOddsChangeCorrectly() {
        AlphaFeedRequest request = new AlphaFeedRequest(
                "odds_update", "ev123",
                Map.of("1", 2.0, "X", 3.1, "2", 3.8),
                null
        );

        StandardFeedMessage result = mapper.map(request);

        assertThat(result).isInstanceOf(StandardOddsChange.class);
        StandardOddsChange oddsChange = (StandardOddsChange) result;
        assertThat(oddsChange.eventId()).isEqualTo("ev123");
        assertThat(oddsChange.homeOdds()).isEqualTo(2.0);
        assertThat(oddsChange.drawOdds()).isEqualTo(3.1);
        assertThat(oddsChange.awayOdds()).isEqualTo(3.8);
        assertThat(oddsChange.timestamp()).isNotNull();
    }

    @Test
    void mapsSettlementHomeOutcomeCorrectly() {
        AlphaFeedRequest request = new AlphaFeedRequest("settlement", "ev123", null, "1");

        StandardFeedMessage result = mapper.map(request);

        assertThat(result).isInstanceOf(StandardBetSettlement.class);
        StandardBetSettlement settlement = (StandardBetSettlement) result;
        assertThat(settlement.eventId()).isEqualTo("ev123");
        assertThat(settlement.outcome()).isEqualTo(Outcome.HOME);
    }

    @Test
    void mapsSettlementDrawOutcomeCorrectly() {
        AlphaFeedRequest request = new AlphaFeedRequest("settlement", "ev123", null, "X");
        StandardBetSettlement result = (StandardBetSettlement) mapper.map(request);
        assertThat(result.outcome()).isEqualTo(Outcome.DRAW);
    }

    @Test
    void mapsSettlementAwayOutcomeCorrectly() {
        AlphaFeedRequest request = new AlphaFeedRequest("settlement", "ev123", null, "2");
        StandardBetSettlement result = (StandardBetSettlement) mapper.map(request);
        assertThat(result.outcome()).isEqualTo(Outcome.AWAY);
    }

    @Test
    void throwsForUnknownMsgType() {
        AlphaFeedRequest request = new AlphaFeedRequest("unknown_type", "ev123", null, null);
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(UnknownMessageTypeException.class)
                .hasMessageContaining("unknown_type");
    }

    @Test
    void throwsWhenOddsValuesAreMissing() {
        AlphaFeedRequest request = new AlphaFeedRequest("odds_update", "ev123", null, null);
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing required odds values");
    }

    @Test
    void throwsWhenOutcomeIsInvalid() {
        AlphaFeedRequest request = new AlphaFeedRequest("settlement", "ev123", null, "INVALID");
        assertThatThrownBy(() -> mapper.map(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown ProviderAlpha outcome value");
    }
}
