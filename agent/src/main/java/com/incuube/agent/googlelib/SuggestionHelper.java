
package com.incuube.agent.googlelib;

// [START of the suggestion help wrapper class]

import com.google.api.services.rcsbusinessmessaging.v1.model.SuggestedReply;
import com.google.api.services.rcsbusinessmessaging.v1.model.Suggestion;

/**
 * Utility class for Suggestion postbackData and buttonText.
 */
public class SuggestionHelper {
    private String text;
    private String postbackData;

    public SuggestionHelper(String text, String postbackData) {
        this.text = text;
        this.postbackData = postbackData;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostbackData() {
        return postbackData;
    }

    public void setPostbackData(String postbackData) {
        this.postbackData = postbackData;
    }

    /**
     * Converts this suggestion helper object into a RBM suggested reply.
     *
     * @return The Suggestion object as a suggested reply.
     */
    public Suggestion getSuggestedReply() {
        SuggestedReply reply = new SuggestedReply();
        reply.setText(this.text);
        reply.setPostbackData(this.postbackData);

        Suggestion suggestion = new Suggestion();
        suggestion.setReply(reply);

        return suggestion;
    }
}
// [END of the suggestion help wrapper class]
