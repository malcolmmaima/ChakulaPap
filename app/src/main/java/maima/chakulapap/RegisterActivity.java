package maima.chakulapap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etName, etEmail, etPhone, etPassword, rentPassword;
    Button bRegister;
    RadioButton customerRd, FoodRd;
    RadioGroup accountType;
    CheckBox agreeTerms;

    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Our EditText Boxes
        etName = (EditText) findViewById(R.id.etName);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        rentPassword = (EditText) findViewById(R.id.rentPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);

        //Our RadioButtons
        customerRd = (RadioButton) findViewById(R.id.customerRd);
        FoodRd = (RadioButton) findViewById(R.id.FoodRd);

        accountType = (RadioGroup) findViewById(R.id.accType);

        //Our Checkbox
        agreeTerms = (CheckBox) findViewById(R.id.agreeTerms);

        //Our Register Button
        bRegister = (Button) findViewById(R.id.bRegister);
        bRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bRegister:

                if(CheckFieldValidation()) {
                    String name = etName.getText().toString();
                    int phone = Integer.parseInt(etPhone.getText().toString());
                    String email = etEmail.getText().toString();

                    int accType = accountType.getCheckedRadioButtonId();
                    String password = etPassword.getText().toString();

                    // find the radio button by returned id
                    RadioButton radioButton = (RadioButton) findViewById(accType);

                    //Toast.makeText(RegisterActivity.this, radioButton.getText(), Toast.LENGTH_SHORT).show();

                    account = radioButton.getText().toString();


                    User user = new User(name, phone, email, account,password);
                    registerUser(user);
                    break;
                }


        }

    }

        //checking if field are empty
        private boolean CheckFieldValidation(){
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            boolean valid=true;
            if(etName.getText().toString().equals("")){
                etName.setError("Can't be Empty");
                valid=false;
            }
            else if(etPhone.getText().toString().equals("")){
                etPhone.setError("Can't be Empty");
                valid=false;
            }
            else if(etEmail.getText().toString().equals("")){
                etEmail.setError("Can't be Empty");
                valid=false;
            }
            else if(etEmail.equals(emailPattern)){
                etEmail.setError("Incorrect email pattern");
                valid=false;
            }
            else if(etPassword.getText().toString().equals("")){
                etPassword.setError("Can't be Empty");
                valid=false;
            }
            else if(rentPassword.getText().toString().equals("")){
                rentPassword.setError("Can't be Empty");
                valid=false;
            }
            else if(etPassword.getText().toString().equals(rentPassword.getText().toString()) == false){
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                valid=false;
            }
            else if(agreeTerms.isChecked() == false){


                Toast.makeText(RegisterActivity.this, "You must agree to Terms and conditions!", Toast.LENGTH_SHORT).show();
                valid=false;
            }

            else if (accountType.getCheckedRadioButtonId() == -1){

                Toast.makeText(RegisterActivity.this, "You must select account type", Toast.LENGTH_SHORT).show();
                valid=false;
            }


            return valid;

        }

    private void registerUser(User user) {
        ServerRequests serverRequest = new ServerRequests(RegisterActivity.this);

        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {

            @Override
            public void done(User returnedUser) {

                    Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(loginIntent);

            }
        });
    }
}
