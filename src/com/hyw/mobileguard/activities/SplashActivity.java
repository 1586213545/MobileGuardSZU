package com.hyw.mobileguard.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.hyw.mobileguard.utils.StreamUtils;
import com.hyw.mobileguard.utils.UIUtils;
import com.itheima.mobileguard.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {
	protected static final int SHOW_UPDATE_DIALOG = 1;
	private static final int LOAD_MAINUI = 2;
	private TextView tv_splash_version;
	private TextView tv_info;
	// ��������
	private PackageManager packageManager;
	private int clientVersionCode;

	// ����������Դ������Ϣ
	private String desc;
	// ��������Դ������·��
	private String downloadurl;

	// ��Ϣ������
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOAD_MAINUI:
				loadMainUI();
				break;
			case SHOW_UPDATE_DIALOG:
				// ��Ϊ�Ի�����activity��һ������ʾ�Ի��� ����ָ��activity�Ļ��������ƣ�
				AlertDialog.Builder builder = new Builder(SplashActivity.this);
				builder.setTitle("��������");
				builder.setMessage(desc);
				// builder.setCancelable(false);
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						loadMainUI();
					}
				});
				builder.setNegativeButton("�´���˵", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loadMainUI();
					}
				});
				builder.setPositiveButton("���̸���", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("���أ�" + downloadurl);
						download(downloadurl);
					}

				});
				builder.show();
				break;
			}
		};
	};

	/**
	 * ���̵߳�������
	 * 
	 * @param downloadurl
	 */
	private void download(String downloadurl) {
		// ���̶߳ϵ����ء�
		HttpUtils http = new HttpUtils();
		http.download(downloadurl, "/mnt/sdcard/temp.apk",
				new RequestCallBack<File>() {
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						System.out.println("��װ /mnt/sdcard/temp.apk");
						// <action android:name="android.intent.action.VIEW" />
						// <category
						// android:name="android.intent.category.DEFAULT" />
						// <data android:scheme="content" />
						// <data android:scheme="file" />
						// <data
						// android:mimeType="application/vnd.android.package-archive"
						// />
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");

						// intent.setType("application/vnd.android.package-archive");
						// intent.setData(Uri.fromFile(new
						// File(Environment.getExternalStorageDirectory(),"temp.apk")));
						intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(), "temp.apk")),
								"application/vnd.android.package-archive");
						startActivityForResult(intent, 0);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toast.makeText(SplashActivity.this, "����ʧ��", 0).show();
						System.out.println(arg1);
						arg0.printStackTrace();
						loadMainUI();
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						tv_info.setText(current + "/" + total);
						super.onLoading(total, current, isUploading);
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		loadMainUI();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_info = (TextView) findViewById(R.id.tv_info);
		packageManager = getPackageManager();
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash_root).startAnimation(aa);
		
		//�����ʲ�Ŀ¼�µ����ݿ��ļ�
		copyDB("address.db");
		
		//������ݷ�ʽ
		createShortcut();
	    
		

		try {
			PackageInfo packInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			String version = packInfo.versionName;
			clientVersionCode = packInfo.versionCode;
			tv_splash_version.setText(version);
			// �ж��Ƿ���Ҫ�����°汾
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean autoupdate = sp.getBoolean("autoupdate", false);
			if (autoupdate) {
				checkVersion();
			}else{
				//�Զ����±��رա�
				new Thread(){
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						loadMainUI();
					};
				}.start();
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// ���ᷢ�� can't reach
		}

	}

	
    /**
     * ��ݷ�ʽ
     */
    
	private void createShortcut() {
		
		
		Intent intent = new Intent();
		
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//�������Ϊtrue��ʾ���Դ����ظ��Ŀ�ݷ�ʽ
		intent.putExtra("duplicate", false);
		
		/**
		 * 1 ��ʲô����
		 * 2 ���ʲô����
		 * 3�㳤��ʲô����
		 */
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "�����ֻ���ʿ");
		//��ʲô����
		/**
		 * ����ط�����ʹ����ʾ��ͼ
		 * ����ʹ����ʽ��ͼ
		 */
		Intent shortcut_intent = new Intent();
		
		shortcut_intent.setAction("aaa.bbb.ccc");
		
		shortcut_intent.addCategory("android.intent.category.DEFAULT");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent);
		
		sendBroadcast(intent);
		
	}

	/**
	 * �����ʲ�Ŀ¼�µ����ݿ��ļ�
	 * @param dbname  ���ݿ��ļ�������
	 */
	private void copyDB(final String dbname) {
		new Thread(){
			public void run() {
				try {
					File file = new File(getFilesDir(),dbname);
					if(file.exists()&&file.length()>0){
						Log.i("SplashActivity","���ݿ��Ǵ��ڵġ����追��");
						return ;
					}
					InputStream is = getAssets().open(dbname);
					FileOutputStream fos  = openFileOutput(dbname, MODE_PRIVATE);
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = is.read(buffer))!=-1){
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * ���ӷ����� ���汾�� �Ƿ��и���
	 */
	private void checkVersion() {
		new Thread() {
			public void run() {
				Message msg = Message.obtain();
				// ��� ����ִ�е�ʱ�䡣���ʱ������2�� ����2��
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL(getResources().getString(
							R.string.serverurl));
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(2000);
					int code = conn.getResponseCode();
					if (code == 200) {
						InputStream is = conn.getInputStream();// json�ı�
						String json = StreamUtils.readStream(is);
						if (TextUtils.isEmpty(json)) {
							// ������json��ȡʧ��
							// ����2016 ����ϵ�ͷ�
							UIUtils.showToast(SplashActivity.this,
									"����2016, ������json��ȡʧ��,����ϵ�ͷ�");
							msg.what = LOAD_MAINUI;
						} else {
							JSONObject jsonObj = new JSONObject(json);
							downloadurl = jsonObj.getString("downloadurl");
							int serverVersionCode = jsonObj.getInt("version");
							desc = jsonObj.getString("desc");
							if (clientVersionCode == serverVersionCode) {
								// ��ͬ������Ӧ�ó���������
								msg.what = LOAD_MAINUI;
							} else {
								// ��ͬ�������������ѶԻ���
								msg.what = SHOW_UPDATE_DIALOG;
							}
						}
					} else {
						// ����2015 ����ϵ�ͷ�
						UIUtils.showToast(SplashActivity.this,
								"����2015, ������״̬�����,����ϵ�ͷ�");
						msg.what = LOAD_MAINUI;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					// ����2011 ����ϵ�ͷ�
					UIUtils.showToast(SplashActivity.this,
							"����2011, url·������ȷ,����ϵ�ͷ�");
					msg.what = LOAD_MAINUI;
				} catch (NotFoundException e) {
					e.printStackTrace();
					// ����2012 ����ϵ�ͷ�
					UIUtils.showToast(SplashActivity.this,
							"����2012, ��������ַ�Ҳ���,����ϵ�ͷ�");
					msg.what = LOAD_MAINUI;
				} catch (IOException e) {
					e.printStackTrace();
					// ����2013 ����ϵ�ͷ�
					UIUtils.showToast(SplashActivity.this, "����2013, �������,����ϵ�ͷ�");
					msg.what = LOAD_MAINUI;
				} catch (JSONException e) {
					e.printStackTrace();
					// ����2014 ����ϵ�ͷ�
					UIUtils.showToast(SplashActivity.this,
							"����2014, json��������,����ϵ�ͷ�");
					msg.what = LOAD_MAINUI;
				} finally {
					long endtime = System.currentTimeMillis();
					long dtime = endtime - startTime;
					if (dtime > 2000) {
						handler.sendMessage(msg);
					} else {
						try {
							Thread.sleep(2000 - dtime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						handler.sendMessage(msg);
					}
				}
			};
		}.start();

	}

	private void loadMainUI() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

}