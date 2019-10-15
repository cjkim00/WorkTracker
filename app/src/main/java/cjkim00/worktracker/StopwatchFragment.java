package cjkim00.worktracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment {

    Chronometer chronometer = new Chronometer(this.getContext());
    TextView textView;
    public StopwatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        textView = (TextView) v.findViewById(R.id.time);

        final Button b = (Button) v.findViewById(R.id.start_button);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chronometer.start();
                b.setVisibility(View.INVISIBLE);
            }
        });


        return v;
    }

}
