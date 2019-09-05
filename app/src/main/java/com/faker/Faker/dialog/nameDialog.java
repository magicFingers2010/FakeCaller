package com.faker.Faker.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.faker.Faker.R;

public class nameDialog extends DialogFragment {

    private String name;
    private EditText audioName;
    View mainLayout;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String dialog);
        public void onDialogNegativeClick(String dialog);
    }

    NoticeDialogListener mListener;

    public nameDialog(String name) {
        this.name = name;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            Log.d("Files",e.getMessage());
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Set the dialog title
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
         mainLayout = inflater.inflate(R.layout.input_inflater,null);
            audioName = (EditText) mainLayout.findViewById(R.id.audio_name);
            audioName.setText(name);

        builder.setTitle("Save Audio")
                .setView(mainLayout)
                .setPositiveButton("save",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }



}
