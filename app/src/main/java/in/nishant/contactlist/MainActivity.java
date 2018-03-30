package in.nishant.contactlist;

//TODO 1. Add Image
//TODO 2. Edit/Delete Contact


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    final String TAG = "TAG";
    private int iter = 10000;
    DatabaseReference mDatabase, mUsersList;
    ArrayList<User> db = new ArrayList<User>();
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDatabase();
        setRecyclerView();
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked");
                Intent intent = new Intent( MainActivity.this, CreateContact.class );
                MainActivity.this.startActivity(intent);
            }
        });
    }

    public void setDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        ValueEventListener contactListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                // ...
                Iterable<DataSnapshot> dataSnapshotIter = dataSnapshot.getChildren();
                db = new ArrayList<User>();
                for( DataSnapshot w : dataSnapshotIter )
                    db.add( w.getValue(User.class) );
                mAdapter = new MyAdapter(db);
                recyclerView.swapAdapter(mAdapter, false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mUsersList.addValueEventListener(contactListener);
    }

    public void setRecyclerView()
    {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyAdapter(db);
        recyclerView.setAdapter(mAdapter);
    }
}

//public class MainActivity extends AppCompatActivity {
//
//    DatabaseReference mDatabase, mTest;
//    private final String TAG = "tag";
//    private Button submit;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mTest = mDatabase.child("Users");
//        setContentView(R.layout.activity_main);
//
//        submit = findViewById(R.id.button);
//        submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String name = "User 1";
//                User u = new User( "Nishant", "1", "n@sinha" );
//                mDatabase.child(name).setValue(u);
//            }
//        });
//    }
//}

