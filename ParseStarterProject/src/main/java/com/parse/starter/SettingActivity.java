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


public class SettingActivity extends Activity {
    Button mCamButton, mApplyButton, mUpload, mBrowse, mHistoryButton, mYourChannels;
    EditText mName, mBio;
    ImageView mImageView;
    Bitmap mImageBitmap;
     private int PICK_IMAGE_REQUEST = 2;
    private int CAM_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        mName = (EditText) findViewById(R.id.memberName);
        mBio = (EditText) findViewById(R.id.bioText);
        mImageView = (ImageView) findViewById(R.id.imageView2);
        mHistoryButton = (Button) findViewById(R.id.historyButton);
        mYourChannels = (Button) findViewById(R.id.yourChannels);
      //  mImageView.setPadding(2,2,2,2);
       // mImageView.setBackgroundColor(Color.BLACK);

        mName.setText(ParseUser.getCurrentUser().get("name").toString()); // write the user's name
        mBio.setText(ParseUser.getCurrentUser().get("bio").toString());
        ParseFile p = (ParseFile) ParseUser.getCurrentUser().getParseFile("image");
        byte[] b = null;
        try {
            b = p.getData(); // get avatar
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (b != null) {// draw the avatar
            mImageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));

        }


        mBrowse = (Button) findViewById(R.id.Browse);
        mCamButton = (Button) findViewById(R.id.camButton);
        mApplyButton = (Button) findViewById(R.id.applyButton);


        mUpload = (Button) findViewById(R.id.upload);
        if (ParseUser.getCurrentUser().getList("areas") == null) // if the user cannot post goals, disable the button
            mUpload.setVisibility(View.GONE);


        mCamButton.setOnClickListener(new View.OnClickListener() { // open the camera to take pic
            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra("android.intent.extras.CAMERA_FACING", 1);

                startActivityForResult(intent, CAM_IMAGE_REQUEST);

            }
        });

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { // start graph
                // Logout current user
                Intent intent = new Intent(SettingActivity.this, GraphActivity.class);
                intent.putExtra("type", "history");
                startActivityForResult(intent, 1);

            }
        });
        mYourChannels.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { // start subscribe to channels
                // Logout current user
                Intent intent = new Intent(SettingActivity.this, subscribeActivity.class);

                startActivityForResult(intent, 1);

            }
        });

        mBrowse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { // open th gallery to pick pic
                // Logout current user
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.Select_Picture)), PICK_IMAGE_REQUEST);


            }
        });


        mUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { // upload goal to db if has permission!
                Intent intent = new Intent(SettingActivity.this, UploadActivity.class);
                startActivity(intent);

            }
        });
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) { // user pressed 'apply' change
                Intent returnIntent = new Intent();
                ParseUser user = ParseUser.getCurrentUser();

                if (SettingActivity.this.mName.getText().toString() != null) { // if name not null
                    user.put("name", SettingActivity.this.mName.getText().toString()); // save the new name in db
                    returnIntent.putExtra("name", SettingActivity.this.mName.getText().toString());// send the name caller activity
                }
                if (SettingActivity.this.mBio.getText().toString() != null) {
                    user.put("bio", SettingActivity.this.mBio.getText().toString()); // save the new bio in db
                    returnIntent.putExtra("bio", SettingActivity.this.mBio.getText().toString());// send bio caller activity

                }
                if (mImageBitmap != null) { // if there is avatar
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); // convert the pic to bitmap
                    byte[] image = stream.toByteArray();
                    ParseFile file = new ParseFile("photo.png", image);
                    user.put("image", file); // save pic to db
                    returnIntent.putExtra("image", mImageBitmap); // send pic to caller

                }
                user.saveInBackground(); // save all changes to db


                setResult(Activity.RESULT_OK, returnIntent);
                finish();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAM_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) { // user took a pic
            if (resultCode == Activity.RESULT_OK) { // user took a pic
                Bundle extras = data.getExtras();
                mImageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(mImageBitmap); // refelct the change to imageview

                mImageView.setVisibility(View.VISIBLE); // update the image view with new pic
             }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            // user cancel the pic
        }


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) { // user choose pic from gallery

            Uri uri = data.getData();

            try {
                mImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                mImageView.setImageBitmap(mImageBitmap); // refelct the change to imageview
                mImageView.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }

    private Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
