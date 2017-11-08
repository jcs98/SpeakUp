package snowleopard.speakup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chintandani on 08/11/17.
 */

public class PopDelete extends Activity {
    private String key;
    private DatabaseReference mDatabase;
    private Button yes_delete;
    private Button cancel_delete;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_delete);
        key = getIntent().getStringExtra("Key");
        mDatabase = FirebaseDatabase.getInstance().getReference("Story");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels;
        double height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.23));

        cancel_delete = (Button) findViewById(R.id.cancel_button_delete_story);
        cancel_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PopDelete.this, ProfileActivity.class));
                finish();
            }
        });

        yes_delete = (Button) findViewById(R.id.del_final_story);
        yes_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(key).removeValue();
                startActivity(new Intent(PopDelete.this , ProfileActivity.class));
                finish();
            }
        });
    }
}
