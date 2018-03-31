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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String name = extras.getString("name");
        String phone = extras.getString("phone");
        String email = extras.getString("email");
        initialUser = new User(name, phone , email );

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        imageView = (ImageView) findViewById(R.id.imageView2);
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
                User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString() );
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
        Log.d("Nishant", "Clicked submit");
        if( flag == 1 )
            uploadImage();
        else
        {
            User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString() );
            mUsersList.child(phone.getText().toString()).setValue(user);
            Toast.makeText(EditContact.this,"Upload successful",Toast.LENGTH_SHORT).show();
        }
    }

}
