package in.co.fastride;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;


public class EditprofileFragment extends Fragment {

    private SharedPreferences sharedPreferencesUserDetails;
    String sUserName,sUserImage,sAccessToken,sRoleName,sUserId,sTokenType,NewAccessTokan,sMobile,sEmail;
    EditText sName_et,sEmail_et,sContact_et;
    Button sBtn_submit;
    JsonObjectHandler handler;


    public EditprofileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);

        sharedPreferencesUserDetails=getActivity().getSharedPreferences("myapp", Context.MODE_PRIVATE);
        sUserName=sharedPreferencesUserDetails.getString("FullName","");
        sAccessToken=sharedPreferencesUserDetails.getString("access_token","");
        sUserImage=sharedPreferencesUserDetails.getString("ImgUrl","");
        sRoleName=sharedPreferencesUserDetails.getString("RoleName","");
        sUserId=sharedPreferencesUserDetails.getString("UserId","");
        sTokenType=sharedPreferencesUserDetails.getString("token_type","");
        sMobile=sharedPreferencesUserDetails.getString("mobile","");
        sEmail=sharedPreferencesUserDetails.getString("email","");
        NewAccessTokan = sTokenType+" "+sAccessToken;

        sName_et = view.findViewById(R.id.name_et);
        sEmail_et = view.findViewById(R.id.email_et);
        sContact_et = view.findViewById(R.id.contact_et);
        sBtn_submit = view.findViewById(R.id.btn_submit);

        sName_et.setText(sUserName);
        sEmail_et.setText(sEmail);
        sContact_et.setText(sMobile);

        sBtn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String Name = sName_et.getText().toString();
              String Email = sEmail_et.getText().toString();
              String Contact = sContact_et.getText().toString();

              new UpdateProfile(Name,Email,Contact,NewAccessTokan).execute();
            }
        });

        return view;
    }

    public class UpdateProfile extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String sName;
        String sEmail;
        String sContact;
        String sNewAccessTokan;

        UpdateProfile(String Name,String Email,String Contact, String NewAccessTokan){
            this.sName = Name;
            this.sEmail = Email;
            this.sContact = Contact;
            this.sNewAccessTokan=NewAccessTokan;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("MobileNo",""+sContact);
                data.put("Email",""+sEmail);
                data.put("FullName",""+sName);

                jsonObject=handler.makeHttpRequest(AppConstant.UpdateProfileURL, "POST", data,NewAccessTokan);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected  void onPostExecute(final JSONObject jsonObject){

            try {
                if (jsonObject!=null){
                    JSONObject statusObject=jsonObject.optJSONObject("ReqStatus");
                    String status=statusObject.getString("Status");
                    String message=statusObject.getString("Message");
                    if (statusObject.getBoolean("Status")){
                        JSONObject resultObject=jsonObject.optJSONObject("Result");
                        Toast.makeText(getContext(),"Details Change successfully",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getContext(),LoginActivity.class);
                        startActivity(intent);

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Something went wrong please Try later")
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