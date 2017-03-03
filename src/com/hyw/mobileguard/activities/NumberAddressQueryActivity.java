package com.hyw.mobileguard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyw.mobileguard.db.dao.NumberAddressDao;
import com.itheima.mobileguard.R;

public class NumberAddressQueryActivity extends Activity {
	private EditText et_phone_number;
	private TextView tv_address_info;
	private Vibrator  vibrator;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�õ�ϵͳ���𶯷���
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		setContentView(R.layout.activity_address_query);
		tv_address_info = (TextView) findViewById(R.id.tv_address_info);
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		et_phone_number.addTextChangedListener(new TextWatcher() {
			//���ı��仯��ʱ����õķ���
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			//���ı��仯֮ǰ���õķ��� 
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			//���ı��仯֮����õķ���
			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				String location = NumberAddressDao.getLocation(text);
				tv_address_info.setText("�����أ�"+location);
			}
		});
		
	}
	
	public void query(View view){
		String phone = et_phone_number.getText().toString().trim();
		if(TextUtils.isEmpty(phone)){
	        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
	        et_phone_number.startAnimation(shake);
	        vibrator.vibrate(new long[]{200,100,300,100,300,100}, 2);
			Toast.makeText(this, "���벻��Ϊ��", 0).show();
			return;
		}
		String location = NumberAddressDao.getLocation(phone);
		tv_address_info.setText("�����أ�"+location);
	}
}