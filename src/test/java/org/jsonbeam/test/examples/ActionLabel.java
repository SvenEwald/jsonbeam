/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.annotations.JBRead;
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
