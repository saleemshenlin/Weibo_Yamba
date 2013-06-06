package com.example.weibo_yamba;

import java.io.IOException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class StatusActivity extends BaseActivity implements OnClickListener,
		TextWatcher, OnSharedPreferenceChangeListener {
	private static final String TAG = "StatusActivity";
	private StatusesAPI api = null;

	SharedPreferences prefs;
	// private Handler handler = null;

	EditText editText;
	Button updateButton;
	String lat = "31.5";
	String lon = "121.5";
	TextView textCount;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		// Find views
		editText = (EditText) findViewById(R.id.editText); //
		updateButton = (Button) findViewById(R.id.buttonUpdate);
		updateButton.setOnClickListener(this); //
		// 创建属于主线程的handler
		// handler = new Handler();
		textCount = (TextView) findViewById(R.id.textCount);
		textCount.setText("还可以输入" + Integer.toString(140) + "字");
		textCount.setTextColor(Color.WHITE);
		editText.addTextChangedListener(this);
		// 设置 prefs
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		api = yambaApplication.getStatusesAPI(this);

	}

	// Asynchronously posts to Weibo
	class PostToWeibo extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... statuses) {
			api.update(statuses[0], lat, lon, new RequestListener() {

				@Override
				public void onIOException(IOException arg0) {
					// editText.setText(arg0.toString());
				}

				@Override
				public void onError(WeiboException arg0) {
					// editText.setText(arg0.toString());
				}

				@Override
				public void onComplete(String arg0) {
				}
			});
			return statuses[0];
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(String result) {
			editText = (EditText) findViewById(R.id.editText);
			editText.setText("");
			editText.setHint("发言请遵守社区公约，还剩下140个字！");
			Toast.makeText(StatusActivity.this, "发送成功！Yeah！", Toast.LENGTH_LONG)
					.show();

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onclicked");
		final String content = editText.getText().toString();
		new PostToWeibo().execute(content);
		// new Thread() {
		// public void run() {
		// // api.update(content, lat, lon, new RequestListener() {
		// //
		// // @Override
		// // public void onIOException(IOException arg0) {
		// // // TODO Auto-generated method stub
		// // editText.setText(arg0.toString());
		// // }
		// //
		// // @Override
		// // public void onError(WeiboException arg0) {
		// // // TODO Auto-generated method stub
		// // editText.setText(arg0.toString());
		// // }
		// //
		// // @Override
		// // public void onComplete(String arg0) {
		// // // TODO Auto-generated method stub
		// // }
		// // });
		// // handler.post(runnableUI);
		// }
		// }.start();

	}

	// TextWatcher methods

	public void afterTextChanged(Editable statusText) {
		int count = 140 - statusText.length();
		if (count > 10) {
			textCount.setText("还可以输入" + Integer.toString(count) + "字");
			textCount.setTextColor(Color.WHITE);
			updateButton.setEnabled(true);
		} else if (count <= 10 && count >= 0) {
			textCount.setText("还可以输入" + Integer.toString(count) + "字");
			textCount.setTextColor(Color.YELLOW);
			updateButton.setEnabled(true);
		} else {
			textCount.setText("已超过" + Integer.toString(count) + "字");
			textCount.setTextColor(Color.RED);
			updateButton.setEnabled(false);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		//
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		//
	}

	Runnable runnableUI = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			editText = (EditText) findViewById(R.id.editText);
			editText.setText("");
			editText.setHint("发言请遵守社区公约，还剩下140个字！");
			Toast.makeText(StatusActivity.this, "发送成功！Yeah！", Toast.LENGTH_LONG)
					.show();
		}
	};

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}
	

}
