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
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;

public class ResetPasswordActivity extends AppCompatActivity {
    EditText newPassword,cnfmPassword;
    TextView login;
    Button submitBtn;
    JsonObjectHandler handler;
    SharedPreferences sharedPreferencesOtpMobile;
    LinearLayout linlaHeaderProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().hide();

        newPassword=findViewById(R.id.new_password_et);
        cnfmPassword=findViewById(R.id.confirm_password_et);
        submitBtn=findViewById(R.id.submit_btn);
        login = findViewById(R.id.lnk_login);
        linlaHeaderProgress =  findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);

        sharedPreferencesOtpMobile= getApplicationContext().getSharedPreferences("otpMobile", MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String NewPassword=newPassword.getText().toString();
                String ConfirmPassword=cnfmPassword.getText().toString();
                String PasswordHashCode=sharedPreferencesOtpMobile.getString("PasswordHashCode","");
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                closeKeyboard();
                new ChangePasswordPost(NewPassword,ConfirmPassword,PasswordHashCode).execute();

            }
        });
    }
    public class ChangePasswordPost extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String newPassword;
        String confirmPassword;
        String passwordHashCode;

        ChangePasswordPost(String NewPassword, String ConfirmPassword, String PasswordHashCode){
            this.newPassword=NewPassword;
            this.confirmPassword=ConfirmPassword;
            this.passwordHashCode=PasswordHashCode;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("PasswordHashCode",""+passwordHashCode);
                data.put("Password",""+newPassword);
                data.put("ConfirmPassword",""+confirmPassword);
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(AppConstant.resetPasswordURL, "POST", data,null);

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
//                        JSONObject resultObject=jsonObject.optJSONObject("Result");
//
                        AlertDialog.Builder alertd=new AlertDialog.Builder(ResetPasswordActivity.this);
                        alertd.setMessage("")
                                .setTitle("Password changed successfully.")
                                .setIcon(R.drawable.ic_success)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent loginIntent= new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                        startActivity(loginIntent);
                                    }
                                });

                        alertd.show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                builder.setMessage("Exception")
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}