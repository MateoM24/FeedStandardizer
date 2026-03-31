package com.feedstandardizer.adapter.in.web.alpha;

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

@WebMvcTest(ProviderAlphaController.class)
@Import(AlphaFeedMapper.class)
class ProviderAlphaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedProcessingUseCase feedProcessingUseCase;

    @Test
    void acceptsOddsChangeMessage() throws Exception {
        String body = """
                {
                  "msg_type": "odds_update",
                  "event_id": "ev123",
                  "values": { "1": 2.0, "X": 3.1, "2": 3.8 }
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        ArgumentCaptor<StandardFeedMessage> captor = ArgumentCaptor.forClass(StandardFeedMessage.class);
        verify(feedProcessingUseCase).process(captor.capture());
        assertThat(captor.getValue().eventId()).isEqualTo("ev123");
    }

    @Test
    void acceptsSettlementMessage() throws Exception {
        String body = """
                {
                  "msg_type": "settlement",
                  "event_id": "ev123",
                  "outcome": "1"
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void returns422ForUnknownMsgType() throws Exception {
        String body = """
                {
                  "msg_type": "unknown",
                  "event_id": "ev123"
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void returns400WhenMsgTypeIsMissing() throws Exception {
        String body = """
                {
                  "event_id": "ev123"
                }
                """;

        mockMvc.perform(post("/provider-alpha/feed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
