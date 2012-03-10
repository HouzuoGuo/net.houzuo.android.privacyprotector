package net.houzuo.android.privacyprotector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public final class PrivacyProtectorService extends Service {
	
	public void notificationOff() {
		this.nm.cancel(PrivacyProtectorService.NOTIFICATION_ID);
	}
	
	public void notificationOn() {
		Notification appNotification = new Notification(R.drawable.ic_launcher,
						"Privacy Protector", System.currentTimeMillis());
		appNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		appNotification.setLatestEventInfo(this.getApplicationContext(),
						"Privacy Protector", "Privacy Protector is running",
						PendingIntent.getActivity(this, 0, new Intent(this,
										PrivacyProtectorActivity.class), 0));
		this.nm.notify(PrivacyProtectorService.NOTIFICATION_ID, appNotification);
		
	}
	
	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		PrivacyProtectorService.instance = null;
		this.foregroundAppHandler.removeCallbacks(this.foregroundAppReporter);
		this.nm.cancelAll();
	}
	
	@Override
	public int onStartCommand(final Intent intent, final int flags,
					final int startId) {
		final PolicyDb db = new PolicyDb(this);
		PrivacyProtectorService.instance = this;
		this.appInfo = new AppInfo(this);
		this.noLocation = db.readNoLocation();
		this.noNetwork = db.readNoInternet();
		this.nm = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
		if (this.getSharedPreferences("PrivacyProtector", 0).getBoolean(
						"notify", true)) {
			this.notificationOn();
		}
		this.foregroundAppHandler.post(this.foregroundAppReporter);
		return Service.START_STICKY;
	}
	
	private void reportForegroundApp(final RunningAppProcessInfo app) {
		if (app != null) {
			final String processName = app.processName;
			final List<String> toasts = new ArrayList<String>(5);
			final Intent resetIntent = new Intent(this,
							ResetSettingsNotification.class);
			final DeviceStatus currentStatus = new DeviceStatus();
			boolean settingsAffected = false;
			if (this.noNetwork.contains(processName)) {
				if (PowerManagement.isBluetoothOn()) {
					settingsAffected = true;
					
					currentStatus.bluetooth = true;
					toasts.add(this.getString(R.string.bluetooth_disabled));
					PowerManagement.bluetoothOff();
				} else {
					currentStatus.bluetooth = false;
				}
				if (PowerManagement.isWifiOn(this)) {
					settingsAffected = true;
					currentStatus.wifi = true;
					toasts.add(this.getString(R.string.wifi_disabled));
					PowerManagement.wifiOff(this);
				} else {
					currentStatus.wifi = false;
				}
				if (PowerManagement.isMobileDataOn(this)) {
					settingsAffected = true;
					currentStatus.mobileData = true;
					toasts.add(this.getString(R.string.mobile_data_disable));
					PowerManagement.toggleMobileData(this);
				} else {
					currentStatus.mobileData = false;
				}
			}
			if (this.noLocation.contains(processName)) {
				if (PowerManagement.isGpsOn(this)) {
					settingsAffected = true;
					currentStatus.gps = true;
					toasts.add(this.getString(R.string.gps_disable));
					PowerManagement.toggleGps(this);
				} else {
					currentStatus.gps = false;
				}
				if (PowerManagement.isNetworkLocationOn(this)) {
					settingsAffected = true;
					currentStatus.networkLocation = true;
					toasts.add(this.getString(R.string.network_location_disable));
					PowerManagement.toggleNetworkLocation(this);
				} else {
					currentStatus.networkLocation = false;
				}
			}
			if (settingsAffected) {
				Collections.reverse(toasts);
				PrivacyProtectorService.resetStatus = currentStatus;
				for (final String s : toasts) {
					Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
				}
				final Notification resetNotification = new Notification(
								R.drawable.ic_launcher,
								this.getString(R.string.restore_notification_title),
								System.currentTimeMillis());
				resetNotification
								.setLatestEventInfo(
												this.getApplicationContext(),
												this.getString(R.string.restore_notification_title),
												this.getString(R.string.restore_notification_message),
												PendingIntent.getActivity(this,
																0, resetIntent,
																0));
				this.nm.notify(ResetSettingsNotification.NOTIFICATION_ID,
								resetNotification);
			}
		}
	}
	
	public final static long CHECK_RATE = 1000;
	public static PrivacyProtectorService instance = null;
	public static boolean notification;
	public final static int NOTIFICATION_ID = 2;
	public static DeviceStatus resetStatus = new DeviceStatus();
	private AppInfo appInfo;
	private final Handler foregroundAppHandler = new Handler();
	private final Runnable foregroundAppReporter = new Runnable() {
		@SuppressWarnings("synthetic-access")
		@Override
		public final void run() {
			final RunningAppProcessInfo app = PrivacyProtectorService.this.appInfo
							.getForegroundApp();
			PrivacyProtectorService.this.reportForegroundApp(app);
			PrivacyProtectorService.this.foregroundAppHandler.postDelayed(this,
							PrivacyProtectorService.CHECK_RATE);
		}
	};
	private NotificationManager nm;
	private List<String> noLocation, noNetwork;
}
