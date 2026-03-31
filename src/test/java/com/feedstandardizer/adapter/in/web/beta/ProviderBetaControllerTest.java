package com.feedstandardizer.adapter.in.web.beta;

import com.feedstandardizer.domain.port.in.FeedProcessingUseCase;
import com.feedstandardizer.domain.model.StandardFeedMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProviderBetaController.class)
@Import(BetaFeedMapper.class)
class ProviderBetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedProcessingUseCase feedProcessingUseCase;

    @Test
    void acceptsOddsChangeMessage() throws Exception {
        String body = """
                {
                  "type": "ODDS",
                  "event_id": "ev456",
                  "odds": { "home": 1.95, "draw": 3.2, "away": 4.0 }
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        ArgumentCaptor<StandardFeedMessage> captor = ArgumentCaptor.forClass(StandardFeedMessage.class);
        verify(feedProcessingUseCase).process(captor.capture());
        assertThat(captor.getValue().eventId()).isEqualTo("ev456");
    }

    @Test
    void acceptsSettlementMessage() throws Exception {
        String body = """
                {
                  "type": "SETTLEMENT",
                  "event_id": "ev456",
                  "result": "away"
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void returns422ForUnknownType() throws Exception {
        String body = """
                {
                  "type": "UNKNOWN",
                  "event_id": "ev456"
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void returns400WhenTypeIsMissing() throws Exception {
        String body = """
                {
                  "event_id": "ev456"
                }
                """;

        mockMvc.perform(post("/provider-beta/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
