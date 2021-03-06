package com.funo.appmarket.activity;

import java.util.List;

import com.bumptech.glide.Glide;
import com.funo.appmarket.R;
import com.funo.appmarket.activity.base.BaseActivity;
import com.funo.appmarket.bean.StatusChangeNotify;
import com.funo.appmarket.business.StatusChangeNotifyService;
import com.funo.appmarket.business.StatusChangeNotifyService.StatusChangeNotifyCallback;
import com.funo.appmarket.business.SyncEquipmentInfoService;
import com.funo.appmarket.business.define.IStatusChangeNotifyService.StatusChangeNotifyParam;
import com.funo.appmarket.business.define.ISyncEquipmentInfoService.SyncEquipmentInfoParam;
import com.funo.appmarket.db.AppModelDB;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingActivity extends BaseActivity {

	private ProgressBar pb;
	private TextView tv;
	
	private boolean statusChangeNotifyFlag = false;// 调用状态改变通知接口是否完成
	
	private boolean clearImageCacheFinished = false;// 是否已经清除图片缓存
	
	private StatusChangeNotifyService statusChangeNotifyService;
	private SyncEquipmentInfoService syncEquipmentInfoService;
	
	private String product_model;
	private String product_serialnum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		product_model = SystemProperties.get("ro.product.model", "");
		product_serialnum = SystemProperties.get("ro.product.stb.serialnum", "");
		
		AppModelDB.clearUninstalledApps();
		
		statusChangeNotifyService = new StatusChangeNotifyService(getContext());
		StatusChangeNotifyParam statusChangeNotifyParam = new StatusChangeNotifyParam();
		statusChangeNotifyService.statusChangeNotify(statusChangeNotifyParam, new StatusChangeNotifyCallback() {
			
			@Override
			public void doCallback(List<StatusChangeNotify> statusChangeNotifies) {
				if (statusChangeNotifies != null) {
					if (!statusChangeNotifies.isEmpty()) {
						StatusChangeNotify statusChangeNotify = statusChangeNotifies.get(0);
						sys_sp.edit().putBoolean("isTypeChage", statusChangeNotify.isTypeChage()).apply();
						sys_sp.edit().putInt("templateUsedId", statusChangeNotify.getTemplateUsedId()).apply();
					}
				}
				
				statusChangeNotifyFlag = true;
			}
			
		});
		
		syncEquipmentInfoService = new SyncEquipmentInfoService(getContext());
		SyncEquipmentInfoParam syncEquipmentInfoParam = new SyncEquipmentInfoParam();
		String brand = android.os.Build.BRAND;
		syncEquipmentInfoParam.eqNo = brand + product_model + product_serialnum;
		syncEquipmentInfoService.syncEquipmentInfo(syncEquipmentInfoParam, null);
		
		setContentView(R.layout.activity_loading);
		
		pb = (ProgressBar) findViewById(R.id.pb);
		tv = (TextView) findViewById(R.id.tv);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 必须在后台线程中调用，建议同时clearMemory()
				Glide.get(getContext()).clearDiskCache();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// 必须在UI线程中调用
						Glide.get(getContext()).clearMemory();
						
						clearImageCacheFinished = true;
					}

				});
			}

		}).start();
		
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				if (pb.getProgress() < 100) {
					pb.setProgress(pb.getProgress() + 2);
				}
				if (pb.getProgress() == 100 && statusChangeNotifyFlag && clearImageCacheFinished) {
					startActivity(new Intent(getContext(), MainActivity.class));
					finish();
				} else {
					pb.postDelayed(this, 50);
				}
			}
			
		};
		pb.post(r);
	}

}
