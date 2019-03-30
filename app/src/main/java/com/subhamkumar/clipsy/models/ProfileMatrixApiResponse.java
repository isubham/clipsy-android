package com.subhamkumar.clipsy.models;

import java.util.List;

public class ProfileMatrixApiResponse {
    public String message, status;
    public relationShip data;

    public class relationShip {
        public Profile profile;
        public List<relationshipActions> actions;
    }

    private class relationshipActions {
            public String action;
    }
}
