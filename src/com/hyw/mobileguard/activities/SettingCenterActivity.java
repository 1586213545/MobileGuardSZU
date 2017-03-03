package com.hyw.mobileguard.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hyw.mobileguard.services.CallSmsSafeService;
import com.hyw.mobileguard.services.ShowLocationService;
import com.hyw.mobileguard.ui.SettingView;
import com.hyw.mobileguard.utils.SystemInfoUtils;
import com.itheima.mobileguard.R;

public class SettingCenterActivity extends Activity {
	
	private static final String[] items ={"��͸��","������","��ʿ��","������","ƻ����"};
	
	private SharedPreferences sp;
	private SettingView sv_autoupdate;
	/**
	 * ������
	 */
	private SettingView sv_blacknumber;
	private Intent callSmsSafeIntent;
	/**
	 * ��������ʾ
	 */
	private SettingView sv_showaddress;
	private Intent showAddressIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		//��ʾ����Ĭ�ϵķ��
		tv_title_style = (TextView) findViewById(R.id.tv_title_style);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);
		tv_title_style.setText(items[which]);
		
		sv_autoupdate = (SettingView) findViewById(R.id.sv_autoupdate);
		sv_autoupdate.setChecked(sp.getBoolean("autoupdate", false));
		sv_autoupdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				System.out.println("hahah ��������ˡ�");
				Editor editor = sp.edit();
				//�жϹ�ѡ��״̬��
				if(sv_autoupdate.isChecked()){
					sv_autoupdate.setChecked(false);
					editor.putBoolean("autoupdate", false);
				}else{
					sv_autoupdate.setChecked(true);
					editor.putBoolean("autoupdate", true);
				}
				editor.commit();
			}
		});
		//������
		sv_blacknumber = (SettingView) findViewById(R.id.sv_blacknumber);
		callSmsSafeIntent = new Intent(this,CallSmsSafeService.class);
		sv_blacknumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sv_blacknumber.isChecked()){
					sv_blacknumber.setChecked(false);
					//ֹͣ���ط���
					stopService(callSmsSafeIntent);
				}else{
					sv_blacknumber.setChecked(true);
					//�������ط���
					startService(callSmsSafeIntent);
				}
			}
		});
		//������
		sv_showaddress = (SettingView) findViewById(R.id.sv_showaddress);
		showAddressIntent = new Intent(this,ShowLocationService.class);
		sv_showaddress.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sv_showaddress.isChecked()){
					sv_showaddress.setChecked(false);
					//ֹͣ���ط���
					stopService(showAddressIntent);
				}else{
					sv_showaddress.setChecked(true);
					//�������ط���
					startService(showAddressIntent);
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		//�жϷ��������״̬
		boolean running = SystemInfoUtils.isServiceRunning(this, "com.itheima.mobileguard.services.CallSmsSafeService");
		if(running){
			sv_blacknumber.setChecked(true);
		}else{ 
			sv_blacknumber.setChecked(false);
		}
		
		running = SystemInfoUtils.isServiceRunning(this, "com.itheima.mobileguard.services.ShowLocationService");
		if(running){
			sv_showaddress.setChecked(true);
		}else{
			sv_showaddress.setChecked(false);
		}
		super.onStart();
	}
	
	
	private TextView tv_title_style;
	/**
	 * �޸Ĺ�������ʾ�ı������
	 * @param view
	 */
	public void changeBgStyle(View view){
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.main_icon_36);
		builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = sp.edit();
				editor.putInt("which", which);
				editor.commit();
				tv_title_style.setText(items[which]);
				dialog.dismiss();
			}
		});
		builder.setTitle("��������ʾ����");
		builder.show();
	}
	
}