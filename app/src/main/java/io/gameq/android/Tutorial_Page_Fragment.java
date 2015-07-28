package io.gameq.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tutorial_Page_Fragment extends Fragment {

    private int pageNum = 0;
    private Activity mTutActivity;

    public Tutorial_Page_Fragment() {

        // Required empty public constructor
    }

    public void setArguments(int page, Activity act) {
        pageNum = page;
        mTutActivity = act;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView;
        if (pageNum == 0) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_tutorial_1, container, false);
        } else if (pageNum == 1) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_tutorial_2, container, false);
        } else if (pageNum == 2) {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_tutorial_3, container, false);
        } else {
            rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_tutorial_4, container, false);
            Button doneButton = (Button) rootView.findViewById(R.id.tut_finished_button);
            doneButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pressedDone();
                }
            });
        }


        return rootView;
    }

    public void pressedDone() {
        ConnectionHandler.saveFirstLoginDone();
        Intent intent = new Intent(mTutActivity, MainActivity.class);
        intent.putExtra(getString(R.string.intent_inhouse_extra), true);
        startActivity(intent);
    }


}
