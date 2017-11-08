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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class ViewStoryActivity extends AppCompatActivity {

    private Button report;
    private TextView mTitle;
    private TextView mDescription;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseReport;

    private DatabaseReference mDatabase;
    private Button mReport;
    private TextView mNumLikes;
    private ImageView mImage;
    private DatabaseReference mStory;
    private ImageButton mLike;
    private String key;
    private FirebaseAuth mAuth;
    private Button mLoc;

    private boolean mProcessLike=false;
    private boolean mProcessReport=false;


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
        mReport = (Button) findViewById(R.id.report);
        mLoc = (Button) findViewById(R.id.mLoc);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseReport=FirebaseDatabase.getInstance().getReference().child("Reports");

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseReport.keepSynced(true);
        mLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(ViewStoryActivity.this,MappingActivity.class);
                        intent.putExtra("long",dataSnapshot.child(key).child("Longitude").getValue().toString());
                        intent.putExtra("lat",dataSnapshot.child(key).child("Latitude").getValue().toString());
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });







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
        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProcessReport = true;
                mDatabaseReport.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (mProcessReport) {
                            int number=0;
                            if(dataSnapshot.child(key).hasChild("NumberOfReports"))
                            {
                                number = Integer.parseInt(dataSnapshot.child(key).child("NumberOfReports").getValue().toString());
                            }

                            if (dataSnapshot.child(key).hasChild(mAuth.getCurrentUser().getUid())) {
                                mDatabaseReport.child(key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                mProcessReport = false;
                                number--;

                            } else {
                                mDatabaseReport.child(key).child(mAuth.getCurrentUser().getUid()).setValue("Reported");
                                number++;
                                mProcessReport = false;
                            }
                            mDatabaseReport.child(key).child("NumberOfReports").setValue(number);
                            if(number == 2){
                                mDatabaseReport.child(key).removeValue();
                                mDatabaseLike.child(key).removeValue();
                                mDatabase.child(key).removeValue();
                                Intent viewStories = new Intent(getApplicationContext(),ListViewActivity.class);
                                startActivity(viewStories);
                                finish();


                            }

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
                    mLike.setImageResource(R.drawable.likedfi);

                }
                else{
                    mLike.setImageResource(R.drawable.unliked);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(key)) {
                    mTitle.setText(dataSnapshot.child(key).child("Title").getValue().toString());
                    mDescription.setText(dataSnapshot.child(key).child("Description").getValue().toString());
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child(key).child("ImageUrl").getValue().toString()).networkPolicy(NetworkPolicy.OFFLINE).into(mImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getApplicationContext()).load(dataSnapshot.child(key).child("ImageUrl").getValue().toString()).into(mImage);

                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent=new Intent(ViewStoryActivity.this,pop.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mainIntent.putExtra("Key",key);
                startActivity(mainIntent);
            }
        });
    }


}
