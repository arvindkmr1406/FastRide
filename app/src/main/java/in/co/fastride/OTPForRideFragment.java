package in.co.fastride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class OTPForRideFragment extends Fragment {

Button submit;
    public OTPForRideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_for_ride, container, false);

        submit = view.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DroppingFragment fragment = new DroppingFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("OtoForRideFragment")
                        .commit();
            }
        });
        return view;
    }
}