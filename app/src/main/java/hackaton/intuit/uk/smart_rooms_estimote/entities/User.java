package hackaton.intuit.uk.smart_rooms_estimote.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by andulrv on 24/02/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    String id;
    String nickname;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return nickname;
    }
}
