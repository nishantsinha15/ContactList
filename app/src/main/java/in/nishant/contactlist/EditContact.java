package in.nishant.contactlist;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditContact extends AppCompatActivity {

    TextInputEditText name, email, phone;
    User initialUser;
    DatabaseReference mDatabase, mUsersList;
    private static final int SELECT_PHOTO = 100;
    Uri selectedImage;
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    ImageView imageView;
    int flag = 0;
    String sname = "", semail = "", sphone = "", spicture = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);

        name = findViewById(R.id.textInputLayout1);
        email = findViewById(R.id.emailInput1);
        phone = findViewById(R.id.phoneInput1);
        imageView = (ImageView) findViewById(R.id.imageView2);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        sname = extras.getString("name");
        sphone = extras.getString("phone");
        semail = extras.getString("email");
        spicture = extras.getString("picture");
        initialUser = new User(sname, sphone , semail, spicture );

        name.setText(sname);
        email.setText(semail);
        phone.setText(sphone);
        if( spicture.equals("true") )
        {
            //todo Download image
            // Reference to an image file in Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference img = storageReference.child("Users/"+sphone);

            // Load the image using Glide
            Glide.with(EditContact.this)
                    .using(new FirebaseImageLoader())
                    .load(img)
                    .into(imageView);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public void selectImage(View view) {
        Log.d("TAG", "selectImage()");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Log.d("TAG", "onActivityResult()");
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    flag = 1;
                    Toast.makeText(EditContact.this,"Image selected, click on upload button",Toast.LENGTH_SHORT).show();
                    selectedImage = imageReturnedIntent.getData();
                }
        }
    }

    public void uploadImage() {
        Log.d("TAG", "uploadImage()");
        imageRef = storageRef.child("Users/"+phone.getText().toString());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        uploadTask = imageRef.putFile(selectedImage);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EditContact.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString(), "true" );
                mUsersList.child(phone.getText().toString()).setValue(user);
                Toast.makeText(EditContact.this,"Upload successful",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Picasso.with(EditContact.this).load(downloadUrl).into(imageView);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void deleteContact( View view )
    {
        Query q = mUsersList.orderByChild("phone").equalTo(initialUser.phone);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Nishant", "onCancelled", databaseError.toException());
            }
        });
        Toast.makeText(EditContact.this,"Deletion successful",Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void saveChanges( View view )
    {
        if( phone.getText().toString().equals("") )
        {
            Toast.makeText(EditContact.this,"Phone Number cannot be empty",Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Log.d("Nishant", "Clicked submit");
            if (!sphone.equals(phone.getText().toString())) {
                Query q = mUsersList.orderByChild("phone").equalTo(sphone);
                q.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Nishant", "onCancelled", databaseError.toException());
                    }
                });
            }

            if (flag == 1)
                uploadImage();
            else {
                User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString(), "false");
                mUsersList.child(phone.getText().toString()).setValue(user);
                //todo If number changed, change the number of the photo too
                Toast.makeText(EditContact.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
