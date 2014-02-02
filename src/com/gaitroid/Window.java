package com.gaitroid;

import java.util.LinkedList;

public class Window {

	private LinkedList<Instance> window1;
	private LinkedList<Instance> window2;
	
	public Window() {
		window1 = new LinkedList<Instance>();
		window2 = new LinkedList<Instance>();
	}
	
	public LinkedList<Instance> getWindow1() {
		return this.window1;
	}
	
	public LinkedList<Instance> getWindow2() {
		return this.window2;
	}
}
