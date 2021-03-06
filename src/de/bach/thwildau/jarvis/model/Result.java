package de.bach.thwildau.jarvis.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    "alternatives"
})
public class Result {

    @JsonProperty("alternatives")
    private List<Alternative> alternatives = new ArrayList<Alternative>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The alternatives
     */
    @JsonProperty("alternatives")
    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    /**
     * 
     * @param alternatives
     *     The alternatives
     */
    @JsonProperty("alternatives")
    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
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
