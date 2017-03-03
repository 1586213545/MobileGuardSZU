package com.hyw.mobileguard.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyw.mobileguard.utils.SmsUtils;
import com.hyw.mobileguard.utils.UIUtils;
import com.hyw.mobileguard.utils.SmsUtils.BackUpCallBackSms;
import com.itheima.mobileguard.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class AtoolsActivity extends Activity {
	
	private Button button;
	private ProgressDialog pd;
	@ViewInject(R.id.progressBar1)
	private ProgressBar progressBar1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
		ViewUtils.inject(this);
		
	}
	
	
	public void numberAddressQuery(View view){
		Intent intent = new Intent(this,NumberAddressQueryActivity.class);
		startActivity(intent);
	}
	
	/**
	 * ���ݶ���
	 * @param view
	 */
	public void backUpsms(View view){
		//��ʼ��һ���������ĶԻ���
		pd = new ProgressDialog(AtoolsActivity.this);
		pd.setTitle("��ʾ");
		pd.setMessage("�԰����ꡣ���ڱ��ݡ�����Űɡ���");
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.show();
		new Thread(){
			public void run() {
				boolean result = SmsUtils.backUp(AtoolsActivity.this,new BackUpCallBackSms() {
					
					@Override
					public void onBackUpSms(int process) {
						pd.setProgress(process);
						progressBar1.setProgress(process);
						
					}
					
					@Override
					public void befor(int count) {
						pd.setMax(count);
						progressBar1.setMax(count);
					}
				});
				if(result){
					//��ȫ����˾�ķ���
					UIUtils.showToast(AtoolsActivity.this, "���ݳɹ�");
				}else{
					UIUtils.showToast(AtoolsActivity.this, "����ʧ��");
				}
				pd.dismiss();
			};
		}.start();
		
		
	}
	

}