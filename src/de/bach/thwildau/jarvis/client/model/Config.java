
package de.bach.thwildau.jarvis.client.model;

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
    "encoding",
    "sampleRate",
    "languageCode"
})
public class Config {

    @JsonProperty("encoding")
    private String encoding;
    @JsonProperty("sampleRate")
    private String sampleRate;
    @JsonProperty("languageCode")
    private String languageCode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Config(){}
    
    public Config(String encoding, String sampleRate, String languageCode, Map<String, Object> additionalProperties) {
		super();
		this.encoding = encoding;
		this.sampleRate = sampleRate;
		this.languageCode = languageCode;
		this.additionalProperties = additionalProperties;
	}

	/**
     * 
     * @return
     *     The encoding
     */
    @JsonProperty("encoding")
    public String getEncoding() {
        return encoding;
    }

    /**
     * 
     * @param encoding
     *     The encoding
     */
    @JsonProperty("encoding")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * 
     * @return
     *     The sampleRate
     */
    @JsonProperty("sampleRate")
    public String getSampleRate() {
        return sampleRate;
    }

    /**
     * 
     * @param sampleRate
     *     The sampleRate
     */
    @JsonProperty("sampleRate")
    public void setSampleRate(String sampleRate) {
        this.sampleRate = sampleRate;
    }

    /**
     * 
     * @return
     *     The languageCode
     */
    @JsonProperty("languageCode")
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * 
     * @param languageCode
     *     The languageCode
     */
    @JsonProperty("languageCode")
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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
