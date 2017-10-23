package snowleopard.speakup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddStoryActivity extends AppCompatActivity {

    private EditText mTitle;
    private EditText mDesc;

    private Button mSubmit;

    private ImageButton mImg;

    private Uri mImgU = null;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUser;
    private StorageReference mStorage;
    public static final int GALLERY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        mTitle = (EditText)findViewById(R.id.etTitle);
        mDesc = (EditText)findViewById(R.id.etDesc);

        mSubmit= (Button) findViewById(R.id.btSub);

        mImg = (ImageButton) findViewById(R.id.btImg);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Story");
        mStorage  = FirebaseStorage.getInstance().getReference().child("Photos");

        mProgress = new ProgressDialog(this);


        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    public void startPosting() {
        mProgress.setMessage("Is Uploading");
        mProgress.show();
        final String title = mTitle.getText().toString().trim();
        final String desc = mDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc))
        {
            StorageReference filepath = mStorage.child(mImgU.getLastPathSegment());
            filepath.putFile(mImgU).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri DownloadUrl = taskSnapshot.getDownloadUrl();
                    final DatabaseReference newpost = mDatabase.push();
                    newpost.child("Title").setValue(title);
                    newpost.child("Description").setValue(desc);
                    newpost.child("ImageUrl").setValue(DownloadUrl.toString().trim());
                    mProgress.dismiss();


                    Intent mainIntent = new Intent(AddStoryActivity.this, ListViewActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            mImgU = data.getData();
            mImg.setImageURI(mImgU);
        }
    }
}