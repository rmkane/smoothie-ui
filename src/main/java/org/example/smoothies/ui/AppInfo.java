package org.example.smoothies.ui;

import lombok.experimental.UtilityClass;

import org.example.smoothies.SmoothieApp;

@UtilityClass
public class AppInfo {

	public static final String NAME = "Smoothie Maker";

	public static final String DESCRIPTION = "A desktop app for selecting ingredients and discovering which smoothies you can make.";

	public static String version() {
		String version = SmoothieApp.class.getPackage().getImplementationVersion();
		return version != null ? version : "development";
	}
}
