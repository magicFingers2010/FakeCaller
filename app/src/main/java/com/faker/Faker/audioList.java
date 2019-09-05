package com.faker.Faker;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.faker.Faker.Adapter.AudioListAdapter;
import com.faker.Faker.model.audio;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class audioList extends Fragment implements AudioListAdapter.NoticeDialogListener {
    private RecyclerView myRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<String> list = new ArrayList<String>();
    private ArrayList<audio> audioList = new ArrayList<audio>();
    private AudioListAdapter my_adapter;
    private static final String TAG = "MyActivity";
    private JSONArray  audioFiles = new JSONArray();
    private ImageButton back;


    public audioList() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_audio_list, container, false);
        back = (ImageButton) mainView.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        myRecyclerView = (RecyclerView) mainView.findViewById(R.id.myRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        myRecyclerView.setLayoutManager(linearLayoutManager);
        getFiles();

        return mainView;
    }

    private void getFiles() {

        try{
            String path = Environment.getExternalStorageDirectory() + File.separator + "Faker/";
            Log.d("Files", path);
            File f = new File(path);
            File[] file = f.listFiles();
            if (String.valueOf(file.length).equals("0")){
            }
            Log.d("Files", String.valueOf(file.length));
            for (File aFile : file) {
                String check_name=String.valueOf(aFile).substring(String.valueOf(aFile).lastIndexOf(".")+1);
                if(check_name.equals("aac")) {

                    list.add(String.valueOf(aFile));
                    JSONObject audioLink = new JSONObject();
                    audioLink.put("name",String.valueOf(aFile));
                    audioLink.put("paused",false);
                    audioFiles.put(audioLink);
                }

            }





        }catch (Exception e) {
            Log.d("Files", e.getMessage());
        }
        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String s = listview.getItemAtPosition(i).toString();

                    try {
                        Intent intent = new Intent(ACTION_VIEW, Uri.parse(s));
                        intent.setDataAndType(Uri.parse(s), "video/mp4");
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.d("Files", e.getMessage());
                    }




                 // If you want to close the adapter
            }
        });
        */

        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i< audioFiles.length(); i++){
            try {
                Log.d(TAG, String.valueOf(audioFiles.get(i).toString()));
                audio audio = mapper.readValue(audioFiles.get(i).toString(),audio.class);
                audioList.add(audio);
                Log.d(TAG, String.valueOf(audioList));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }


        }


        my_adapter= new AudioListAdapter(audioList,getActivity().getApplicationContext(),this);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        myRecyclerView.setAdapter(my_adapter);
    }


    private void returnResult(String audioName) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("audio",audioName);
        getActivity().setResult(Activity.RESULT_OK,returnIntent);
        getActivity().finish();
    }

    @Override
    public void onDialogPositiveClick(String audioName) {
        returnResult(audioName);
    }

    @Override
    public void onDialogNegativeClick(String dialog) {

    }
}
