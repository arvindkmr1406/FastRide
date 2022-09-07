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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;
    TextView mSkip;
    private EditText mMobileNo,mPassword;
    private CheckBox rememberMe;
    private boolean isRmChecked=false;
    private SharedPreferences sharedPreferencesUserDetails;
    JsonObjectHandler handler;
    LinearLayout linlaHeaderProgress;
    TextView reg,forgot,sign_up,forgot_pwd;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);

        mMobileNo = findViewById(R.id.mobile_et);
        mPassword = findViewById(R.id.password_et);
        sign_up = findViewById(R.id.sign_up);
        rememberMe = findViewById(R.id.rememberMe);

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

//        mSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent skipIntent = new Intent(getApplicationContext(), DashboardActivity.class);
//                startActivity(skipIntent);
//            }
//        });
//        if( mMobileNo.getText().toString().length() == 0 ){
//            mMobileNo.setError("Mobile Number is required!");
//        }
        Button mEmailSignInButton = (Button) findViewById(R.id.btn_login);

        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if (mMobileNo.getText().toString().length() == 0) {
                    mMobileNo.setError("Enter mobile number!");
                    return;
                }
                if (mPassword.getText().toString().length() == 0) {
                    mPassword.setError("Enter password!");
                    return;
                }
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                Long mobileNo = Long.parseLong(mMobileNo.getText().toString());
                if (isRmChecked == true) {
                    SharedPreferences.Editor editor = sharedPreferencesUserDetails.edit();
                    editor.putString("pass", "" + mPassword);
                    editor.putString("mobile", "" + mMobileNo);
                    editor.putString("remember", "true");
                }
                new UserLoginTask(mobileNo, mPassword.getText().toString(), isRmChecked).execute();
            }
        });

//        mLoginFormView = findViewById(R.id.login_form);
//        mProgressView = findViewById(R.id.login_progress);
//        rememberMe = findViewById(R.id.remember_me_cb);
//        forgotPassword=findViewById(R.id.forgot_password);


        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRmChecked = isChecked;
            }
        });

        sharedPreferencesUserDetails = getSharedPreferences("myapp", MODE_PRIVATE);


        if (sharedPreferencesUserDetails.getBoolean("remember", true)) {
            rememberMe.setChecked(true);
            mMobileNo.setText(sharedPreferencesUserDetails.getString("mobile", ""));
            mPassword.setText(sharedPreferencesUserDetails.getString("pass", ""));
        } else {
            rememberMe.setChecked(false);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });

        forgot_pwd = (TextView) findViewById(R.id.forgot_pwd);

        forgot_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotActivity.class);
                startActivity(intent);
            }
        });

    }
    protected void onResume(){
        super.onResume();
        closeKeyboard();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public class UserLoginTask extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        Long mMobileView;
        String mPasswordView;
        Boolean rememberme;

        UserLoginTask(Long mMobileNoView,String mPasswordView,Boolean rememberme){
            this.mMobileView=mMobileNoView;
            this.mPasswordView=mPasswordView;
            this.rememberme=rememberme;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("MobileNo",""+mMobileView);
                data.put("Password",""+mPasswordView);
                data.put("RememberMe",""+"true");
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(AppConstant.userLoginURL, "POST", data,null);

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

                        String access_token = resultObject.getString("access_token");
                        String token_type = resultObject.getString("token_type");
                        String expires_in = resultObject.getString("expires_in");
                        String userId = resultObject.getString("UserId");
                        String UserName = resultObject.getString("UserName");
                        String RoleName = resultObject.getString("RoleName");
                        String FullName = resultObject.getString("FullName");
                        String ImgUrl = resultObject.getString("ImgUrl");
                        String Mobile = resultObject.getString("Mobile");
                        String Email = resultObject.getString("Email");
                        String issued = resultObject.getString(".issued");
                        String expires = resultObject.getString(".expires");


                        SharedPreferences.Editor editor=sharedPreferencesUserDetails.edit();
                        editor.putString("FullName", FullName);
                        editor.putString("RoleName", RoleName);
                        editor.putString("token_type", token_type);
                        editor.putString("UserId", userId);
                        editor.putString("access_token", access_token);
                        editor.putString("UserName", UserName);
                        editor.putString("expires_in", expires_in);
                        editor.putString("ImgUrl", ImgUrl);
                        editor.putString("pass", mPasswordView);
                        editor.putString("mobile", Mobile);
                        editor.putString("email", Email);
                        editor.commit();

//                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                        builder.setMessage("")
//                                .setTitle("Success")
//                                .setIcon(R.drawable.ic_success)
//                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                        AlertDialog alert =builder.create();
//
//                        alert.show();
                        Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
                        startActivity(intent);
                        finishAffinity();

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("something went wrong in login")
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

    }
}