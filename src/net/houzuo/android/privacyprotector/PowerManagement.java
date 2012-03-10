package net.houzuo.android.privacyprotector;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

public final class PowerManagement {
	public static void bluetoothOff() {
		if (PowerManagement.hasBluetooth() && PowerManagement.isBluetoothOn()) {
			BluetoothAdapter.getDefaultAdapter().disable();
		}
	}
	
	public static void bluetoothOn() {
		if (PowerManagement.hasBluetooth() && !PowerManagement.isBluetoothOn()) {
			BluetoothAdapter.getDefaultAdapter().enable();
		}
	}
	
	public static boolean hasBluetooth() {
		return BluetoothAdapter.getDefaultAdapter() != null;
	}
	
	public static boolean hasGps(final Context context) {
		return ((LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE))
						.getAllProviders().contains(
										LocationManager.GPS_PROVIDER);
	}
	
	public static boolean hasMobileData(final Context context) {
		return ((ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE))
						.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null;
	}
	
	public static boolean hasNetworkLocation(final Context context) {
		return ((LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE))
						.getAllProviders().contains(
										LocationManager.NETWORK_PROVIDER);
	}
	
	public static boolean hasWifi(final Context context) {
		return ((ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE))
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null;
	}
	
	public static boolean isBluetoothOn() {
		return PowerManagement.hasBluetooth()
						&& BluetoothAdapter.getDefaultAdapter().isEnabled() ? true
						: false;
	}
	
	public static boolean isGpsOn(final Context context) {
		return PowerManagement.hasGps(context)
						&& ((LocationManager) context
										.getSystemService(Context.LOCATION_SERVICE))
										.isProviderEnabled(LocationManager.GPS_PROVIDER) ? true
						: false;
	}
	
	public static boolean isMobileDataOn(final Context context) {
		return PowerManagement.hasMobileData(context)
						&& ((ConnectivityManager) context
										.getSystemService(Context.CONNECTIVITY_SERVICE))
										.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
										.isConnectedOrConnecting() ? true
						: false;
	}
	
	public static boolean isNetworkLocationOn(final Context context) {
		return PowerManagement.hasNetworkLocation(context)
						&& ((LocationManager) context
										.getSystemService(Context.LOCATION_SERVICE))
										.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? true
						: false;
	}
	
	public static boolean isWifiOn(final Context context) {
		return PowerManagement.hasWifi(context)
						&& ((WifiManager) context
										.getSystemService(Context.WIFI_SERVICE))
										.isWifiEnabled() ? true : false;
	}
	
	public static DeviceStatus readDeviceAvailability(final Context context) {
		return new DeviceStatus(PowerManagement.hasWifi(context),
						PowerManagement.hasGps(context),
						PowerManagement.hasNetworkLocation(context),
						PowerManagement.hasMobileData(context),
						PowerManagement.hasBluetooth());
	}
	
	public static DeviceStatus readPowerStatus(final Context context) {
		return new DeviceStatus(PowerManagement.isWifiOn(context),
						PowerManagement.isGpsOn(context),
						PowerManagement.isNetworkLocationOn(context),
						PowerManagement.isMobileDataOn(context),
						PowerManagement.isBluetoothOn());
	}
	
	public static void toggleGps(final Context context) {
		if (PowerManagement.hasGps(context)) {
			context.startActivity(new Intent(
							android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
	
	public static void toggleMobileData(final Context context) {
		if (PowerManagement.hasMobileData(context)) {
			context.startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
	
	public static void toggleNetworkLocation(final Context context) {
		if (PowerManagement.hasNetworkLocation(context)) {
			PowerManagement.toggleGps(context);
		}
	}
	
	public static void wifiOff(final Context context) {
		if (PowerManagement.hasWifi(context)
						&& PowerManagement.isWifiOn(context)) {
			((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
							.setWifiEnabled(false);
		}
	}
	
	public static void wifiOn(final Context context) {
		if (PowerManagement.hasWifi(context)
						&& !PowerManagement.isWifiOn(context)) {
			((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
							.setWifiEnabled(true);
		}
	}
}

final class DeviceStatus {
	public DeviceStatus() {
	}
	
	public DeviceStatus(final boolean wifi, final boolean gps,
					final boolean networkLocation, final boolean mobileData,
					final boolean bluetooth) {
		this.wifi = wifi;
		this.gps = gps;
		this.networkLocation = networkLocation;
		this.mobileData = mobileData;
		this.bluetooth = bluetooth;
	}
	
	@Override
	public boolean equals(final Object another) {
		final DeviceStatus ds = (DeviceStatus) another;
		return ds.wifi == this.wifi && ds.bluetooth == this.bluetooth
						&& ds.mobileData == this.mobileData
						&& ds.gps == this.gps
						&& ds.networkLocation == this.networkLocation ? true
						: false;
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString() {
		return "Wifi " + this.wifi + ", MobileData " + this.mobileData
						+ ", Bluetooth " + this.bluetooth + ", GPS " + this.gps
						+ ", NetworkLocation " + this.networkLocation;
	}
	
	public boolean bluetooth, wifi, gps, networkLocation, mobileData;
}