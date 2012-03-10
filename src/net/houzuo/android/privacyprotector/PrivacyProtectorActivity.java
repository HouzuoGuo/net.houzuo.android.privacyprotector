package net.houzuo.android.privacyprotector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public final class PrivacyProtectorActivity extends Activity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		this.wifiToggleButton = (ToggleButton) this
						.findViewById(R.id.wifiToggleButtion);
		this.wifiToggleButton.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public final void onClick(final View v) {
				PrivacyProtectorActivity.this.statusHandler
								.removeCallbacks(PrivacyProtectorActivity.this.statusUpdater);
				PrivacyProtectorActivity.this.statusHandler.postDelayed(
								PrivacyProtectorActivity.this.statusUpdater,
								PrivacyProtectorActivity.REFRESH_LONG_DELAY);
				if (PowerManagement.isWifiOn(PrivacyProtectorActivity.this)) {
					PowerManagement.wifiOff(PrivacyProtectorActivity.this);
				} else {
					PowerManagement.wifiOn(PrivacyProtectorActivity.this);
				}
			}
		});
		this.bluetoothToggleButton = (ToggleButton) this
						.findViewById(R.id.bluetoothToggleButton);
		this.bluetoothToggleButton
						.setOnClickListener(new View.OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								PrivacyProtectorActivity.this.statusHandler
												.removeCallbacks(PrivacyProtectorActivity.this.statusUpdater);
								PrivacyProtectorActivity.this.statusHandler
												.postDelayed(PrivacyProtectorActivity.this.statusUpdater,
																PrivacyProtectorActivity.REFRESH_LONG_DELAY);
								if (PowerManagement.isBluetoothOn()) {
									PowerManagement.bluetoothOff();
								} else {
									PowerManagement.bluetoothOn();
								}
							}
						});
		this.mobileDataToggleButton = (ToggleButton) this
						.findViewById(R.id.mobileDataToggleButton);
		this.mobileDataToggleButton
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public final void onClick(final View v) {
								PowerManagement.toggleMobileData(PrivacyProtectorActivity.this);
							}
						});
		this.gpsToggleButton = (ToggleButton) this
						.findViewById(R.id.gpsToggleButton);
		this.gpsToggleButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public final void onClick(final View v) {
				PowerManagement.toggleGps(PrivacyProtectorActivity.this);
			}
		});
		this.networkLocationToggleButton = (ToggleButton) this
						.findViewById(R.id.networkLocationToggleButton);
		this.networkLocationToggleButton
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public final void onClick(final View v) {
								PowerManagement.toggleNetworkLocation(PrivacyProtectorActivity.this);
							}
						});
		this.statusToggleButton = (ToggleButton) this
						.findViewById(R.id.serviceToggleButtion);
		this.statusToggleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public final void onClick(final View v) {
				if (PrivacyProtectorService.instance == null) {
					PrivacyProtectorActivity.this.startService(new Intent(
									PrivacyProtectorActivity.this,
									PrivacyProtectorService.class));
				} else {
					PrivacyProtectorService.instance.stopSelf();
				}
			}
		});
		this.howButton = (Button) this.findViewById(R.id.howButton);
		this.howButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public final void onClick(final View v) {
				AlertDialog ad = new AlertDialog.Builder(
								PrivacyProtectorActivity.this)
								.setTitle(R.string.how_this_works)
								.setMessage(R.string.how_this_actually_works)
								.create();
				ad.setButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public final void onClick(final DialogInterface dialog,
									final int which) {
						dialog.dismiss();
					}
				});
				ad.show();
			}
		});
		this.manageButton = (Button) this.findViewById(R.id.manageButton);
		this.manageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public final void onClick(final View v) {
				PrivacyProtectorActivity.this.startActivity(new Intent(
								PrivacyProtectorActivity.this,
								AppListActivity.class));
			}
		});
		this.notificationToggleButton = (ToggleButton) this
						.findViewById(R.id.notificationToggleButton);
		this.notificationToggleButton
						.setOnClickListener(new View.OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								final Editor e = PrivacyProtectorActivity.this
												.getSharedPreferences(
																"PrivacyProtector",
																0).edit();
								if (PrivacyProtectorActivity.this.notificationToggleButton
												.isChecked()) {
									if (PrivacyProtectorService.instance != null) {
										PrivacyProtectorService.instance
														.notificationOn();
									}
									e.putBoolean("notify", true);
								} else {
									if (PrivacyProtectorService.instance != null) {
										PrivacyProtectorService.instance
														.notificationOff();
									}
									e.putBoolean("notify", false);
								}
								e.commit();
							}
						});
		
		this.refreshStatus();
		this.statusHandler.post(this.statusUpdater);
	}
	
	private void refreshStatus() {
		final DeviceStatus availability = PowerManagement
						.readDeviceAvailability(this);
		this.bluetoothToggleButton.setEnabled(availability.bluetooth);
		this.wifiToggleButton.setEnabled(availability.wifi);
		this.gpsToggleButton.setEnabled(availability.gps);
		this.networkLocationToggleButton
						.setEnabled(availability.networkLocation);
		this.mobileDataToggleButton.setEnabled(availability.mobileData);
		final DeviceStatus powerStats = PowerManagement.readPowerStatus(this);
		this.bluetoothToggleButton.setChecked(powerStats.bluetooth);
		this.wifiToggleButton.setChecked(powerStats.wifi);
		this.gpsToggleButton.setChecked(powerStats.gps);
		this.networkLocationToggleButton.setChecked(powerStats.networkLocation);
		this.mobileDataToggleButton.setChecked(powerStats.mobileData);
		this.statusToggleButton
						.setChecked(PrivacyProtectorService.instance != null);
		this.notificationToggleButton.setChecked(this.getSharedPreferences(
						"PrivacyProtector", 0).getBoolean("notify", true));
	}
	
	public final static long REFRESH_LONG_DELAY = 4000;
	public final static long REFRESH_RATE = 1000;
	private Button howButton, manageButton;
	private final Handler statusHandler = new Handler();
	private final Runnable statusUpdater = new Runnable() {
		@SuppressWarnings("synthetic-access")
		@Override
		public final void run() {
			PrivacyProtectorActivity.this.refreshStatus();
			PrivacyProtectorActivity.this.statusHandler.postDelayed(this,
							PrivacyProtectorActivity.REFRESH_RATE);
		}
	};
	private ToggleButton wifiToggleButton, networkLocationToggleButton,
					gpsToggleButton, mobileDataToggleButton,
					bluetoothToggleButton, statusToggleButton,
					notificationToggleButton;
}