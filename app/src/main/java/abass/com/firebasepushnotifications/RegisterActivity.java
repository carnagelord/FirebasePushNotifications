package abass.com.firebasepushnotifications;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullname,email , password ,phone , city , street , nid ;
    DatePicker datePicker;
    int  day  , month , year ;
    private Button mRegBtn , mLoginPageBtn;
    private ProgressBar mregisterprogressbar;
    String Myname , myemail , myPassword , Myphone ,MyCity ,MyStreet ,MyNID , longtitude,latitude ;
    private FirebaseAuth mAuth ;
    private FirebaseFirestore mFirestore;


    LocationManager locationManager;
    private FusedLocationProviderClient client;
    private static GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        datePicker = (DatePicker) findViewById(R.id.DOB);
        fullname = (EditText) findViewById(R.id.fullname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        phone= (EditText) findViewById(R.id.Phone);
        city= (EditText) findViewById(R.id.city);
        street= (EditText) findViewById(R.id.street);
        nid= (EditText) findViewById(R.id.NID);


        client = LocationServices.getFusedLocationProviderClient(RegisterActivity.this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(RegisterActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mRegBtn = (Button) findViewById(R.id.btnRegister);
        mLoginPageBtn = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mregisterprogressbar = (ProgressBar) findViewById(R.id.registerprogressbar);

        mLoginPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLocationServiceEnabled()){
                    if(isNetworkAvailable()){
                        if (ActivityCompat.checkSelfPermission( RegisterActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(RegisterActivity.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        client.getLastLocation().addOnSuccessListener( RegisterActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if(location != null){
                                    longtitude = ""+location.getLongitude();
                                    latitude = ""+location.getLatitude();
                                }
                            }
                        });
                        Register();
                    }else{
                        Toast.makeText(RegisterActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "Location Is Disabled.", Toast.LENGTH_SHORT).show();
                    showSettingDialog();
                }
            }
        });

    }

    private void Register(){
        if(longtitude == null || latitude == null)
        {
            Toast.makeText(RegisterActivity.this,"Something Went Wrong Please Try Again...",Toast.LENGTH_LONG).show();
            return;
        }
        Myname = fullname.getText().toString();
        myemail = email.getText().toString();
        myPassword = password.getText().toString();
        Myphone = phone.getText().toString();
        MyCity = city.getText().toString();
        MyStreet = city.getText().toString();
        MyNID = nid.getText().toString();
        day = datePicker.getDayOfMonth();
        month =(datePicker.getMonth() + 1);
        year =(datePicker.getYear());

        if(ValidateData()){
            mAuth.createUserWithEmailAndPassword(myemail,myPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        ProgressDialog.show(RegisterActivity.this, "Registering", "Please Wait until Registering completes ");
                        mregisterprogressbar.setVisibility(View.VISIBLE);

                        String User_id = mAuth.getCurrentUser().getUid();
                        String Token_id = FirebaseInstanceId.getInstance().getToken();

                        Map<String,Object> userMap= new HashMap<>();
                        userMap.put("name",Myname);
                        userMap.put("email",myemail);
                        userMap.put("phone",Myphone);
                        userMap.put("city",MyCity);
                        userMap.put("street",MyStreet);
                        userMap.put("nid",MyNID);
                        userMap.put("day",day);
                        userMap.put("month",month);
                        userMap.put("year",year);
                        userMap.put("longtitude",longtitude);
                        userMap.put("latitude",latitude);
                        userMap.put("token_id",Token_id);

                        mFirestore.collection("Users").document(User_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                SendToMain();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        mregisterprogressbar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Error : "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
    private boolean ValidateData() {
            if(Myname.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Name ",Toast.LENGTH_SHORT).show();
                return false;
            }else if (myemail.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter an Email ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(myPassword.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter a Password ",Toast.LENGTH_SHORT).show();
                return false;
            }else if(MyNID.equals("")){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter Your National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }else if((MyNID.length() != 14)){
                Toast.makeText(RegisterActivity.this,"Error : You Must Enter A Valid National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }
            int NIDYear  =  Integer.parseInt(MyNID.substring(0,1));
            int NIDBYear =  Integer.parseInt(MyNID.substring(1,3));
            int NIDMonth =  Integer.parseInt(MyNID.substring(3,5));
            int NIDDay   =  Integer.parseInt(MyNID.substring(5,7));
            int City     =  Integer.parseInt(MyNID.substring(7,9));
            if(NIDBYear>=50){
                NIDBYear+=1900;
            }else{
                NIDBYear+=2000;
            }
            if((year>=2000 && NIDYear != 3)  || (year<2000 && NIDYear != 2)  || (year != NIDBYear) || (month != NIDMonth)|| (day != NIDDay) || (City >35 && City != 88) ){
                Toast.makeText(RegisterActivity.this,"Error : Invalid National ID ",Toast.LENGTH_SHORT).show();
                return false;
            }
            if (year>2001){
               Toast.makeText(RegisterActivity.this,"Error : You Are to young to Register ",Toast.LENGTH_SHORT).show();
            return false;
            }

        return true;
    }

    private void SendToMain() {
            Intent intent = new Intent(RegisterActivity.this, HelpRequest.class);
            startActivity(intent);
            finish();
    }
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(RegisterActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        if(isNetworkAvailable()){
                            if (ActivityCompat.checkSelfPermission( RegisterActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(RegisterActivity.this, "Sorry Permission Denied .", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            client.getLastLocation().addOnSuccessListener( RegisterActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if(location != null){
                                        longtitude = ""+location.getLongitude();
                                        latitude = ""+location.getLatitude();
                                    }
                                }
                            });
                            Register();
                        }else{
                            Toast.makeText(RegisterActivity.this, "No Internet.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case RESULT_CANCELED:
                        Toast.makeText(RegisterActivity.this, "Location Disabled ...", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
        }
    }
    public boolean isLocationServiceEnabled(){
        boolean gps_enabled= false;

        if(locationManager ==null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled ;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
