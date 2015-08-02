package org.jsonbeam.jsonprojector.projector;

import java.lang.reflect.Method;

import org.jsonbeam.index.keys.PathReferenceStack;
import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.jsonprojector.utils.ProjectionInterfaceHelper;

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
