package  hocc.fun.forget;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class PermissionRequest extends AppCompatActivity {

    TextView Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_request);
        Button = findViewById(R.id.button);
        Button.setOnClickListener(view -> {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(PermissionRequest.this, MainActivity.class);
            startActivity(intent);
        }
    }
}