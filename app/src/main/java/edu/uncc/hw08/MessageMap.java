package edu.uncc.hw08;

import java.io.Serializable;
import java.util.HashMap;

public class MessageMap implements Serializable {
    private HashMap<String, Object> message;

    public MessageMap(){

    }

    public MessageMap(HashMap<String, Object> message) {
        this.message = message;
    }

    public HashMap<String, Object> getMessage() {
        return message;
    }
}
