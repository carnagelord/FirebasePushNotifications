package abass.com.firebasepushnotifications;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private TextView mProfileLabel;
    private TextView mUsersLabel;
    private TextView mNotificationsLabel;

    private ViewPager mMainPager;

    private PagerViewAdapter mpagerViewAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser ==null ){
            sendToLogin();
        }
    }

    private void sendToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mProfileLabel = (TextView) findViewById(R.id.profileLabel);
        mNotificationsLabel=(TextView) findViewById(R.id.notificationsLabel);
        mUsersLabel = (TextView) findViewById(R.id.usersLabel);
        mMainPager = (ViewPager) findViewById(R.id.mainPager);

        mpagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mpagerViewAdapter);
        mMainPager.setOffscreenPageLimit(2);

        mProfileLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(0);
            }
        });

        mUsersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(1);
            }
        });

        mNotificationsLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainPager.setCurrentItem(2);
            }
        });


        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageSelected(int position) {
                changeTabs(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    private void changeTabs(int position) {
        if(position ==0){
            mProfileLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabBright));
            mProfileLabel.setTextSize(22);

            mNotificationsLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mNotificationsLabel.setTextSize(16);

            mUsersLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mUsersLabel.setTextSize(16);
        }
        if(position ==2){
            mProfileLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mNotificationsLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabBright));
            mNotificationsLabel.setTextSize(22);

            mUsersLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mUsersLabel.setTextSize(16);
        }
        if(position ==1){
            mProfileLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mProfileLabel.setTextSize(16);

            mNotificationsLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabLight));
            mNotificationsLabel.setTextSize(16);

            mUsersLabel.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textTabBright));
            mUsersLabel.setTextSize(22);
        }
    }
}