package com.mrex.ncs;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    private class SessionCallback implements ISessionCallback {
        //로그인 성공
        @Override
        public void onSessionOpened() {
            requestMe();
        }

        //로그인 실패
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        public void requestMe() {
            //사용자 정보 요청 결과에 대한 Callback
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSuccess(MeV2Response result) {
                    Log.e("SessionCallback:", "onSuccess");

                    String results = result.toString();
                    String kakaoAccount = result.getKakaoAccount().toString();
                    long id = result.getId();

                    Log.e("kakaoAccount:", kakaoAccount);
                    Log.e("id:", id + "");
                    Log.e("results:", results);

                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("SessionCallback:", "onSessionClosed:" + errorResult.getErrorMessage());
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    super.onFailure(errorResult);
                    Log.e("SessionCallback:", "onFailure:" + errorResult.getErrorMessage());
                }
            });
        }
    }
}
