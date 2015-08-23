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
package org.jsonbeam.evaluation;

import java.util.function.Supplier;

import org.jsonbeam.JPathEvaluator;
import org.jsonbeam.io.CharacterSource;
import org.jsonbeam.jsonprojector.projector.BCJSONProjector;
import org.jsonbeam.jsonprojector.projector.intern.CanEvaluateOrProject;

/**
 * @author Sven
 */
public class DefaultEvaluator implements CanEvaluateOrProject {

	private final Supplier<CharacterSource> docProvider;
	private final BCJSONProjector projector;

	public DefaultEvaluator(final BCJSONProjector projector, final Supplier<CharacterSource> docProvider) {
		this.docProvider = docProvider;
		this.projector = projector;
	}

	@Override
	public JPathEvaluator evalJPath(final String jpath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T createProjection(final Class<T> type) {
		CharacterSource source = docProvider.get();
		try (AutoCloseable handle = source.ioHandle()) {
			return projector.projectCharacterSource(source, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
