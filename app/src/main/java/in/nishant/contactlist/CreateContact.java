package in.nishant.contactlist;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateContact extends AppCompatActivity {

    Button submit;
    TextInputEditText name, email, phone;
    DatabaseReference mDatabase, mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_contact);
        name = findViewById(R.id.textInputLayout1);
        email = findViewById(R.id.emailInput1);
        phone = findViewById(R.id.phoneInput1);
        submit = findViewById(R.id.submit);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(name.getText().toString(), phone.getText().toString(), email.getText().toString() );
                mUsersList.child(name.getText().toString()).setValue(user);
                Intent intent = new Intent(CreateContact.this, MainActivity.class);
                CreateContact.this.startActivity(intent);
                finish();
            }
        });

    }
}
