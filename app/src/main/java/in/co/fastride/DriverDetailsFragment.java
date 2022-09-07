package in.co.fastride;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;
import in.co.fastride.ui.profile.ProfileFragment;


public class DriverDetailsFragment extends Fragment {
    private SharedPreferences sharedPreferencesUserDetails;
    String sUserName,sAccessToken,sRoleName,sUserId,sTokenType,NewAccessTokan,sMobile,sEmail;
    EditText pinCode,emContact,modelName,vehicleNumber;
    Spinner citySpnr,vehicleSpnr,bloodGrpSpnr;
    Button btnSubmit;
    TextView cityName,cityID,stateName,panCardUpload,dlUpload,rcUpload,insuranceUpload,vehicleId,
            vehicleIconUrl,vehicleName,bloodGrpName;
    LinearLayout linlaHeaderProgress;
    JsonObjectHandler handler;
    private ArrayList<Integer> cityIdlist;
    private ArrayList<String> cityList;
    private ArrayList<String> stateList;
    ArrayAdapter<String> cityNameAdapter;

    private ArrayList<Integer> vehicleIdlist;
    private ArrayList<String> vehicleList;
    private ArrayList<String> vehicleIconUrlList;
    ArrayAdapter<String> vehicleNameAdapter;


    private ArrayList<String> bloodGroupList;
    ArrayAdapter<String> bloodGroupNameAdapter;
    // One Preview Image
    ImageView PreviewImagePan,PreviewImageDL,PreviewImageRC,PreviewImageINS;
    Bitmap bitmap;

    // constant to compare
    // the activity result code
    int SELECT_PICTURE_PAN = 100;
    int SELECT_PICTURE_DL = 200;
    int SELECT_PICTURE_RC = 300;
    int SELECT_PICTURE_INS = 400;
    String base64strPAN,base64strDL,base64strRC,base64strINS,DRIVERID;

    public DriverDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        sharedPreferencesUserDetails=getActivity().getSharedPreferences("myapp", Context.MODE_PRIVATE);
        sUserName=sharedPreferencesUserDetails.getString("FullName","");
        sAccessToken=sharedPreferencesUserDetails.getString("access_token","");
        sRoleName=sharedPreferencesUserDetails.getString("RoleName","");
        sUserId=sharedPreferencesUserDetails.getString("UserId","");
        sTokenType=sharedPreferencesUserDetails.getString("token_type","");
        sMobile=sharedPreferencesUserDetails.getString("mobile","");
        sEmail=sharedPreferencesUserDetails.getString("email","");
        NewAccessTokan = sTokenType+" "+sAccessToken;


        View view = inflater.inflate(R.layout.fragment_driver_details, container, false);
        pinCode = view.findViewById(R.id.pin_code);
        bloodGrpName = view.findViewById(R.id.blood_grp_name);
        bloodGrpSpnr = view.findViewById(R.id.blood_grp_spnr);
        emContact = view.findViewById(R.id.em_contact);
        modelName = view.findViewById(R.id.model_name);
        vehicleNumber = view.findViewById(R.id.vehicle_number);
        panCardUpload = view.findViewById(R.id.pan_card_upload);
        dlUpload = view.findViewById(R.id.dl_upload);
        rcUpload = view.findViewById(R.id.rc_upload);
        insuranceUpload = view.findViewById(R.id.insurance_upload);
        citySpnr = view.findViewById(R.id.city_spnr);
        vehicleSpnr = view.findViewById(R.id.vehicle_spnr);
        btnSubmit = view.findViewById(R.id.btn_submit);
        cityName = view.findViewById(R.id.city_name);
        cityID = view.findViewById(R.id.city_id);
        stateName = view.findViewById(R.id.state_name);
        vehicleName = view.findViewById(R.id.vehicle_Name);
        vehicleId = view.findViewById(R.id.vehicle_Id);
        vehicleIconUrl = view.findViewById(R.id.vehicle_IconUrl);
        linlaHeaderProgress = view.findViewById(R.id.linlaHeaderProgress);
        PreviewImagePan = view.findViewById(R.id.IVPreviewImage_pan);
        PreviewImageDL = view.findViewById(R.id.IVPreviewImage_dl);
        PreviewImageRC = view.findViewById(R.id.IVPreviewImage_rc);
        PreviewImageINS = view.findViewById(R.id.IVPreviewImage_insurance);

