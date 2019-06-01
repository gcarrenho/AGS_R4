package com.ags.guideme;

import java.io.File;

import com.ags.guideme.db.SQLiteAPI;

public interface ClientContext {
	
	public String getString(int resId, Object... args);
	
	public File getAppPath(String extend);
	
	public void showShortToastMessage(int msgId, Object... args);
	
	public void showToastMessage(int msgId, Object... args);
	
	public void showToastMessage(String msg);
	
	public SQLiteAPI getSQLiteAPI();
	
	public void runInUIThread(Runnable run);

	public void runInUIThread(Runnable run, long delay);
}
