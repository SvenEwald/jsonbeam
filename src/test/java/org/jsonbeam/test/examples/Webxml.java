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

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Webxml {

	@JBRead("web-app.servlet[0].init-param.useJSP")
	@JBExpect(booleans = false)
	public boolean getBoolAsPrimitive();

	@JBRead("web-app.servlet[0].init-param.useJSP")
	@JBExpect(booleans = false)
	public Boolean getBoolAsObject();

	@JBRead("web-app.servlet[4].init-param.betaServer")
	@JBExpect(booleans = true)
	public boolean getBoolAsPrimitive2();

	@JBRead("web-app.servlet[4].init-param.betaServer")
	@JBExpect(booleans = true)
	public Boolean getBoolAsObject2();

	@JBRead("web-app.xxx")
	@JBExpect(booleans = false)
	public boolean getNonexistingBoolAsPrimitive();

	@JBRead("web-app.xxx")
	@JBExpect()
	public Boolean getNonexistingBoolAsObject();

	@JBRead("web-app.servlet[0].init-param.cachePackageTagsTrack")
	@JBExpect(ints = 200)
	public int getIntAsPrimitive();

	@JBRead("web-app.servlet[0].init-param.cachePackageTagsTrack")
	@JBExpect(ints = 200)
	public Integer getIntAsObject();

	@JBRead("web-app.servlet[0].init-param.dataStoreInitConns")
	@JBExpect(bytes = 10)
	public byte getByteAsPrimitive();

	@JBRead("web-app.servlet[0].init-param.dataStoreInitConns")
	@JBExpect(bytes = 10)
	public Byte getByteAsObject();

	@JBRead("web-app.servlet[0].init-param.dataStoreInitConns")
	@JBExpect(shorts = 10)
	public short getShortAsPrimitive();

	@JBRead("web-app.servlet[0].init-param.dataStoreInitConns")
	@JBExpect(shorts = 10)
	public Short getShortAsObject();

	@JBRead("web-app.servlet[0].init-param.maxUrlLength")
	@JBExpect(longs = 500)
	public long getLongAsPrimitive();

	@JBRead("web-app.servlet[0].init-param.maxUrlLength")
	@JBExpect(longs = 500)
	public Long getLongAsObject();

}
