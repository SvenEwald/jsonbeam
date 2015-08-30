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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsonbeam.intern.utils.ProjectionInterfaceHelper;

import java.io.Serializable;

public class ProjectionType {
	//private final Class<?> projectionInterface;
	private final List<ProjectionMethod> projectionMethods;

	//private final List<Method> publicMethods;
	private final Class<?>[] implementedInterfaces;

	public ProjectionType(final Class<?> projectionInterface) {
		ProjectionInterfaceHelper.checkProjectionInterfaceType(projectionInterface).ifPresent(error -> {
			throw error;
		});

		//this.projectionInterface = projectionInterface;
		this.projectionMethods = fillProjectionMethods(projectionInterface);
		//this.publicMethods = Arrays.stream(projectionInterface.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).collect(Collectors.toList());

		final Set<Class<?>> interfaces = new HashSet<Class<?>>();
		interfaces.add(projectionInterface);
		interfaces.add(JBProjection.class);
		interfaces.add(Serializable.class);
		this.implementedInterfaces = interfaces.toArray(new Class<?>[interfaces.size()]);
	}

	private static List<ProjectionMethod> fillProjectionMethods(final Class<?> projectionInterface) {
		return ProjectionInterfaceHelper.getProjectionMethods(projectionInterface).map(ProjectionMethod::new).collect(Collectors.toList());
	}

	public List<ProjectionMethod> getProjectionsMethods() {
		return projectionMethods;
	}

	public Class<?>[] getImplementedInterfaces() {
		return implementedInterfaces;
	}
}
