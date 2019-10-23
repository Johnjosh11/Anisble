package productapi;

import org.springframework.util.Base64Utils;

public class RequestAuthorization {

    private static final String USERNAME = "unittestuser";
    private static final String PASSWORD = "test";

    public static String createValidAuthHeader() {
        return createBasicAuthHeader(USERNAME, PASSWORD);
    }

    public static String createBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
    }
}
