
package com.incuube.agent.googlelib.payments;

/**
 * Enum values for the tokenization type for the payment processing provider.
 * https://developers.google.com/rcs-business-messaging/rbm/rest/v1/phones.agentMessages#tokenizationtype
 */
public enum TokenizationType {
    PAYMENT_GATEWAY,
    NETWORK_TOKEN,
    DIRECT
}
