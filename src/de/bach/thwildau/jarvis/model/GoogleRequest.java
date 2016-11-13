
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
    "config",
    "audio",
    "auth"
})
public class GoogleRequest {

    @JsonProperty("config")
    private Config config;
    @JsonProperty("audio")
    private Audio audio;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The config
     */
    @JsonProperty("config")
    public Config getConfig() {
        return config;
    }

    /**
     * 
     * @param config
     *     The config
     */
    @JsonProperty("config")
    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * 
     * @return
     *     The audio
     */
    @JsonProperty("audio")
    public Audio getAudio() {
        return audio;
    }

    /**
     * 
     * @param audio
     *     The audio
     */
    @JsonProperty("audio")
    public void setAudio(Audio audio) {
        this.audio = audio;
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
