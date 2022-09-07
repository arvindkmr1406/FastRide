package in.co.fastride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class RoutMapFragment extends Fragment {

ImageView profile_dp_rout;
LinearLayout decline_layout;
    public RoutMapFragment() {
        // Required empty public constructor
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rout_map, container, false);
       profile_dp_rout = view.findViewById(R.id.profile_dp_rout);
       decline_layout = view.findViewById(R.id.decline_layout);

       decline_layout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fm = getFragmentManager();
               FindTripFragment fragment = new FindTripFragment();
               fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                       .addToBackStack("RoutMapFragment")
                       .commit();
           }
       });
       profile_dp_rout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fm = getFragmentManager();
               RiderNotifyFragment fragment = new RiderNotifyFragment();
               fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                       .addToBackStack("RoutMapFragment")
                       .commit();
           }
       });

        return view;
    }
}