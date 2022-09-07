package in.co.fastride.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import in.co.fastride.R;
import in.co.fastride.RoutMapFragment;

public class HomeFragment extends Fragment {
Button btngo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

btngo = view.findViewById(R.id.btn_bottom);

        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                RoutMapFragment fragment = new RoutMapFragment();
                fm.beginTransaction().replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack("HomeFragment")
                        .commit();
            }
        });
        return view;
    }

}