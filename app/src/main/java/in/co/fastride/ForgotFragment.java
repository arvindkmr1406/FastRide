package in.co.fastride;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;


public class ForgotFragment extends Fragment {

    TextView loginlnk;
    EditText mobileNo;
    Button btnsendOTP;
    JsonObjectHandler handler;
    private SharedPreferences sharedPreferencesUserDetails;
    LinearLayout linlaHeaderProgress;
    public ForgotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot, container, false);

        mobileNo =view.findViewById(R.id.mobile_et);
        btnsendOTP=view.findViewById(R.id.btn_otp_send);
        loginlnk=view.findViewById(R.id.lnk_login);
        linlaHeaderProgress =  view.findViewById(R.id.linlaHeaderProgress);
        linlaHeaderProgress.setVisibility(View.GONE);

        sharedPreferencesUserDetails= getActivity().getSharedPreferences("otpMobile", MODE_PRIVATE);
//        String OTPMobile=sharedPreferencesUserDetails.getString("mobile","");
//        mobileNo.setText(OTPMobile);

        loginlnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
            }
        });


        btnsendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mobileNo.getText().toString().length() == 0 ) {
                    mobileNo.setError("Enter mobile number!");
                    return;
                }
                String MobileNo=mobileNo.getText().toString();
                linlaHeaderProgress.setVisibility(View.VISIBLE);
                closeKeyboard();
                new ResetPasswordPost(MobileNo).execute();
            }
        });

        return view;
    }
    public class ResetPasswordPost extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String mobileNo;

        ResetPasswordPost(String Mobile){
            this.mobileNo=Mobile;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("MobileNo",""+mobileNo);
                //handler=new JsonObjectHandler();
                jsonObject=handler.makeHttpRequest(AppConstant.resetPasswordRequestURL, "POST", data,null);

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

                        sharedPreferencesUserDetails= getActivity().getSharedPreferences("otpMobile", MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferencesUserDetails.edit();
                        editor.putString("mobile", ""+mobileNo);
                        editor.putString("RequestFor", "ResetPassword");
                        editor.putString("otp_msg",message);
                        editor.commit();
                        Intent intent = new Intent(getActivity(),OTPActivity.class);
                        startActivity(intent);
//                        FragmentManager fragmentManager = getFragmentManager();
//                        OtpFragment fragment = new OtpFragment();
//                        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}