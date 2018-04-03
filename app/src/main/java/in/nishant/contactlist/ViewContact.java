package in.nishant.contactlist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ViewContact extends AppCompatActivity {

    public void sendMail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        String mailAddress = "mailto: " + email.getText().toString();
        emailIntent.setData(Uri.parse(mailAddress));
        startActivity(Intent.createChooser(emailIntent, "Send Mail"));
    }

    public void makeCall() {
        Log.d("Nishant", "Calling");
        String number = ("tel:" + phone.getText().toString());
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(number));
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                startActivity(intent);
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 123);
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            startActivity(intent);
        }
    }




    public void editContact( View view ){
        Intent intent = new Intent( ViewContact.this, EditContact.class );
        //Send the User Data
        Bundle extras = new Bundle();
        extras.putString("name",user.name);
        extras.putString("phone",user.phone);
        extras.putString("email", user.email);
        extras.putString("picture", user.picture);
        intent.putExtras(extras);
        Log.d("Nishant", "Sending to editing " + sname + " " + semail + " " + sphone );
        this.startActivity( intent );
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CustomTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        setUpOldData();
        TextView phoneTextView = (TextView) findViewById(R.id.phone);
        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });
    }

    public void setUpOldData()
    {
        imageView = findViewById(R.id.imageView2);
        name = findViewById(R.id.textInputLayout1);
        email = findViewById(R.id.emailInput1);
        phone = findViewById(R.id.phoneInput1);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        sname = extras.getString("name");
        sphone = extras.getString("phone");
        semail = extras.getString("email");
        spicture = extras.getString("picture");

        name.setText(sname);
        email.setText(semail);
        phone.setText(sphone);
        user = new User(sname, sphone, semail, spicture);
        if( spicture.equals("true") )
        {
            //todo Downlaod image
            // Reference to an image file in Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference img = storageReference.child("Users/"+sphone);

            // Load the image using Glide
            Glide.with(ViewContact.this)
                    .using(new FirebaseImageLoader())
                    .load(img)
                    .into(imageView);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    makeCall();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    User user;
    TextInputEditText name, email, phone;
    String sname = "", semail=  "", sphone = "";
    String spicture = "false";
    ImageView imageView;
    DatabaseReference mUsersList, mDatabase;

}
