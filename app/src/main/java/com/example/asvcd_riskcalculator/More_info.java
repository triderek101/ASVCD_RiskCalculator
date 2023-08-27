import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Your initialization code here
    }

    public void showMoreInformation(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("More Information")
            .setMessage("This is the additional information you want to display.")
            .setPositiveButton("OK", null)
            .show();
    }
    
    // Other methods and code for your activity
}
