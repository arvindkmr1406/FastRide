package in.co.fastride;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class DroppingFragment extends Fragment {
TextView btn_complete_ride;

    public DroppingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dropping, container, false);

        btn_complete_ride = view.findViewById(R.id.btn_complete_ride);


        btn_complete_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PaymentRatingFragment fragment = new PaymentRatingFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("DroppingFragment")
                        .commit();
            }
        });

        return view;
    }
}