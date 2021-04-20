package com.atlas.android.sample.retrofit.data;

import com.atlas.android.sample.retrofit.data.model.LoggedInUser;
import com.atlas.android.sample.retrofit.http.HttpManager;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {
        try {
            boolean ret = HttpManager.getInstance().doLogin(username, password);
            if (ret) {
                // TODO: handle loggedInUser authentication
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                "Jane Doe");
                return new Result.Success<>(fakeUser);
            }

            return new Result.Error(new Exception("Failed to login"));
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}