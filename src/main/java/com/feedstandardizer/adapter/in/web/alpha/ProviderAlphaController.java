package com.feedstandardizer.adapter.in.web.alpha;

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
 * Inbound adapter for ProviderAlpha.
 * Accepts raw provider messages, maps them to the standard domain model,
 * and delegates processing to the application use case.
 */
@RestController
@RequestMapping("/provider-alpha")
public class ProviderAlphaController {

    private static final Logger log = LoggerFactory.getLogger(ProviderAlphaController.class);

    private final AlphaFeedMapper mapper;
    private final FeedProcessingUseCase feedProcessingUseCase;

    public ProviderAlphaController(AlphaFeedMapper mapper, FeedProcessingUseCase feedProcessingUseCase) {
        this.mapper = mapper;
        this.feedProcessingUseCase = feedProcessingUseCase;
    }

    @PostMapping("/feed")
    public ResponseEntity<Void> receive(@Valid @RequestBody AlphaFeedRequest request) {
        log.debug("Received ProviderAlpha message: msgType={}, eventId={}", request.msgType(), request.eventId());
        feedProcessingUseCase.process(mapper.map(request));
        return ResponseEntity.accepted().build();
    }
}
