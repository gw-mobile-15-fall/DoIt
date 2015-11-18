package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by omar on 10/27/2015.
 */
public class SettingActivity extends Activity{
    Button camButton,applyButton,upload,Browse,historyButton;
    EditText name,bio;
    ImageView mImageView;
    Bitmap imageBitmap;
    EditText nameInput,bioInput;
    private int PICK_IMAGE_REQUEST = 2;
    private int CAM_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        name = (EditText)findViewById(R.id.memberName);
        bio = (EditText)findViewById(R.id.bioText);
        mImageView  = (ImageView) findViewById(R.id.imageView2);
        historyButton =  (Button) findViewById(R.id.historyButton);
        name.setText(ParseUser.getCurrentUser().get("name").toString());
        bio.setText(ParseUser.getCurrentUser().get("bio").toString());
        ParseFile p = (ParseFile) ParseUser.getCurrentUser().getParseFile("image");
        byte[] b = null;
        try {
            b = p.getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(b != null ){
            mImageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
        }
        Browse = (Button) findViewById(R.id.Browse);
        camButton = (Button) findViewById(R.id.camButton);
        applyButton = (Button) findViewById(R.id.applyButton);


        upload= (Button) findViewById(R.id.upload);
        if ( ParseUser.getCurrentUser().getList("areas").size() ==0 )
            upload.setVisibility(View.GONE);


        camButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAM_IMAGE_REQUEST);

            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(SettingActivity.this,ExploreActivity.class);
                intent.putExtra("type","history");
                startActivityForResult(intent, 1);

            }
        });
        Browse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent();
                 intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                 startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);


            }
        });





        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(SettingActivity.this,UploadActivity.class);
                startActivity(intent);

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
                      imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
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

        if (requestCode == CAM_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(imageBitmap);
               // mImageView.setRotation(90);
                mImageView.setVisibility(View.VISIBLE);

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));


                mImageView.setImageBitmap(imageBitmap);
                mImageView.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }
}
