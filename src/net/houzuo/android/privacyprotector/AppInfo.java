package net.houzuo.android.privacyprotector;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public final class AppInfo {
	public AppInfo(final Context context) {
		this.activityManager = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
		this.packageManager = context.getPackageManager();
	}
	
	public RunningAppProcessInfo getForegroundApp() {
		for (final RunningAppProcessInfo app : this.activityManager
						.getRunningAppProcesses()) {
			if (app.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
							&& !this.isService(app.processName)) {
				return app;
			}
		}
		return null;
	}
	
	public List<App> getInteretingApps() {
		final List<App> apps = new ArrayList<App>(400);
		for (final PackageInfo p : this.packageManager
						.getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
			if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				if (!p.packageName
								.equals("net.houzuo.android.privacyprotector")) {
					String[] permissions = p.requestedPermissions;
					final App app = new App(
									p.applicationInfo.loadLabel(
													this.packageManager)
													.toString(),
									p.packageName,
									p.applicationInfo
													.loadIcon(this.packageManager));
					if (permissions != null) {
						for (int i = 0; i < permissions.length; ++i) {
							if (permissions[i]
											.equals("android.permission.ACCESS_FINE_LOCATION")
											|| permissions[i]
															.equals("android.permission.ACCESS_COARSE_LOCATION")) {
								app.useLoc = true;
							} else if (permissions[i]
											.equals("android.permission.BLUETOOTH")
											|| permissions[i]
															.equals("android.permission.INTERNET")) {
								app.useNet = true;
							}
						}
						if (app.useNet || app.useLoc) {
							apps.add(app);
						}
					}
				}
			}
		}
		return apps;
	}
	
	public RunningServiceInfo getService(final String processName) {
		for (final RunningServiceInfo service : this.activityManager
						.getRunningServices(999)) {
			if (service.process.equals(processName)) {
				return service;
			}
		}
		return null;
	}
	
	public boolean isService(final String processName) {
		for (final RunningServiceInfo service : this.activityManager
						.getRunningServices(999)) {
			if (service.process.equals(processName)) {
				return true;
			}
		}
		return false;
	}
	
	private final ActivityManager activityManager;
	private final PackageManager packageManager;
}

class App {
	public App(final String name, final String packageName, final Drawable icon) {
		this.icon = icon;
		this.name = name;
		this.packageName = packageName;
	}
	
	@Override
	public boolean equals(final Object o) {
		final App a = (App) o;
		return a.packageName.equals(this.packageName);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	public final Drawable icon;
	public final String name, packageName;
	public boolean useNet, useLoc;
}