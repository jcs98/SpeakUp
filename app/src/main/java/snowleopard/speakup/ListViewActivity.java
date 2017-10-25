package snowleopard.speakup;


import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ListViewActivity extends AppCompatActivity {

    private RecyclerView mList;
    private DatabaseReference mDatabase;
    private FloatingActionButton mAddNS;
    private DatabaseReference mDatabaseUsers;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(R.layout.activity_list_view);
        mList = (RecyclerView) findViewById(R.id.list);
        //mList.hasFixedSize(true);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mAddNS = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        mAddNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ListViewActivity.this, MapActivity.class);

                startActivity(mainIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Cards_ListViewActivity , cardViewHolder> firebaserecycleradapter = new FirebaseRecyclerAdapter<Cards_ListViewActivity, cardViewHolder>(
                Cards_ListViewActivity.class,
                R.layout.list_row,
                cardViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(cardViewHolder viewHolder, Cards_ListViewActivity model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setOwner(model.getOwner());
                viewHolder.setImageUrl(getApplicationContext(),model.getImageURL());

            }
        };
        mList.setAdapter(firebaserecycleradapter);


    }

    private void checkUserExist() {

        final String user_id = mAuth.getCurrentUser().getUid();
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(user_id)) {

                    Intent mainIntent = new Intent(ListViewActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                    //Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                    finish();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    public static class cardViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public cardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.tvTitle);
            post_title.setText(title);

        }

        public void setDescription(String description) {
            TextView post_desc = (TextView) mView.findViewById(R.id.tvDesc);
            post_desc.setText(description);
        }
        public void setOwner(String owner){
            TextView post_owner = (TextView) mView.findViewById(R.id.tvOwner);
            post_owner.setText(owner); }

        public void setImageUrl(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.imgCard);
            Picasso.with(ctx).load(image).into(post_image);

        }
    }
}
