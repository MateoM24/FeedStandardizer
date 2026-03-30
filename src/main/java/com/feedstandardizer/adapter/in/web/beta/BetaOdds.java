package com.feedstandardizer.adapter.in.web.beta;

/**
 * Nested odds object in a ProviderBeta ODDS_CHANGE message.
 */
public record BetaOdds(double home, double draw, double away) {
}
