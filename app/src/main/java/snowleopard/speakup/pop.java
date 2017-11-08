package snowleopard.speakup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by chintandani on 07/10/17.
 */



public class pop extends Activity {
    private boolean mProcessReport=false;
    private String key;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseReport;
    private Button yes_report;
    private Button mCancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseReport=FirebaseDatabase.getInstance().getReference().child("Reports");
        mAuth = FirebaseAuth.getInstance();
        key = getIntent().getStringExtra("Key");
        mCancel = (Button) findViewById(R.id.reportC);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseReport.keepSynced(true);
        double width = dm.widthPixels;
        double height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8),(int)(height*.23));
        yes_report = (Button) findViewById(R.id.reportY);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent back = new Intent(getApplicationContext(),ViewStoryActivity.class);
                back.putExtra("Key",key);
                startActivity(back);*/
                finish();
            }
        });

        yes_report.setOnClickListener(new View.OnClickListener() {
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

                            }
                            finish();


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        });

    }
}
