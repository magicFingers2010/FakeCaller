package com.faker.Faker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BackgroundFragment  extends Fragment {
        private View backGroundFragment;
        LinearLayout backgroundTheme;
    LinearLayout callVoice;
        TextView backgroundValue;
    AssetFileDescriptor afd;
    TextView audioSet;
    LinearLayout privacy;


    final static String PREF_FILE_1 = "pref_file_1";



    public BackgroundFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        backGroundFragment = inflater.inflate(R.layout.backgroun_fragment, container, false);

        privacy = (LinearLayout) backGroundFragment.findViewById(R.id.privacy);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyPage = new Intent(getActivity(),privacy.class);
                startActivity(privacyPage);
            }
        });
        backgroundTheme = (LinearLayout) backGroundFragment.findViewById(R.id.background);
        callVoice = (LinearLayout) backGroundFragment.findViewById(R.id.callVoice);
        backgroundValue = (TextView) backGroundFragment.findViewById(R.id.backgroundValue);
        audioSet = (TextView) backGroundFragment.findViewById(R.id.audioSet);

        Toolbar toolbar = (Toolbar) backGroundFragment.findViewById(R.id.toolbar);


        backgroundTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseBackground();
            }
        });
        callVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseAudio();
            }
        });
        SharedPreferences sharedPref = getActivity().getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        String defaultValue = "1";
        String highScore = sharedPref.getString("getBackground", defaultValue);

        String audioFile = sharedPref.getString("getAudio","error");

        if (audioFile.equals("error")){
            audioSet.setText("hello.m4a");
        }else{
           audioSet.setText(audioFile);
        }
        switch (highScore){
            case "1":
                backgroundValue.setText("Theme 1");
                break;
            case "2":
                backgroundValue.setText("Theme 2");
                break;
            case "3":
                backgroundValue.setText("Theme 3");
                break;
        }
        return backGroundFragment;

    }

    private void chooseBackground() {
        Intent i = new Intent(getActivity(), choose_backg.class);
        startActivityForResult(i, 1);
    }
    private void chooseAudio(){
        Intent i = new Intent(getActivity(), audioSettings.class);
        startActivityForResult(i, 2);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        SharedPreferences sharedPref = getActivity().getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                switch (result){
                    case "1":
                        backgroundValue.setText("Theme 1");
                        editor.putString("getBackground", "1");
                        editor.apply();
                        break;
                    case "2":
                        backgroundValue.setText("Theme 2");
                        editor.putString("getBackground", "2");
                        editor.apply();
                        break;
                    case "3":
                        backgroundValue.setText("Theme 3");
                        editor.putString("getBackground", "3");
                        editor.apply();
                        break;
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
        if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("audio");
                audioSet.setText(result);
                editor.putString("getAudio",result);
                editor.apply();
            }
        }
    }//
}
