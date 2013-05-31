package com.example.weibo_yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.Utility;

public class SSO_Main extends Activity {
	public static Oauth2AccessToken accessToken;
	public static final String TAG = "sinasdk";
	/**
	 * SsoHandler 仅当sdk支持sso时有效，
	 */
	SsoHandler mSsoHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SSO_Main.accessToken = AccessTokenKeeper.readAccessToken(this);
		YambaApplication yambaApplication = ((YambaApplication) getApplication());
		if (SSO_Main.accessToken.isSessionValid()) {
			Weibo.isWifi = Utility.isWifi(this);
			Toast.makeText(SSO_Main.this, "认证成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(SSO_Main.this, TimelineActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(SSO_Main.this, "开始认证", Toast.LENGTH_SHORT).show();
			mSsoHandler = new SsoHandler(SSO_Main.this,
					yambaApplication.getWeibo());
			mSsoHandler.authorize(new AuthDialogListener());
			Intent intent = new Intent(SSO_Main.this, TimelineActivity.class);
			startActivity(intent);
		}
	}

	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			String token = values.getString("access_token");
			String expires_in = values.getString("expires_in");
			SSO_Main.accessToken = new Oauth2AccessToken(token, expires_in);
			if (SSO_Main.accessToken.isSessionValid()) {
				AccessTokenKeeper.keepAccessToken(SSO_Main.this, accessToken);
				Toast.makeText(SSO_Main.this, "认证成功", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Toast.makeText(getApplicationContext(),
					"Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(getApplicationContext(), "Auth cancel",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/**
		 * 下面两个注释掉的代码，仅当sdk支持sso时有效，
		 */
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

}
