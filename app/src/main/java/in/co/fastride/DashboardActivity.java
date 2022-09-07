package in.co.fastride;

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
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import in.co.fastride.apihandler.AppConstant;
import in.co.fastride.apihandler.JsonObjectHandler;
import in.co.fastride.databinding.ActivityDashboardBinding;
import in.co.fastride.ui.about.AboutFragment;
import in.co.fastride.ui.home.HomeFragment;
import in.co.fastride.ui.mywallet.MyWalletFragment;
import in.co.fastride.ui.profile.ProfileFragment;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        private SharedPreferences sharedPreferencesUserDetails;
        String sUserName,sUserImage,sAccessToken,sRoleName,sUserId,sTokenType,NewAccessTokan,sMobile,sEmail;
        TextView userName;
        ImageView userImage;
        JsonObjectHandler handler;
        Toolbar toolbar;
        //FloatingActionButton newEntryFab;
        Bitmap profilebitmap;
        private static final int SELECT_PICTURE = 1;
        String base64str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final int sdk = android.os.Build.VERSION.SDK_INT;

        sharedPreferencesUserDetails=getApplicationContext().getSharedPreferences("myapp", Context.MODE_PRIVATE);
        sUserName=sharedPreferencesUserDetails.getString("FullName","");
        sAccessToken=sharedPreferencesUserDetails.getString("access_token","");
        sUserImage=sharedPreferencesUserDetails.getString("ImgUrl","");
        sRoleName=sharedPreferencesUserDetails.getString("RoleName","");
        sUserId=sharedPreferencesUserDetails.getString("UserId","");
        sTokenType=sharedPreferencesUserDetails.getString("token_type","");
        sMobile=sharedPreferencesUserDetails.getString("mobile","");
        sEmail=sharedPreferencesUserDetails.getString("email","");
        NewAccessTokan = sTokenType+" "+sAccessToken;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, new HomeFragment()).commit();
        toolbar.setTitle("Dashboard");

        View hView =  navigationView.getHeaderView(0);
        userName=hView.findViewById(R.id.userName_tv);
        userImage=hView.findViewById(R.id.userImage_iv);
        //userName.setText(sUserName);
        Menu menuNav=navigationView.getMenu();
        MenuItem nav_logout = menuNav.findItem(R.id.nav_Logout);
        MenuItem nav_resetPwd = menuNav.findItem(R.id.nav_Reset_Password);
        MenuItem nav_login = menuNav.findItem(R.id.nav_Login);
        MenuItem nav_profile = menuNav.findItem(R.id.nav_Profile);

        if (sAccessToken != null && sAccessToken != "") {
            nav_logout.setVisible(true);
            userName.setText(sUserName);
            nav_resetPwd.setVisible(true);
            nav_login.setVisible(false);
            nav_profile.setVisible(true);
            Picasso.get()
                    .load(AppConstant.baseURL+sUserImage)
//                    .load("https://tineye.com/images/widgets/mona.jpg")
                    .error(R.drawable.ic_person)
                    .placeholder(R.drawable.ic_menu_camera)
                    .into(userImage);

//            userImage.setImageURI(sUserImage);
        }else{
            nav_logout.setVisible(false);
            userName.setText("Hello Guest");
            nav_resetPwd.setVisible(false);
            nav_login.setVisible(true);
            nav_profile.setVisible(false);
        }
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sAccessToken != null && sAccessToken != "") {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Picture"), SELECT_PICTURE);
                }else {
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            // selectedImagePath = getPath(selectedImageUri);
            userImage.setImageURI(selectedImageUri);
            try {
                profilebitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                if (profilebitmap != null) {
                    base64str = encodeFromString(profilebitmap);

                    new UpdateProfileImage(sUserId, base64str, NewAccessTokan).execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static String encodeFromString(Bitmap bm){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 70, baos); //bm is the bitmap object

        byte[] b = baos.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(b,0,b.length);

        return Base64.encodeToString(b, Base64.DEFAULT);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//       newEntryFab.setVisibility(View.GONE);
        Fragment fragment = null;
        if (id == R.id.nav_home) {
            //toolbar.setTitle("Dashboard");
//            newEntryFab.setVisibility(View.GONE);
            fragment = new HomeFragment();

        }else if (id == R.id.nav_Reset_Password) {
            Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_MyWallet) {
            fragment=new MyWalletFragment();
            setTitle("My Wallet");

        } else if (id == R.id.nav_Profile) {
            fragment=new ProfileFragment();
            setTitle("Profile");
        } else if (id == R.id.nav_About) {
            fragment=new AboutFragment();
            setTitle("About");
        } else if (id == R.id.nav_Login) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_Logout) {
            if (sAccessToken != null && sAccessToken != "") {
                new AccessTokenLogout(sTokenType +" "+sAccessToken).execute();
                //toolbar.setTitle("Logout");
            }

        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
//                    .addToBackStack("Dashboard")
                    .commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class AccessTokenLogout extends AsyncTask<Void,Void, JSONObject> {

        JSONObject jsonObject=null;
        String AccessToken;

        AccessTokenLogout(String mAccessToken){
            this.AccessToken=mAccessToken;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();

                jsonObject=handler.makeHttpRequest(AppConstant.userLogoutURL, "POST", data,AccessToken);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonObject;
        }

        @Override
        protected  void onPostExecute(final JSONObject jsonObject){
            try {
                SharedPreferences.Editor editor=sharedPreferencesUserDetails.edit();
                editor.putString("FullName", "");
                editor.putString("RoleName", "");
                editor.putString("token_type", "");
                editor.putString("ImgUrl", "");
                editor.putString("access_token", "");
                editor.putString("UserName", "");
                editor.putString("userId", "");
                editor.putString("pass", "");
                editor.putString("mobile", "");
                editor.commit();

                Intent loginIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(loginIntent);

            }catch (Exception e){

            }
        }

    }


    public class UpdateProfileImage extends AsyncTask<Void,Void,JSONObject> {

        JSONObject jsonObject=null;
        String userId;
        String imageUri;
        String NewAccessTokan;
        UpdateProfileImage(String UserID,String ImageUri,String NewAccessTokan){
            this.userId=UserID;
            this.imageUri=ImageUri;
            this.NewAccessTokan=NewAccessTokan;
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            handler=new JsonObjectHandler();
            try {
                HashMap<String,String> data=new HashMap<>();
                data.put("UserId",""+userId);
                data.put("UserImage",""+imageUri);

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
                        Toast.makeText(getApplicationContext(),"Profile Image Updated",Toast.LENGTH_LONG).show();

                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
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
