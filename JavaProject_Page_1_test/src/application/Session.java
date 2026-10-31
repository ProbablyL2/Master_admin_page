package application;

public class Session {
    private static String username;
    private static String email;

    public static void setAccountData(String user, String mail) {
        username = user;
        email = mail;
    }

    public static String getUsername() {
        return username;
    }

    public static String getEmail() {
        return email;
    }

    public static void clear() {
        username = null;
        email = null;
    }
}