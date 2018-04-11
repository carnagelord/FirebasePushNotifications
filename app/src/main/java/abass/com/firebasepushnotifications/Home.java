package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import abass.com.firebasepushnotifications.Maps.MainMap;
import abass.com.firebasepushnotifications.Request.HelpRequest;
import abass.com.firebasepushnotifications.Request.MainActivity;

public class Home extends AppCompatActivity {

    public Button Help_Request_BTN;
    public Button Blood_Donor_BTN;
    public Button SOS_BTN;
    public Button Places_BTN;
    public Button First_Aid_BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Help_Request_BTN = (Button) findViewById(R.id.help_request_Btn);
        Blood_Donor_BTN = (Button) findViewById(R.id.Blood_BTN);
        SOS_BTN =  (Button) findViewById(R.id.SOS_BTN);
        Places_BTN = (Button) findViewById(R.id.Places_BTN);
        First_Aid_BTN = (Button) findViewById(R.id.First_Aid_BTN);

        Help_Request_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent HelpIntent = new Intent(Home.this, HelpRequest.class);
                startActivity(HelpIntent);
            }
        });
        Blood_Donor_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Go To Blood Donor.", Toast.LENGTH_SHORT).show();
            }
        });
        SOS_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Go To SOS .", Toast.LENGTH_SHORT).show();
            }
        });
        Places_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MapsIntnet = new Intent(Home.this, MainMap.class);
                startActivity(MapsIntnet);
            }
        });
        First_Aid_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Go To First Aid.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}