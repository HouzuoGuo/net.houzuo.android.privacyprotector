package net.houzuo.android.privacyprotector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public final class AppListActivity extends Activity {
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.app_list);
		this.db = new PolicyDb(this);
		this.appListView = (ListView) this.findViewById(R.id.appListView);
		((Button) this.findViewById(R.id.saveButton))
						.setOnClickListener(new View.OnClickListener() {
							@SuppressWarnings("synthetic-access")
							@Override
							public final void onClick(final View v) {
								AppListActivity.this.db
												.writeNoInternet(AppListActivity.this.noInternet);
								AppListActivity.this.db
												.writeNoLocation(AppListActivity.this.noLocation);
								if (PrivacyProtectorService.instance != null) {
									PrivacyProtectorService.instance.stopSelf();
									AppListActivity.this
													.startService(new Intent(
																	AppListActivity.this,
																	PrivacyProtectorService.class));
								}
								AppListActivity.this.finish();
							}
						});
		((Button) this.findViewById(R.id.cancelButton))
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public final void onClick(final View v) {
								AppListActivity.this.finish();
							}
						});
		this.loadingDialog = new ProgressDialog(this);
		this.load();
	}
	
	private void load() {
		this.loadingDialog.setTitle("Please wait");
		this.loadingDialog
						.setMessage("Loading app list...\n(This may take up to a minute)");
		this.loadingDialog.setCancelable(false);
		this.loadingDialog.show();
		new Thread() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void run() {
				final List<App> apps = new AppInfo(AppListActivity.this)
								.getInteretingApps();
				AppListActivity.this.noInternet = AppListActivity.this.db
								.readNoInternet();
				AppListActivity.this.noLocation = AppListActivity.this.db
								.readNoLocation();
				AppListActivity.this.loadingHandler.post(new Thread() {
					@Override
					public void run() {
						AppListActivity.this.appListView
										.setAdapter(new AppItemAdapter(
														AppListActivity.this,
														R.layout.app_list_item,
														apps));
						AppListActivity.this.loadingDialog.dismiss();
					}
				});
			}
		}.start();
	}
	
	private final class AppItemAdapter extends ArrayAdapter<App> {
		public AppItemAdapter(final Context context,
						final int textViewResourceId, final List<App> apps) {
			super(context, textViewResourceId, apps);
			this.mApps = apps;
			Collections.sort(this.mApps, new Comparator<App>() {
				@Override
				public final int compare(final App a1, final App a2) {
					return a1.name.compareTo(a2.name);
				}
			});
			this.li = (LayoutInflater) AppListActivity.this
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public View getView(final int position, final View convertView,
						final ViewGroup parent) {
			final View v = this.li.inflate(R.layout.app_list_item, null);
			final App a = this.mApps.get(position);
			((TextView) v.findViewById(R.id.appNameTextView)).setText(a.name);
			((ImageView) v.findViewById(R.id.appIconImageView))
							.setImageDrawable(a.icon);
			final CheckBox noInternetCheckBox = (CheckBox) v
							.findViewById(R.id.noInternetCheckBox), noLocationCheckBox = (CheckBox) v
							.findViewById(R.id.noLocationCheckBox);
			if (a.useNet) {
				noInternetCheckBox.setEnabled(true);
				noInternetCheckBox.setChecked(AppListActivity.this.noInternet
								.contains(a.packageName));
				noInternetCheckBox.setOnClickListener(new OnClickListener() {
					@Override
					public final void onClick(final View view) {
						if (noInternetCheckBox.isChecked()) {
							AppListActivity.this.noInternet.add(a.packageName);
						} else {
							AppListActivity.this.noInternet
											.remove(a.packageName);
						}
					}
				});
			} else {
				noInternetCheckBox.setVisibility(View.INVISIBLE);
			}
			if (a.useLoc) {
				noLocationCheckBox.setEnabled(true);
				noLocationCheckBox.setChecked(AppListActivity.this.noLocation
								.contains(a.packageName));
				noLocationCheckBox.setOnClickListener(new OnClickListener() {
					@Override
					public final void onClick(final View view) {
						if (noLocationCheckBox.isChecked()) {
							AppListActivity.this.noLocation.add(a.packageName);
						} else {
							AppListActivity.this.noLocation
											.remove(a.packageName);
						}
					}
				});
			} else {
				noLocationCheckBox.setVisibility(View.INVISIBLE);
			}
			return v;
		}
		
		private final LayoutInflater li;
		private final List<App> mApps;
	}
	
	private ListView appListView;
	private PolicyDb db;
	private ProgressDialog loadingDialog;
	private final Handler loadingHandler = new Handler();
	private List<String> noLocation, noInternet;
}