        linlaHeaderProgress.setVisibility(View.VISIBLE);
        new GetDriverDetails(NewAccessTokan).execute();

        cityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityName.setVisibility(View.GONE);
                citySpnr.setVisibility(View.VISIBLE);
            }
        });

        handler=new JsonObjectHandler();
        cityIdlist=new ArrayList<>();
        cityList=new ArrayList<>();
        stateList=new ArrayList<>();
        new GetCityList().execute();

        citySpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String CityID = cityIdlist.get(position).toString();
                String StateName = stateList.get(position).toString();
                cityID.setText(CityID);
                stateName.setText(StateName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        vehicleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleName.setVisibility(View.GONE);
                vehicleSpnr.setVisibility(View.VISIBLE);
            }
        });
        bloodGrpName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodGrpName.setVisibility(View.GONE);
                bloodGrpSpnr.setVisibility(View.VISIBLE);
            }
        });

        handler=new JsonObjectHandler();
        vehicleIdlist=new ArrayList<>();
        vehicleList=new ArrayList<>();
        vehicleIconUrlList=new ArrayList<>();
        new GetVehicleList().execute();


        vehicleSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String VehicleID = vehicleIdlist.get(position).toString();
                String VehicleIconUrl = vehicleIconUrlList.get(position).toString();
                vehicleId.setText(VehicleID);
                vehicleIconUrl.setText(VehicleIconUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        handler=new JsonObjectHandler();
        bloodGroupList=new ArrayList<>();
        new GetBloodGroups().execute();

        bloodGrpSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String BloodGroupName = bloodGroupList.get(position).toString();
                bloodGrpName.setText(BloodGroupName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        panCardUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserPAN();
            }
        });
        dlUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserDL();
            }
        });
        rcUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserRC();
            }
        });
        insuranceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserINSURANCE();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DLImageBase64= base64strDL;
                String PANImageBase64= base64strPAN;
                String CityID= cityID.getText().toString();
                String PinCode= pinCode.getText().toString();
                String BloodGroup= bloodGrpName.getText().toString();
                String EmergencyContactNumber= emContact.getText().toString();
                String ModalName= modelName.getText().toString();
                String VehicleTypeID= vehicleId.getText().toString();
                String VehicleNumber= vehicleNumber.getText().toString();
                String RCImageBase64= base64strRC;
                String INSURANCEImageBase64= base64strINS;

                new SaveDriverVehicleDetails(DLImageBase64,PANImageBase64,CityID,PinCode,BloodGroup,EmergencyContactNumber,ModalName,VehicleTypeID,VehicleNumber,RCImageBase64,INSURANCEImageBase64,NewAccessTokan).execute();
            }
        });

        return view;
    }

    void imageChooserPAN() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_PAN);
    }
    void imageChooserDL() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_DL);
    }
    void imageChooserRC() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_RC);
    }
    void imageChooserINSURANCE() {
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE_INS);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 100) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    PreviewImagePan.setImageURI(selectedImageUri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        if (bitmap != null) {
                            base64strPAN = (String) encodeFromString(bitmap);

//                            new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if(requestCode==200){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    PreviewImageDL.setImageURI(selectedImageUri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        if (bitmap != null) {
                            base64strDL = (String) encodeFromString(bitmap);

//                            new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if (requestCode == 300){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    PreviewImageRC.setImageURI(selectedImageUri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        if (bitmap != null) {
                            base64strRC = (String) encodeFromString(bitmap);

//                            new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if (requestCode == 400){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    PreviewImageINS.setImageURI(selectedImageUri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                        if (bitmap != null) {
                            base64strINS = (String) encodeFromString(bitmap);

//                            new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
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
                        if (!resultObject.equals(null)){
                            JSONObject driverInfoObject=resultObject.optJSONObject("driver");
                            if (!driverInfoObject.equals(null)){
                                String DriverId = driverInfoObject.getString("Id");
                                String IsVerified = driverInfoObject.getString("IsVerified");
                                String StateName = driverInfoObject.getString("StateName");
                                String CityName = driverInfoObject.getString("CityName");
                                String StateId = driverInfoObject.getString("StateId");
                                String CityId = driverInfoObject.getString("CityId");
                                base64strDL = driverInfoObject.getString("DrivingLicence");
                                base64strPAN = driverInfoObject.getString("PANImage");
                                String PinCode = driverInfoObject.getString("PinCode");
                                String EmergencyContactNumber = driverInfoObject.getString("EmergencyContactNumber");
                                String BloodGroup = driverInfoObject.getString("BloodGroup");
                                String CreatedDate = driverInfoObject.getString("CreatedDate");
                                String ModifiedDate = driverInfoObject.getString("ModifiedDate");

                                pinCode.setText(PinCode);
                                bloodGrpName.setText(BloodGroup);
                                emContact.setText(EmergencyContactNumber);

                                cityName.setText(CityName);
                                cityID.setText(CityId);
                                stateName.setText(StateName);

                                Picasso.get()
                                        .load(AppConstant.baseURL+base64strPAN)
                                        .error(R.drawable.ic_person)
                                        .into(PreviewImagePan);

                                Picasso.get()
                                        .load(AppConstant.baseURL+base64strDL)
                                        .error(R.drawable.ic_person)
                                        .into(PreviewImageDL);

                            }
                        }
                        JSONObject vehicleInfoObject=resultObject.optJSONObject("vehicle");
                        if (!vehicleInfoObject.equals(null)){

                            String VehicleId = vehicleInfoObject.getString("Id");
                            String VehicleIsVerified = vehicleInfoObject.getString("IsVerified");
                            String ModalName = vehicleInfoObject.getString("ModalName");
                            base64strRC = vehicleInfoObject.getString("RCImage");
                            base64strINS = vehicleInfoObject.getString("InsuranceImage");
                            String VehicleNumber = vehicleInfoObject.getString("VehicleNumber");
                            String VehicleType = vehicleInfoObject.getString("VehicleType");
                            String VehicleIconUrl = vehicleInfoObject.getString("VehicleIconUrl");
                            String VehicleTypeId = vehicleInfoObject.getString("VehicleTypeId");
                            String VehicleCreatedDate = vehicleInfoObject.getString("CreatedDate");
                            String VehicleModifiedDate = vehicleInfoObject.getString("ModifiedDate");

                            modelName.setText(ModalName);
                            vehicleNumber.setText(VehicleNumber);
                            vehicleName.setText(VehicleType);
                            vehicleId.setText(VehicleId);

                            Picasso.get()
                                    .load(AppConstant.baseURL+base64strRC)
                                    .error(R.drawable.ic_person)
                                    .into(PreviewImageRC);
                        Picasso.get()
                                .load(AppConstant.baseURL+base64strINS)
                                .error(R.drawable.ic_person)
                                .into(PreviewImageINS);

//                            Picasso.get()
//                                    .load(AppConstant.baseURL+InsuranceImage)
//                                    .error(R.drawable.ic_person)
//                                    .placeholder(R.drawable.ic_menu_camera)
//                                    .into(PreviewImageRC);
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

    public class GetCityList extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        GetCityList() {

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                HashMap<String,String> data=new HashMap<>();
                jsonObject=handler.makeHttpRequest(AppConstant.AllCityListURL, "GET", data,null);

            } catch (Exception e) {

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
                        JSONArray stateObjectList=jsonObject.optJSONArray("Result");

                        for (int i=0;i<stateObjectList.length();i++){
                            JSONObject tempobject=stateObjectList.getJSONObject(i);
                            cityIdlist.add(tempobject.getInt("Id"));
                            cityList.add(tempobject.getString("CityName"));
                            stateList.add(tempobject.getString("StateName"));

                            cityNameAdapter= new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,cityList);
                            cityNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            citySpnr.setAdapter(cityNameAdapter);
                        }
                    }
                }

            }catch (Exception e){

            }
        }
    }

    public class GetVehicleList extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        GetVehicleList() {

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                HashMap<String,String> data=new HashMap<>();
                jsonObject=handler.makeHttpRequest(AppConstant.VehicleTypesURL, "GET", data,null);

            } catch (Exception e) {

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
                        JSONArray stateObjectList=jsonObject.optJSONArray("Result");

                        for (int i=0;i<stateObjectList.length();i++){
                            JSONObject tempobject=stateObjectList.getJSONObject(i);
                            vehicleIdlist.add(tempobject.getInt("Id"));
                            vehicleList.add(tempobject.getString("Type"));
                            vehicleIconUrlList.add(tempobject.getString("VehicleIconUrl"));

                            vehicleNameAdapter= new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,vehicleList);
                            vehicleNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            vehicleSpnr.setAdapter(vehicleNameAdapter);
                        }
                    }
                }

            }catch (Exception e){

            }
        }
    }

    public class GetBloodGroups extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        GetBloodGroups() {

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                HashMap<String,String> data=new HashMap<>();
                jsonObject=handler.makeHttpRequest(AppConstant.GetBloodGroupsURL, "GET", data,null);

            } catch (Exception e) {

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
                        JSONArray stateObjectList=jsonObject.optJSONArray("Result");

                        for (int i=0;i<stateObjectList.length();i++){
                            JSONObject tempobject=stateObjectList.getJSONObject(i);

                            bloodGroupList.add(tempobject.getString("GroupName"));

                            bloodGroupNameAdapter= new ArrayAdapter<String>(getActivity(),R.layout.spinneritem,bloodGroupList);
                            bloodGroupNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            bloodGrpSpnr.setAdapter(bloodGroupNameAdapter);
                        }
                    }
                }

            }catch (Exception e){

            }
        }
    }

    public class SaveDriverVehicleDetails extends AsyncTask<Void,Void,JSONObject> {

        JSONObject jsonObject=null;
        String sDLImageBase64;
        String sPANImageBase64;
        String sCityID;
        String sPinCode;
        String sBloodGroup;
        String sEmergencyContactNumber;
        String sModalName;
        String sVehicleTypeID;
        String sVehicleNumber;
        String sRCImageBase64;
        String sINSURANCEImageBase64;
        String sNewAccessTokan;

        SaveDriverVehicleDetails(String DLImageBase64,String PANImageBase64,String CityID,String PinCode,String BloodGroup,String EmergencyContactNumber,String ModalName,String VehicleTypeID,String VehicleNumber,String RCImageBase64,String INSURANCEImageBase64, String NewAccessTokan){
            this.sDLImageBase64 = DLImageBase64;
            this.sPANImageBase64 = PANImageBase64;
            this.sCityID = CityID;
            this.sPinCode = PinCode;
            this.sBloodGroup = BloodGroup;
            this.sEmergencyContactNumber = EmergencyContactNumber;
            this.sModalName = ModalName;
            this.sVehicleTypeID = VehicleTypeID;
            this.sVehicleNumber = VehicleNumber;
            this.sRCImageBase64 = RCImageBase64;
            this.sINSURANCEImageBase64 = INSURANCEImageBase64;
            this.sNewAccessTokan=NewAccessTokan;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("DrivingLicenceImage",""+sDLImageBase64);
                data.put("PANImage",""+sPANImageBase64);
                data.put("CityId",""+sCityID);
                data.put("PinCode",""+sPinCode);
                data.put("BloodGroup",""+sBloodGroup);
                data.put("EmergencyContactNumber",""+sEmergencyContactNumber);
                data.put("ModalName",""+sModalName);
                data.put("VehicleTypeId",""+sVehicleTypeID);
                data.put("VehicleNumber",""+sVehicleNumber);
                data.put("RCImage",""+sRCImageBase64);
                data.put("InsuranceImage",""+sINSURANCEImageBase64);

                jsonObject=handler.makeHttpRequest(AppConstant.SaveDriverVehicleDetailsURL, "POST", data,NewAccessTokan);

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
                        Toast.makeText(getContext(),"Details save successfully",Toast.LENGTH_LONG).show();

                        pinCode.setText("");
                        bloodGrpName.setText("");
                        emContact.setText("");
                        modelName.setText("");
                        vehicleNumber.setText("");
                        panCardUpload.setText("");
                        dlUpload.setText("");
                        rcUpload.setText("");
                        insuranceUpload.setText("");
                        cityName.setText("");
                        cityID.setText("");
                        stateName.setText("");
                        vehicleName.setText("");
                        vehicleId.setText("");
                        vehicleIconUrl.setText("");

                        PreviewImagePan.setImageURI(null);
                        PreviewImageDL.setImageURI(null);
                        PreviewImageRC.setImageURI(null);
                        PreviewImageINS.setImageURI(null);

                        FragmentManager fm = getFragmentManager();
                        ProfileFragment fragment = new ProfileFragment();
                        fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                                .addToBackStack("ProfileFragment")
                                .commit();

//                        ProfileFragment fragment2 = new ProfileFragment();
//                        FragmentManager fragmentManager = getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment2);
//                        fragmentTransaction.commit();
//
//                        FragmentManager fragmentManager = getFragmentManager();
//                        ProfileFragment fragment = new ProfileFragment();
//                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
//                        Intent intent = new Intent(getContext(),DashboardActivity.class);
//                        startActivity(intent);

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