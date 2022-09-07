package in.co.fastride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class RiderNotifyFragment extends Fragment {
Button btn_start_ride;
    public RiderNotifyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider_notify, container, false);

        btn_start_ride = view.findViewById(R.id.btn_start_ride);

        btn_start_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                OTPForRideFragment fragment = new OTPForRideFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("RiderNotifyFragment")
                        .commit();
            }
        });

        return view;
    }
}