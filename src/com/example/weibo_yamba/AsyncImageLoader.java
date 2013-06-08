package com.example.weibo_yamba;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 对于图片异步处理类
 * 
 * @author liuxr
 * 
 */
public class AsyncImageLoader {

	static ImageView singImageView; // 针对于单张图片异步加载
	private static HashMap<String, SoftReference<Drawable>> singleImageCache = null;

	/**
	 * 通过图片地址,返回drawable
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImageFromUrl(String url) {
		ByteArrayOutputStream out = null;
		Drawable drawable = null;
		int BUFFER_SIZE = 1024 * 16;
		InputStream inputStream = null;
		try {
			inputStream = new URL(url).openStream();
			BufferedInputStream in = new BufferedInputStream(inputStream,
					BUFFER_SIZE);
			out = new ByteArrayOutputStream(BUFFER_SIZE);
			int length = 0;
			byte[] tem = new byte[BUFFER_SIZE];
			length = in.read(tem);
			while (length != -1) {
				out.write(tem, 0, length);
				length = in.read(tem);
			}
			in.close();
			drawable = Drawable.createFromStream(
					new ByteArrayInputStream(out.toByteArray()), "src");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
		return drawable;
	}

	/**
	 * 异步设置单张imageview图片,采取软引用
	 * 
	 * @param url
	 *            网络图片地址
	 * @param imageView
	 *            需要设置的imageview
	 */
	public static void setImageViewFromUrl(final String url,
			final ImageView imageView) {
		singImageView = imageView;
		// 如果软引用为空,就新建一个
		if (singleImageCache == null) {
			singleImageCache = new HashMap<String, SoftReference<Drawable>>();
		}
		// 如果软引用中已经有了相同的地址,就从软引用中获取
		if (singleImageCache.containsKey(url)) {
			SoftReference<Drawable> soft = singleImageCache.get(url);
			Drawable draw = soft.get();
			singImageView.setImageDrawable(draw);
			return;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				singImageView.setImageDrawable((Drawable) msg.obj);
			}
		};
		// 子线程不能更新UI,又不然会报错
		new Thread() {
			public void run() {
				Drawable drawable = loadImageFromUrl(url);
				if (drawable == null) {
					Log.e("single imageview",
							"single imageview of drawable is null");
				} else {
					// 把已经读取到的图片放入软引用
					singleImageCache.put(url, new SoftReference<Drawable>(
							drawable));
				}
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			};
		}.start();
	}

	/*
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 这里的path是图片的地址
	 */
	public Uri getImageURI(String path, File cache) throws Exception {
		String name = MD5.getMD5(path) + ".jpg";
		File file = new File(cache, name);
		// 如果图片存在本地缓存目录，则不去服务器下载
		if (file.exists()) {
			return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
		} else {
			// 从网络上获取图片
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			if (conn.getResponseCode() == 200) {

				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
				// 返回一个URI对象
				return Uri.fromFile(file);
			}
		}
		return null;
	}

	public static class MD5 {

		public static String getMD5(String content) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(content.getBytes());
				return getHashString(digest);

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return null;
		}

		private static String getHashString(MessageDigest digest) {
			StringBuilder builder = new StringBuilder();
			for (byte b : digest.digest()) {
				builder.append(Integer.toHexString((b >> 4) & 0xf));
				builder.append(Integer.toHexString(b & 0xf));
			}
			return builder.toString();
		}
	}

	public void asyncloadImage(ImageView imageView, String path) {
		AsyncGetImg task = new AsyncGetImg(imageView);
		task.execute(path);
	}

	/**
	 * 采用普通方式异步的加载图片
	 */
	class AsyncGetImg extends AsyncTask<String, Integer, Uri> {

		private ImageView imageView;

		public AsyncGetImg(ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		protected Uri doInBackground(String... path) {
			try {
				return getImageURI(path[0], YambaApplication.imgCache);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Uri result) {
			super.onPostExecute(result);
			Log.i("AsyncGetImg", result.toString());
			if (imageView != null && result != null) {
				imageView.setImageURI(result);
			}

		}
	}
}
