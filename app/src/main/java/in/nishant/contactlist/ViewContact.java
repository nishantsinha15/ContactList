package in.nishant.contactlist;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    public void sendMail( View view ){}

    public void makeCall( View view ){}

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_contact);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        setUpOldData();
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
