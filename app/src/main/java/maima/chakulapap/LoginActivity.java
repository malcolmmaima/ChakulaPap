package maima.chakulapap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import maima.chakulapap.ViewPager.CustomerActivity;
import maima.chakulapap.ViewPager.ProviderActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button bLogin;
    Button registerLink;
    TextView ResetLink;
    EditText etEmail, etPassword;


    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bLogin = (Button) findViewById(R.id.bLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerLink = (Button) findViewById(R.id.etRegisterLink);
        ResetLink = (TextView) findViewById(R.id.etResetLink);

        bLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);
        ResetLink.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                if(CheckFieldValidation()) {
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    User user = new User(email, password);

                    authenticate(user);

                }
                break;
            case R.id.etRegisterLink:
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                break;

            case R.id.etResetLink:
                Intent resetIntent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(resetIntent);
                break;
        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Incorrect email or password");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();


    }

    private void logUserIn(User returnedUser) {
        String provider = "provider";
        String admin = "admin";
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        String name = userLocalStore.getLoggedInUser().name.toString();
        String account_type = userLocalStore.getLoggedInUser().account.toString();

        //If account type is of provider then load main activity else load customer activity
        if (account_type.equals(provider)){
            Intent customer_act = new Intent(LoginActivity.this, ProviderActivity.class);
            customer_act.putExtra("Welcome", name);
            startActivity(customer_act);
        }

        else {
            Intent i = new Intent(LoginActivity.this, CustomerActivity.class);
            i.putExtra("Welcome", name);
            startActivity(i);
        }



        //startActivity(new Intent(this, MainActivity.class));
    }

    //checking if field are empty
    private boolean CheckFieldValidation(){

        boolean valid=true;
        if(etEmail.getText().toString().equals("")){
            etEmail.setError("Can't be Empty");
            valid=false;
        }
        else if(etPassword.getText().toString().equals("")){
            etPassword.setError("Can't be Empty");
            valid=false;
        }
        return valid;

    }

}
