package com.nagarro.driven.client.ui.api;

import java.util.Set;

public interface WindowFrame {

	void close();

	String getTitle();

	void maximize();

	void fullscreen();

	Dimension getSize();

	void setSize(Dimension size);

	String getWindowHandle();

	void windowHandleMaximize();

	void windowHandleSetSize(String currentWindowHandle, Dimension dim);

	void windowHandleSetPosition(String currentWindowHandle, Location location);

	Location windowHandleGetPosition(String currentWindowHandle);

	Set<String> getWindowHandleSet();

	int getWindowHandleSize(Set<String> windowHandlesSet);

	void switchToWindowHandle(int window);

	void closeAllwindowExceptTheCurrentOne();
}
