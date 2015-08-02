package org.jsonbeam.jsonprojector.projector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.io.Serializable;

import org.jsonbeam.jsonprojector.projector.intern.JBProjection;
import org.jsonbeam.jsonprojector.utils.ProjectionInterfaceHelper;

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
