package in.nishant.contactlist;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        intent.putExtras(extras);
        this.startActivity( intent );
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
        name = findViewById(R.id.textInputLayout1);
        email = findViewById(R.id.emailInput1);
        phone = findViewById(R.id.phoneInput1);
        Intent mIntent = getIntent();
        if( mIntent == null )
            return;
        Bundle x = mIntent.getExtras();
        if( x != null ) {
            String phoneQuery = x.getString("phone");
            Query q = mUsersList.orderByChild("phone").equalTo(phoneQuery);
            q.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> u =  (HashMap)appleSnapshot.getValue();
                        sname = u.get("name");
                        semail = u.get("email");
                        sphone = u.get("phone");
                        name.setText(sname);
                        email.setText(semail);
                        phone.setText(sphone);
                        user = new User(sname, sphone, semail);
                        Log.d("Nishant", "A " + sname + " " + semail + " " + sphone );
                        break;
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Nishant", "onCancelled", databaseError.toException());
                }
            });
        }
    }
    User user;
    TextInputEditText name, email, phone;
    String sname = "", semail=  "", sphone = "";
    DatabaseReference mUsersList, mDatabase;

}
