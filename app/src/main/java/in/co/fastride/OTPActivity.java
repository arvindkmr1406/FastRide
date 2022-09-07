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
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;

public class OTPActivity extends AppCompatActivity {
    EditText otpCode;
    Button submit;
    private SharedPreferences sharedPreferencesOtpMobile;
    private SharedPreferences sharedPreferencesUserDetails;
    JsonObjectHandler handler;
    String otpRequestFor,registerOTPmsg;
    Calendar calendar;
    String ReqUrl;
    int countdown=50;
    TextView timertv,loginlnk,otp_msg,reSendbtn;
    //    ArrayList<SMSLogTB> smsLogList;
    private static final int REQUEST_CONTACTS = 1;
    LinearLayout linlaHeaderProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        getSupportActionBar().hide();

        otpCode=findViewById(R.id.otp_code_et);
        submit=findViewById(R.id.btn_submit);
        reSendbtn = findViewById(R.id.btn_re_send_otp);
        timertv=findViewById(R.id.time_tv);
        loginlnk=findViewById(R.id.lnk_login);
//        otp_msg = findViewById(R.id.otp_msg);
        linlaHeaderProgress =  findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);

        reSendbtn.setEnabled(false);
        calendar=Calendar.getInstance();

        sharedPreferencesUserDetails= getApplicationContext().getSharedPreferences("myapp", MODE_PRIVATE);
        sharedPreferencesOtpMobile= getApplicationContext().getSharedPreferences("otpMobile", MODE_PRIVATE);

        new CountDownTimer(50000, 1000) {

            public void onTick(long millisUntilFinished) {
                timertv.setText( millisUntilFinished / 1000+" seconds remaining: ");
            }

            public void onFinish() {
                reSendbtn.setEnabled(true);
                timertv.setText("done!");
            }
        }.start();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String OTPMobile=sharedPreferencesOtpMobile.getString("mobile","");
                otpRequestFor=sharedPreferencesOtpMobile.getString("RequestFor","");

                String OTPCode= otpCode.getText().toString();

                if(otpRequestFor.equals("Register"))
                {
                    ReqUrl= AppConstant.userOTPConfirmURL;
                }
                else if(otpRequestFor.equals("ResetPassword"))
                {
                    ReqUrl=AppConstant.userChangePasswordOtpURL;
                }
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                closeKeyboard();
                new OTPCodePost(OTPCode,OTPMobile).execute();
            }
        });

        reSendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTPMobile=sharedPreferencesOtpMobile.getString("mobile","");
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                closeKeyboard();
                new OTPResendPost(OTPMobile).execute();
            }
        });


        loginlnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent skipIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(skipIntent);
            }
        });

    }

    public class OTPCodePost extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String mobileNo;
        String otpCode;

        OTPCodePost(String OtpCode,String Mobile){
            this.mobileNo=Mobile;
            this.otpCode=OtpCode;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("MobileNo",""+mobileNo);
                data.put("OTPCode",""+otpCode);
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(ReqUrl, "POST", data,null);

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

                        if(otpRequestFor.equals("Register")) {
                            String userId = resultObject.getString("UserId");
                            String UserName = resultObject.getString("UserName");
                            String access_token = resultObject.getString("access_token");
                            String token_type = resultObject.getString("token_type");
                            String expires_in = resultObject.getString("expires_in");
                            String RoleName = resultObject.getString("RoleName");
                            String FullName = resultObject.getString("FullName");
                            String ImgUrl = resultObject.getString("ImgUrl");

                            SharedPreferences.Editor editor = sharedPreferencesUserDetails.edit();
                            editor.putString("FullName", FullName);
                            editor.putString("RoleName", RoleName);
                            editor.putString("token_type", token_type);
                            editor.putString("ImgUrl", ImgUrl);
                            editor.putString("access_token", access_token);
                            editor.putString("UserName", UserName);
                            editor.putString("userId", userId);
                            editor.putString("mobile", "" + mobileNo);
                            editor.commit();

                            Intent dashBoardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(dashBoardIntent);
                        }
                        else if(otpRequestFor.equals("ResetPassword"))
                        {
                            String passwordHashCode = resultObject.getString("PasswordHashCode");
                            SharedPreferences.Editor editor=sharedPreferencesOtpMobile.edit();
                            editor.putString("PasswordHashCode", ""+passwordHashCode);
                            editor.commit();

                            Intent resetpassIntent= new Intent(getApplicationContext(),ResetPasswordActivity.class);
                            startActivity(resetpassIntent);

                        }
                        else{

                            new AlertDialog.Builder(getApplicationContext())
                                    .setTitle("Warning")
                                    .setMessage(message)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();

//                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                            builder.setMessage("Invalid request")
//                                    .setTitle("Warning")
//                                    .setIcon(R.drawable.ic_warning)
//                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            dialog.dismiss();
//                                        }
//                                    });
//                            AlertDialog alert =builder.create();
//                            alert.show();
                        }
                    }
                    else {
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Warning")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                        builder.setMessage(message)
//                                .setTitle("Warning")
//                                .setIcon(R.drawable.ic_warning)
//                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        AlertDialog alert =builder.create();
//                        alert.show();
                    }
                }

            }catch (Exception e){
                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Warning")
                        .setMessage("Exception Try Again Letter")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }

    }

    public class OTPResendPost extends AsyncTask<Void,Void,JSONObject> {

        JSONObject jsonObject=null;
        String mobileNo;

        OTPResendPost(String Mobile){
            this.mobileNo=Mobile;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("MobileNo",""+mobileNo);
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(AppConstant.resendOtpRequestURL, "POST", data,null);

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


                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Status")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                        builder.setMessage(message)
//                                .setTitle("Success")
//                                .setIcon(R.drawable.ic_success)
//                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        AlertDialog alert =builder.create();
//                        alert.show();

                    }
                    else {
                        new AlertDialog.Builder(getApplicationContext())
                                .setTitle("Warning")
                                .setMessage(message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
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