package in.co.fastride;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;

public class RegistrationActivity extends AppCompatActivity {
    EditText mName,mMobileNo,mPassword,mEmail;
    TextView lnkLogin;
    Spinner userType;
    Button signUpButton;
    boolean mIsDriver,mIsPassenger;
    JsonObjectHandler handler;
    private SharedPreferences sharedPreferencesOtpMobile;
    LinearLayout linlaHeaderProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        getSupportActionBar().hide();

        mName=findViewById(R.id.name_et);
        mMobileNo=findViewById(R.id.mobile_reg_et);
        mPassword=findViewById(R.id.password_et);
        mEmail=findViewById(R.id.email_et);
        signUpButton=findViewById(R.id.btn_signup);
        linlaHeaderProgress =  findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);

        lnkLogin=findViewById(R.id.lnk_login);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Name = mName.getText().toString();
                String Email = mEmail.getText().toString();
                String MobileNo = mMobileNo.getText().toString();
                String Password = mPassword.getText().toString();


                if( mMobileNo.getText().toString().length() == 0 ) {
                    mMobileNo.setError("Enter mobile number!");
                    return;
                }
                if( mPassword.getText().toString().length() == 0 ) {
                    mPassword.setError("Enter password!");
                    return;
                }
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                closeKeyboard();
                new UserRegisterPost(Name,Email,MobileNo,Password).execute();

            }

        });


        lnkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });


    }


    public class UserRegisterPost extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String Name;
        String email;
        String mobileNo;
        String password;
        String isDriver;
        String isPassenger;

        UserRegisterPost(String Name,String Email,String MobileNo,String Password){
            this.Name=Name;
            this.email=Email;
            this.mobileNo=MobileNo;
            this.password=Password;
            this.isDriver="false";
            this.isPassenger="true";

        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("FullName",""+Name);
                data.put("MobileNo",""+mobileNo);
                data.put("Password",""+password);
                data.put("Email",""+email);
                data.put("IsDriver",""+isDriver);
                data.put("IsPassenger",""+isPassenger);
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(AppConstant.userRegistrationURL, "POST", data,null);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected  void onPostExecute(final JSONObject jsonObject){
            linlaHeaderProgress.setVisibility(View.GONE);
            try {
                if (jsonObject!=null){
                    JSONObject statusObject=jsonObject.optJSONObject("ReqStatus");
                    String status=statusObject.getString("Status");
                    String message=statusObject.getString("Message");
                    if (statusObject.getBoolean("Status")){
                        JSONObject resultObject=jsonObject.optJSONObject("Result");

                        sharedPreferencesOtpMobile=getSharedPreferences("otpMobile",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferencesOtpMobile.edit();
                        editor.putString("mobile", ""+mobileNo);
                        editor.putString("RequestFor", "Register");
                        editor.commit();

                        //getSupportFragmentManager().beginTransaction().add(R.id.frame_container,new OtpFragment()).commit();

                        Intent intent = new Intent(getApplicationContext(),OTPActivity.class);
                        startActivity(intent);
//                        FragmentManager fragmentManager = getSupportFragmentManager();
//                        OtpFragment fragment = new OtpFragment();
//                        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setMessage(message)
                                .setTitle("Warning")
                                .setIcon(R.drawable.ic_warning)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert =builder.create();
                        alert.show();
                    }
                }

            }catch (Exception e){

            }
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}