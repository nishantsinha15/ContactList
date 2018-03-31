package in.nishant.contactlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    DatabaseReference mDatabase, mUsersList;
    ArrayList<User> db = new ArrayList<User>();
    ImageButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDatabase();
        setRecyclerView();
    }

    public void createContact( View view )
    {
        Log.d("Nishant", "Clicked create Contact");
        Intent intent = new Intent( MainActivity.this, CreateContact.class );
        MainActivity.this.startActivity(intent);
    }

    public void setDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersList = mDatabase.child("Users");
        ValueEventListener contactListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Nishant", "Data Changed");
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
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                String phoneNumber = db.get(position).phone;
                Intent intent = new Intent(MainActivity.this, ViewContact.class);
                intent.putExtra("phone", phoneNumber);
                Log.d("Nishant", phoneNumber );
                startActivity(intent);
            }
        });
    }

    public void viewContact( View view )
    {
        Log.d("Nishant", "Clicked edit Contact");
        Intent intent = new Intent( MainActivity.this, ViewContact.class );
//        MainActivity.this.startActivity( intent );
        TextView t = findViewById(R.id.phone);
//        Toast.makeText(MainActivity.this,t.getText().toString(),Toast.LENGTH_SHORT).show();
    }


}
