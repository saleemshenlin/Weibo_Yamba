package com.example.weibo_yamba;

import java.io.IOException;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener { //

	EditText editText;
	Button updateButton;
	StatusesAPI weiboAPI = new StatusesAPI(
			AccessTokenKeeper.readAccessToken(this));

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		editText = (EditText) findViewById(R.id.editText); //
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this); //
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String lat = "31";
		String lon = "121";
		String content = editText.getText().toString();
		weiboAPI.update(content, lat, lon, new StatusesListener());
	}

	class StatusesListener implements RequestListener {

		@Override
		public void onComplete(String arg0) {
			// TODO Auto-generated method stub
			Toast.makeText(StatusActivity.this, "发送成功", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onError(WeiboException e) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

		@Override
		public void onIOException(IOException e) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(),
					"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}

	}

}