package org.jsonbeam.jsonprojector.projector.intern;

public interface CanEvaluateOrProject {

	<T> T createProjection(Class<T> type);

}
