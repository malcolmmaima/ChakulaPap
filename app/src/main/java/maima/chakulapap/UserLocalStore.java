package maima.chakulapap;

import android.content.SharedPreferences;
import android.content.Context;

/**
 * Created by malcolm on 3/10/2017.
 */

public class UserLocalStore {
    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("name", user.name);
        userLocalDatabaseEditor.putInt("phone", user.phone);
        userLocalDatabaseEditor.putString("email", user.email);
        userLocalDatabaseEditor.putString("account", user.account);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        String name = userLocalDatabase.getString("name", "");
        int phone = userLocalDatabase.getInt("phone", -1);
        String email = userLocalDatabase.getString("email", "");
        String account = userLocalDatabase.getString("account", "");
        String password = userLocalDatabase.getString("password", "");

        User user = new User(name, phone, email, account, password);
        return user;
    }
}
