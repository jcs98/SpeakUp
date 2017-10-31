package snowleopard.speakup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewStoryActivity extends AppCompatActivity {

    private Button report;
    private TextView mTitle;
    private TextView mDescription;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabase;

    private TextView mNumLikes;
    private ImageView mImage;
    private DatabaseReference mStory;
    private ImageButton mLike;
    private String key;
    private FirebaseAuth mAuth;

    private boolean mProcessLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        key = getIntent().getStringExtra("Key");
        mStory = FirebaseDatabase.getInstance().getReference("Story").child(key);
        mTitle = (TextView) findViewById(R.id.titleView);
        mDescription = (TextView) findViewById(R.id.descView);
        mNumLikes = (TextView) findViewById(R.id.mNLikes);
        mImage = (ImageView) findViewById(R.id.imageView);
        mLike = (ImageButton) findViewById(R.id.mLike);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProcessLike = true;
                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessLike) {
                            int number=0;
                            if(dataSnapshot.child(key).hasChild("NumberOfLikes"))
                            {
                                number = Integer.parseInt(dataSnapshot.child(key).child("NumberOfLikes").getValue().toString());
                            }

                            if (dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDatabaseLike.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProcessLike = false;
                                number--;

                            } else {
                                mDatabaseLike.child(key).child(mAuth.getCurrentUser().getUid()).setValue("Liked");
                                number++;
                                mProcessLike = false;
                            }
                            mDatabaseLike.child(key).child("NumberOfLikes").setValue(number);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        });
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(key).hasChild("NumberOfLikes")){
                    mNumLikes.setText(dataSnapshot.child(key).child("NumberOfLikes").getValue().toString());}

                if(dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())){
                    mLike.setImageResource(R.mipmap.likegray);

                }
                else{
                    mLike.setImageResource(R.mipmap.likeblack);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mStory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTitle.setText(dataSnapshot.child("Title").getValue().toString());
                mDescription.setText(dataSnapshot.child("Description").getValue().toString());
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("ImageUrl").getValue().toString()).into(mImage);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        report =(Button) findViewById(R.id.button3);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent=new Intent(ViewStoryActivity.this,pop.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
        });
    }


}
