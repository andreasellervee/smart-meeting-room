package hackaton.intuit.uk.smart_rooms_estimote.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by andulrv on 24/02/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Room {

    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
