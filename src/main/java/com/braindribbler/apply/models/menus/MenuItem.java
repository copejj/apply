package com.braindribbler.apply.models.menus;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {
	private String href;
	private String text;
	private String cssClass;

	private List<MenuItem> children = new ArrayList<>();

	public MenuItem(String href, String text) {
		this.href = href;
		this.text = text;
		this.cssClass = "";
	}

	public MenuItem(String href, String text, String cssClass) {
		this.href = href;
		this.text = text;
		this.cssClass = cssClass;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public List<MenuItem> getChildren() {
		return children;
	}

	public void setChildren(List<MenuItem> children) {
		this.children = children;
	}

	public void addChild(MenuItem child) {
		this.children.add(child);
	}
}
