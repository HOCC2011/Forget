package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class About extends AppCompatActivity {

    ImageView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.orange_light));
        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
    }

    public void openNewActivity() {
        Intent intent = new Intent(About.this, MainActivity.class);
        startActivity(intent);
    }
}