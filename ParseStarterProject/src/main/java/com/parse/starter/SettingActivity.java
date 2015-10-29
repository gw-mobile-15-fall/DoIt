package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

/**
 * Created by omar on 10/27/2015.
 */
public class SettingActivity extends Activity{
    Button camButton,applyButton;
    EditText name,bio;
    ImageView mImageView;
    Bitmap imageBitmap;
    EditText nameInput,bioInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        name = (EditText)findViewById(R.id.memberName);
        bio = (EditText)findViewById(R.id.bioText);
        name.setText(ParseUser.getCurrentUser().get("name").toString());
        bio.setText(ParseUser.getCurrentUser().get("bio").toString());


        camButton = (Button) findViewById(R.id.camButton);
        applyButton = (Button) findViewById(R.id.applyButton);


        mImageView  = (ImageView) findViewById(R.id.imageView2);

        camButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);

            }
        });
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent returnIntent = new Intent();
                ParseUser user = ParseUser.getCurrentUser();
                if(SettingActivity.this.name.getText().toString() != null)
                {
                    user.put("name", SettingActivity.this.name.getText().toString());
                    returnIntent.putExtra("name", SettingActivity.this.name.getText().toString());
                }
                if(SettingActivity.this.bio.getText().toString() != null) {
                    user.put("bio", SettingActivity.this.bio.getText().toString());
                    returnIntent.putExtra("bio", SettingActivity.this.bio.getText().toString());

                }
                if(imageBitmap != null)
                  {
                      ByteArrayOutputStream stream = new ByteArrayOutputStream();
                      imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                      byte[] image = stream.toByteArray();
                      ParseFile file = new ParseFile("photo.png", image);
                      user.put("image",file);
                      returnIntent.putExtra("image", imageBitmap);

                  }
                user.saveInBackground();


                setResult(Activity.RESULT_OK,returnIntent);
                finish();



            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                  imageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(imageBitmap);
                mImageView.setRotation(90);
                mImageView.setVisibility(View.VISIBLE);

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
    }
}
