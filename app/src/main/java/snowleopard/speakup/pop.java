package snowleopard.speakup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

/**
 * Created by chintandani on 07/10/17.
 */



public class pop extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double width = dm.widthPixels;
        double height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.23));
        yes_report = (Button) findViewById(R.id.button2);
        yes_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pop.this , ViewStoryActivity.class));
            }
        });
    }
    private Button yes_report;
}
