package in.co.fastride;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class ForgotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        getSupportActionBar().hide();

        FragmentManager fragmentManager =getSupportFragmentManager();
        ForgotFragment fragment =new ForgotFragment();
        fragmentManager.beginTransaction().add(R.id.frame_container,fragment).commit();
    }
}