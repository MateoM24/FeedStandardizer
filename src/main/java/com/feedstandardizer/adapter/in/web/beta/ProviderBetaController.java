package com.feedstandardizer.adapter.in.web.beta;

import com.feedstandardizer.domain.port.in.FeedProcessingUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Inbound adapter for ProviderBeta.
 * Accepts raw provider messages, maps them to the standard domain model,
 * and delegates processing to the application use case.
 */
@RestController
@RequestMapping("/provider-beta")
@Tag(name = "ProviderBeta", description = "Feed endpoint for ProviderBeta messages")
public class ProviderBetaController {

    private static final Logger log = LoggerFactory.getLogger(ProviderBetaController.class);

    private final BetaFeedMapper mapper;
    private final FeedProcessingUseCase feedProcessingUseCase;

    public ProviderBetaController(BetaFeedMapper mapper, FeedProcessingUseCase feedProcessingUseCase) {
        this.mapper = mapper;
        this.feedProcessingUseCase = feedProcessingUseCase;
    }

    @Operation(
            summary = "Receive ProviderBeta feed message",
            description = "Accepts ODDS_CHANGE (type: ODDS) or BET_SETTLEMENT (type: SETTLEMENT) messages from ProviderBeta.",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Message accepted and published"),
                    @ApiResponse(responseCode = "400", description = "Invalid or missing fields"),
                    @ApiResponse(responseCode = "422", description = "Unknown message type")
            }
    )
    @PostMapping("/feed")
    public ResponseEntity<Void> receive(@Valid @RequestBody BetaFeedRequest request) {
        log.debug("Received ProviderBeta message: type={}, eventId={}", request.type(), request.eventId());
        feedProcessingUseCase.process(mapper.map(request));
        return ResponseEntity.accepted().build();
    }
}
