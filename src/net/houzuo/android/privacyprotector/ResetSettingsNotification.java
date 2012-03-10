package net.houzuo.android.privacyprotector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public final class ResetSettingsNotification extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final List<String> toasts = new ArrayList<String>(5);
		if (PrivacyProtectorService.resetStatus.bluetooth == true) {
			PowerManagement.bluetoothOn();
			toasts.add(this.getString(R.string.bluetooth_restored));
		}
		if (PrivacyProtectorService.resetStatus.wifi == true) {
			PowerManagement.wifiOn(this);
			toasts.add(this.getString(R.string.wifi_restored));
		}
		if (PrivacyProtectorService.resetStatus.mobileData == true) {
			PowerManagement.toggleMobileData(this);
			toasts.add(this.getString(R.string.mobile_data_restore));
		}
		if (PrivacyProtectorService.resetStatus.gps == true) {
			PowerManagement.toggleGps(this);
			toasts.add(this.getString(R.string.gps_restore));
		}
		if (PrivacyProtectorService.resetStatus.networkLocation == true) {
			PowerManagement.toggleNetworkLocation(this);
			toasts.add(this.getString(R.string.network_location_restore));
		}
		Collections.reverse(toasts);
		for (final String s : toasts) {
			Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		}
		((NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE))
						.cancel(ResetSettingsNotification.NOTIFICATION_ID);
		this.finish();
	}
	
	public final static int NOTIFICATION_ID = 1;
}
