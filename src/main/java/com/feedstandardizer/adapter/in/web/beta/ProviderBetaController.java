package com.feedstandardizer.adapter.in.web.beta;

import com.feedstandardizer.domain.port.in.FeedProcessingUseCase;
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
public class ProviderBetaController {

    private static final Logger log = LoggerFactory.getLogger(ProviderBetaController.class);

    private final BetaFeedMapper mapper;
    private final FeedProcessingUseCase feedProcessingUseCase;

    public ProviderBetaController(BetaFeedMapper mapper, FeedProcessingUseCase feedProcessingUseCase) {
        this.mapper = mapper;
        this.feedProcessingUseCase = feedProcessingUseCase;
    }

    @PostMapping("/feed")
    public ResponseEntity<Void> receive(@Valid @RequestBody BetaFeedRequest request) {
        log.debug("Received ProviderBeta message: type={}, eventId={}", request.type(), request.eventId());
        feedProcessingUseCase.process(mapper.map(request));
        return ResponseEntity.accepted().build();
    }
}
