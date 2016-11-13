
package de.bach.thwildau.jarvis.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "transcript",
    "confidence"
})
public class Alternative {

    @JsonProperty("transcript")
    private String transcript;
    @JsonProperty("confidence")
    private String confidence;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The transcript
     */
    @JsonProperty("transcript")
    public String getTranscript() {
        return transcript;
    }

    /**
     * 
     * @param transcript
     *     The transcript
     */
    @JsonProperty("transcript")
    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    /**
     * 
     * @return
     *     The confidence
     */
    @JsonProperty("confidence")
    public String getConfidence() {
        return confidence;
    }

    /**
     * 
     * @param confidence
     *     The confidence
     */
    @JsonProperty("confidence")
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
