package com.puzzlesmentales.util;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;

import com.puzzlesmentales.R;

import java.util.List;

public class AndroidUtils {
	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
				packageManager.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	public static void setThemeFromPreferences(Context context) {
		SharedPreferences gameSettings = PreferenceManager.getDefaultSharedPreferences(context);
		String theme = gameSettings.getString("theme", "default");
		if (theme.equals("default")) {
			context.setTheme(R.style.Theme_Default);
		} else if (theme.equals("paperi")) {
			context.setTheme(R.style.Theme_PaperI);
		} else if (theme.equals("paperii")) {
			context.setTheme(R.style.Theme_PaperII);
		} else if (theme.equals("light")) {
			context.setTheme(R.style.Theme_Light);
        } else if (theme.equals("paperlighti")) {
            context.setTheme(R.style.Theme_PaperLightI);
        } else if (theme.equals("paperlightii")) {
            context.setTheme(R.style.Theme_PaperLightII);
		} else {
			context.setTheme(R.style.Theme_Default);
		}
	}

	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
