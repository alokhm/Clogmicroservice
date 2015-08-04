
package com.covisint.platform.clog.model;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "add"
})
public class Action {


    @JsonProperty("add")
    private Add add;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
   
    /**
     * 
     * @return
     *     The add
     */
    @JsonProperty("add")
    public Add getAdd() {
        return add;
    }

    /**
     * 
     * @param add
     *     The add
     */
    @JsonProperty("add")
    public void setAdd(Add add) {
        this.add = add;
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
