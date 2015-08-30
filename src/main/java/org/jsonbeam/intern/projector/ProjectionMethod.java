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
package org.jsonbeam.intern.projector;

import java.lang.reflect.Method;

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.intern.index.keys.PathReferenceStack;
import org.jsonbeam.intern.utils.ProjectionInterfaceHelper;

public class ProjectionMethod {

	private final String path;
	private final Class<?> returnType;
	private final boolean returnsSubProjection;
	private final PathReferenceStack pathReferenceStack;

	public ProjectionMethod(final Method m) {
		this.path = m.getAnnotation(JBRead.class).value();
		pathReferenceStack = PathReferenceStack.parse(path);
		this.returnType = ProjectionInterfaceHelper.findTargetComponentType(m);
		this.returnsSubProjection = ProjectionInterfaceHelper.isProjectionInterface(returnType);
	}

	public String getPath() {
		return path;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public boolean returnsSubProjection() {
		return returnsSubProjection;
	}

	public PathReferenceStack getPathReferenceStack() {
		return pathReferenceStack;
	}
}
