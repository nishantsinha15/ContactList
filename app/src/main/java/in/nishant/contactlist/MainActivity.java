package in.nishant.contactlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase, mTest;
    private final String TAG = "tag";
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTest = mDatabase.child("Users");
        setContentView(R.layout.activity_main);

        submit = findViewById(R.id.button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "User 1";
                User u = new User( "Nishant", "1", "n@sinha" );
                mDatabase.child(name).setValue(u);
            }
        });
    }
}
