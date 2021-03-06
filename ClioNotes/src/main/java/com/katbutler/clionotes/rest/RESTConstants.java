package com.katbutler.clionotes.rest;

/**
 * Constants used for REST
 */
public class RESTConstants {

    /**
     * Constants for passing to intents
     */
    public class IntentExtraKeys {
        public final static String REQUEST_TYPE = "REQUEST_TYPE";
        public final static String URL = "URL";
        public final static String HTTP_METHOD = "METHOD";
        public final static String MATTER_ID = "MATTER_ID";
        public final static String NOTE_ID = "NOTE_ID";
    }

    /**
     * Types of requests to Clio
     */
    public class RequestTypes {
        public final static int GET_ALL_MATTERS = 100;
        public final static int GET_MATTER_WITH_ID = 101;
        public final static int GET_ALL_NOTES_FOR_MATTER = 102;
        public final static int GET_NOTE_WITH_ID = 103;

        public final static int POST_NEW_NOTE = 200;

        public final static int PUT_NOTE = 300;

        public final static int DELETE_NOTE = 400;
    }

    /**
     * HTTP Request method types
     */
    public class RequestMethods {
        public final static String GET = "GET";
        public final static String POST = "POST";
        public final static String PUT = "PUT";
        public final static String DELETE = "DELETE";
    }

    public class RESTStates {
        public final static String NORMAL = "NORMAL";
        public final static String POSTING = "POSTING";
        public final static String PUTING = "PUTING";
        public final static String DELETING = "DELETING";
        public final static String GETTING = "GETTING";
    }

    public class RESTStatusCodes {
        public final static int OK = 200;
        public final static int CREATED = 201;

        public final static int REDIRECT = 303;
        public final static int NOT_MODIFIED = 304;

        public final static int BAD_REQUEST = 400;
        public final static int FORBIDDEN = 403;
        public final static int RECORD_NOT_FOUND = 404;
    }

    /**
     * Clio REST API
     * @see <a href="http://api-docs.goclio.com/v2/" >Clio API docs</a>
     */
    public class ClioAPI {
        public final static String MATTERS_URL = "https://app.goclio.com/api/v2/matters";
        public final static String MATTER_URL = "https://app.goclio.com/api/v2/matters/%s";
        public final static String MATTER_NOTE_URL = "https://app.goclio.com/api/v2/notes?regarding_type=Matter&regarding_id=%d";
        public final static String NEW_NOTE_URL = "https://app.goclio.com/api/v2/notes";
        public final static String UPDATE_NOTE_URL = "https://app.goclio.com/api/v2/notes/%d";
        public final static String DELETE_NOTE_URL = "https://app.goclio.com/api/v2/notes/%d";
    }
}
