package maima.chakulapap;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ForgotActivity extends AppCompatActivity implements View.OnClickListener {

    EditText resetemail;
    TextView alert;
    TextView ForgotTextView;
    Button resetpass;

    String email="";
    byte[] data;
    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    InputStream inputStream;
    List<NameValuePair> nameValuePairs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        resetemail = (EditText) findViewById(R.id.resetEmail);
        //alert = (TextView) findViewById(R.id.alert);
        ForgotTextView = (TextView) findViewById(R.id.ForgotTextView);
        resetpass = (Button) findViewById(R.id.bReset);

        resetpass.setOnClickListener(this);
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bReset:

                if(CheckFieldValidation()) {

                    String resEmail = resetemail.getText().toString();

                    //User user = new User(resEmail, "");

                    //authenticate(user);

                    /*
                    Intent registerIntent = new Intent(ForgotActivity.this, LoginActivity.class);
                    startActivity(registerIntent);

                    */

                    try {
                        httpclient = new DefaultHttpClient();
                        httppost = new HttpPost("http://dev.oscorpltd.com/FetchResetData.php");
                        // Add your data
                        nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("email", resEmail.trim()));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        response = httpclient.execute(httppost);
                        inputStream = response.getEntity().getContent();

                        data = new byte[128];

                        buffer = new StringBuffer();
                        int len = 0;
                        while (-1 != (len = inputStream.read(data)) )
                        {
                            buffer.append(new String(data, 0, len));
                        }

                        inputStream.close();
                    }

                    catch (Exception e)
                    {
                        Toast.makeText(ForgotActivity.this, "error"+e.toString(), Toast.LENGTH_LONG).show();
                    }
                    if(buffer.charAt(0)== 'Y')
                    {
                        Toast.makeText(ForgotActivity.this, "Email exists", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(ForgotActivity.this, "Invalid email", Toast.LENGTH_LONG).show();
                    }

                    break;
                }


        }
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedEmail) {
                if (returnedEmail == null) {
                    showErrorMessage();
                } else {
                    //logUserIn(returnedUser);
                    Toast.makeText(ForgotActivity.this, "Email found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ForgotActivity.this);
        dialogBuilder.setMessage("Email does not exist in our system");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    //checking if field are empty
    private boolean CheckFieldValidation(){

        boolean valid=true;
        if(resetemail.getText().toString().equals("")){
            resetemail.setError("Can't be Empty");
            valid=false;
        }

        return valid;
    }


}


