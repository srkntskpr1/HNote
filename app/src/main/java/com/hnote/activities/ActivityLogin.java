package com.hnote.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hnote.R;
import com.huawei.hmf.tasks.OnCompleteListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
public class ActivityLogin extends AppCompatActivity {
    TextView loginBtn;
    AuthHuaweiId huaweiAccount;
    private boolean isLogged(){
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setIdToken().createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(ActivityLogin.this, authParams);
        if(huaweiAccount == null)
        {
            Log.i("TAG", "Starting signIn intent");
            startActivityForResult(service.getSignInIntent(), 1123);
            return false;
        }
        else
        {
            Task<Void> signOutTask = service.signOut();
            signOutTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    Log.i("TAG", "signOut complete");
                    huaweiAccount = null;

                }
            });
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLogged();
        super.onCreate(savedInstanceState);
        TextView txttitle = findViewById(R.id.LoginTitle);
        LinearLayout layoutgiris = findViewById(R.id.layoutGiris);
        LinearLayout copyright = findViewById(R.id.copyright);
        /*if(){
            View view = this.getWindow().getDecorView();
            view.setBackgroundColor(getResources().getColor(R.color.colorSearchBG));
            txttitle.setVisibility(View.INVISIBLE);
            layoutgiris.setVisibility(View.INVISIBLE);
            copyright.setVisibility(View.INVISIBLE);
        }*/
        setContentView(R.layout.activity_login);
        loginBtn = findViewById(R.id.btn_login);



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLogged();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Process the authorization result to obtain an ID token from AuthHuaweiId.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1123)
        {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful())
            {
                // The sign-in is successful, and the user's HUAWEI ID information and ID token are obtained.
                huaweiAccount = authHuaweiIdTask.getResult();
                Intent main_intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(main_intent,3);
            } else {
                // The sign-in failed. No processing is required. Logs are recorded to facilitate fault locating.
                Log.e("TAG", "sign in failed : " +((ApiException)authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }
}