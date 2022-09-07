package in.co.fastride.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import in.co.fastride.DriverDetailsFragment;
import in.co.fastride.EditprofileFragment;
import in.co.fastride.R;
import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;



public class ProfileFragment extends Fragment {
    JsonObjectHandler handler;
    RelativeLayout layout_third_editprofile,layout_driver_details,updateBottomLayout;
    RelativeLayout updateProfile,dark_layout_update;
    SharedPreferences sharedPreferencesUserDetails;
    String sUserName,sUserImage,sAccessToken,sRoleName,sUserId,sTokenType,NewAccessTokan,sMobile,sEmail;
    TextView userFullName,userType,mobile,mobile_number,licence_number,city_name,pin_code,blood_group,
            em_contact_number,vehicle_number,vehicleType;
    LinearLayout linlaHeaderProgress;
    ImageView userImagePreview,rc_ImageView;
    Bitmap bitmap;

    int SELECT_PICTURE = 100;

    String base64str;
    Uri profileIMG;
    Uri baseURL;
    Uri userImg;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

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



        userFullName=view.findViewById(R.id.user_full_name);
        userType=view.findViewById(R.id.user_type);
        userImagePreview=view.findViewById(R.id.profile_dp);
        linlaHeaderProgress = view.findViewById(R.id.linlaHeaderProgress);
        mobile_number = view.findViewById(R.id.mobile_number);
        licence_number = view.findViewById(R.id.licence_number);
        city_name = view.findViewById(R.id.city_name);
        pin_code = view.findViewById(R.id.pin_code);
        blood_group = view.findViewById(R.id.blood_group);
        em_contact_number = view.findViewById(R.id.em_contact_number);
        vehicle_number = view.findViewById(R.id.vehicle_number);
        vehicleType = view.findViewById(R.id.vehicle_type);
        rc_ImageView = view.findViewById(R.id.rc_imageView);
        updateBottomLayout = view.findViewById(R.id.bottom);
        layout_third_editprofile = view.findViewById(R.id.layout_third_editprofile);
        layout_driver_details = view.findViewById(R.id.layout_driver_details);

        linlaHeaderProgress.setVisibility(View.VISIBLE);
        new GetDriverDetails(NewAccessTokan).execute();

        userFullName.setText("Hello"+" "+sUserName+"!");
        userType.setText(sRoleName);
        mobile_number.setText(sMobile);

        Picasso.get()
                .load(AppConstant.baseURL+sUserImage)
                .error(R.drawable.ic_person)
                .into(userImagePreview);
//        userImagePreview.setImageURI(profileIMG);

        userImagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });


        layout_third_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                EditprofileFragment fragment = new EditprofileFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("ProfileFragment")
                        .commit();
            }
        });
        layout_driver_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DriverDetailsFragment fragment = new DriverDetailsFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("ProfileFragment")
                        .commit();
            }
        });
        updateBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DriverDetailsFragment fragment = new DriverDetailsFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("ProfileFragment")
                        .commit();
            }
        });

        return view;
    }

    void imageChooser() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    userImagePreview.setImageURI(selectedImageUri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        if (bitmap != null) {
                            base64str = (String) encodeFromString(bitmap);

                            new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Object encodeFromString(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 70, baos); //bm is the bitmap object

        byte[] b = baos.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public class GetDriverDetails extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String sNewAccessTokan;

        GetDriverDetails(String NewAccessTokan){
            this.sNewAccessTokan=NewAccessTokan;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();

                jsonObject=handler.makeHttpRequest(AppConstant.GetDriverDetailsURL, "GET", data,NewAccessTokan);

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
//                        JSONObject driverObject=jsonObject.optJSONObject("driver");

                        if (!resultObject.equals(null)){
                            JSONObject driverInfoObject=resultObject.optJSONObject("driver");
                            if (!driverInfoObject.equals(null)){
                                String DriverId = driverInfoObject.getString("Id");
                                String IsVerified = driverInfoObject.getString("IsVerified");
                                String StateName = driverInfoObject.getString("StateName");
                                String CityName = driverInfoObject.getString("CityName");
                                String StateId = driverInfoObject.getString("StateId");
                                String CityId = driverInfoObject.getString("CityId");
                                String DrivingLicence = driverInfoObject.getString("DrivingLicence");
                                String PANImage = driverInfoObject.getString("PANImage");
                                String PinCode = driverInfoObject.getString("PinCode");
                                String EmergencyContactNumber = driverInfoObject.getString("EmergencyContactNumber");
                                String BloodGroup = driverInfoObject.getString("BloodGroup");
                                String CreatedDate = driverInfoObject.getString("CreatedDate");
                                String ModifiedDate = driverInfoObject.getString("ModifiedDate");

                                licence_number.setText(StateName);
                                city_name.setText(CityName);
                                pin_code.setText(PinCode);
                                blood_group.setText(BloodGroup);
                                em_contact_number.setText(EmergencyContactNumber);

                            }
                        }
                        JSONObject vehicleInfoObject=resultObject.optJSONObject("vehicle");
                        if (!vehicleInfoObject.equals(null)){

                            String VehicleId = vehicleInfoObject.getString("Id");
                            String VehicleIsVerified = vehicleInfoObject.getString("IsVerified");
                            String ModalName = vehicleInfoObject.getString("ModalName");
                            String RCImage = vehicleInfoObject.getString("RCImage");
                            String VehicleNumber = vehicleInfoObject.getString("VehicleNumber");
                            String VehicleType = vehicleInfoObject.getString("VehicleType");
                            String VehicleIconUrl = vehicleInfoObject.getString("VehicleIconUrl");
                            String VehicleTypeId = vehicleInfoObject.getString("VehicleTypeId");
                            String VehicleCreatedDate = vehicleInfoObject.getString("CreatedDate");
                            String VehicleModifiedDate = vehicleInfoObject.getString("ModifiedDate");

                            vehicle_number.setText(VehicleNumber);
                            vehicleType.setText(VehicleType);

                            Picasso.get()
                                    .load(AppConstant.baseURL+RCImage)
                                    .error(R.drawable.ic_person)
                                    .placeholder(R.drawable.ic_menu_camera)
                                    .into(rc_ImageView);
                        }

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
//                Toast.makeText(getActivity(),"Hello Arvind DEtails uodated",Toast.LENGTH_LONG).show();
//                AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
//                builder.setMessage("Something went wrong please Try later")
//                        .setTitle("Warning")
//                        .setIcon(R.drawable.ic_warning)
//                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                AlertDialog alert =builder.create();
//                alert.show();
            }
        }

    }

    public class UpdateProfileImage extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String sUserId;
        String sBase64str;
        String sNewAccessTokan;

        UpdateProfileImage(String UserId,String Base64str,String NewAccessTokan){
            this.sUserId = UserId;
            this.sBase64str = Base64str;
            this.sNewAccessTokan=NewAccessTokan;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("UserId",""+sUserId);
                data.put("UserImage",""+sBase64str);

                jsonObject=handler.makeHttpRequest(AppConstant.UpdateProfileImageURL, "POST", data,NewAccessTokan);

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
                        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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