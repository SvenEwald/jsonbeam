package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface ActionLabel {

	interface Item {
		@JBRead("id")
		String getId();

		@JBRead("label")
		String getLabel();

		//		@JBRead("self")
		//		Item getSelf();
		//
		//		@JBRead("parent")
		//		ActionLabel getParent();
	}

	@JBExpect(strings = { "Open", "OpenNew", "ZoomIn", "ZoomOut", "OriginalView", "Quality", "Pause", "Mute", "Find", "FindAgain", "Copy", "CopyAgain", "CopySVG", "ViewSVG", "ViewSource", "SaveAs", "Help", "About" })
	@JBRead("menu.items[*].id")
	List<String> getAllIds();

	@JBExpect(strings = "null")
	@JBRead("menu.items[2]")
	String getEmptyItem();

	@JBRead("menu.items[0]")
	Item getFirstItem();

	@JBExpect(strings = "Open")
	default String getFirstItemId() {
		return getFirstItem().getId();
	}

	@JBExpect(strings = "SVG Viewer")
	@JBRead("menu.header")
	String getHeader();

	@JBExpect(strings = "SVG Viewer")
	@JBRead("menu.header")
	List<String> getHeaders2();

	@JBRead("menu.items[1]")
	Item getSecondItem();

	@JBExpect(strings = "Open New")
	default String getSecondItemLabel() {
		return getSecondItem().getLabel();
	}

	@JBExpect(strings = "OpenNew")
	default String getSecondtItemValue() {
		return getSecondItem().getId();
	}
}
