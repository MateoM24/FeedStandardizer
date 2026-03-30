package com.feedstandardizer.adapter.in.web.exception;

/**
 * Thrown when an inbound message contains an unrecognized type discriminator.
 */
public class UnknownMessageTypeException extends RuntimeException {

    public UnknownMessageTypeException(String provider, String receivedType) {
        super("Unknown message type '%s' received from %s".formatted(receivedType, provider));
    }
}
