package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView button;
    ImageView info;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.orange_light));
        setContentView(R.layout.activity_main);
        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });
        info = (ImageView) findViewById(R.id.button_about);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewAbout();
            }
        });
        editText = (EditText) findViewById(R.id.input);
    }

    public void openNewActivity() {
        Intent intent = new Intent(MainActivity.this, Task.class);
        intent.putExtra("input_text", editText.getText().toString());
        startActivity(intent);
    }

    public void openNewAbout() {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

}