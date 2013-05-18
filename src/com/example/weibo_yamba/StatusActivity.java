package com.example.weibo_yamba;

import java.text.SimpleDateFormat;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener {

	private static final String TAG = "StatusActivity";
	Button updateButton;
	EditText editText;
	// Weibo
	private Weibo mWeibo;
	public static Oauth2AccessToken accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);

		// Find View
		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this);

		// Weibo
		mWeibo = Weibo.getInstance(ConstantS.APP_KEY, ConstantS.REDIRECT_URL,
				ConstantS.SCOPE);
		StatusActivity.accessToken = AccessTokenKeeper.readAccessToken(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onClicked");
		mWeibo.anthorize(StatusActivity.this, new AuthDialogListener());
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String code = values.getString("code");
			if (code != null) {
				editText.setText("取得认证code: \r\n Code: " + code);
				Toast.makeText(StatusActivity.this, "认证code成功",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			StatusActivity.accessToken = new Oauth2AccessToken(token,
					expires_in);
			if (StatusActivity.accessToken.isSessionValid()) {
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new java.util.Date(StatusActivity.accessToken
								.getExpiresTime()));
				editText.setText("认证成功: \r\n access_token: " + token + "\r\n"
						+ "expires_in: " + expires_in + "\r\n有效期：" + date);

				AccessTokenKeeper.keepAccessToken(StatusActivity.this,
						accessToken);
				Toast.makeText(StatusActivity.this, "认证成功", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(WeiboDialogError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub

		}

	}

}
