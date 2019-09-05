package com.faker.Faker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class choose_backg extends AppCompatActivity {
    ImageView firstImage;
    ImageView secondImage;
    ImageView thirdImage;
    ImageView imageCont;
    ImageButton choose;
    ImageButton back;
    String choosen = "1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_backg);
        choose = (ImageButton) findViewById(R.id.choose);


        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });


        firstImage = (ImageView) findViewById(R.id.firstImage);
        secondImage = (ImageView) findViewById(R.id.secondImage);
        thirdImage = (ImageView) findViewById(R.id.thirdImage);
        imageCont = (ImageView) findViewById(R.id.imageCont);

        firstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCont.setImageResource(R.drawable.background_1);
                choosen = "1";

            }
        });
        secondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCont.setImageResource(R.drawable.background_2);
                choosen = "2";
            }
        });
        thirdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCont.setImageResource(R.drawable.three);
                choosen ="3";
            }
        });
    }

    private void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",choosen);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
