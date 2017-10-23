package snowleopard.speakup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewStoryActivity extends AppCompatActivity {

    private Button report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

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
