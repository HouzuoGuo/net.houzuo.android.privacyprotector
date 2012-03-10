package net.houzuo.android.privacyprotector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public final class PolicyDb {
	public PolicyDb(final Context context) {
		this.context = context;
		final File noInternet = context
						.getFileStreamPath(PolicyDb.noInternetFileName);
		final File noLocation = context
						.getFileStreamPath(PolicyDb.noLocationFileName);
		try {
			if (!noInternet.exists()) {
				noInternet.createNewFile();
			}
			if (!noLocation.exists()) {
				noLocation.createNewFile();
			}
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public List<String> readNoInternet() {
		return this.read(PolicyDb.noInternetFileName);
	}
	
	public List<String> readNoLocation() {
		return this.read(PolicyDb.noLocationFileName);
	}
	
	public void writeNoInternet(final List<String> processNames) {
		this.write(PolicyDb.noInternetFileName, processNames);
	}
	
	public void writeNoLocation(final List<String> processNames) {
		this.write(PolicyDb.noLocationFileName, processNames);
	}
	
	private List<String> read(final String fileName) {
		final List<String> list = new ArrayList<String>(150);
		FileInputStream fis = null;
		try {
			fis = this.context.openFileInput(fileName);
			final StringBuilder sb = new StringBuilder();
			int ch;
			while ((ch = fis.read()) != -1) {
				sb.append((char) ch);
			}
			final String[] splited = sb.toString().split("\\|");
			for (int i = 0; i < splited.length; ++i) {
				list.add(splited[i]);
			}
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (final IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		return list;
	}
	
	private void write(final String fileName, final List<String> lines) {
		FileOutputStream fos = null;
		try {
			fos = this.context.openFileOutput(fileName, Context.MODE_PRIVATE);
			final StringBuilder sb = new StringBuilder();
			for (final String s : lines) {
				sb.append(s).append('|');
			}
			fos.write(sb.toString().getBytes());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}
	
	private final static String noInternetFileName = "noInternet";
	private final static String noLocationFileName = "noLocation";
	private final Context context;
}
