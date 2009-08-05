/*******************************************************************************
 * Copyright (c) 2009 Mountainminds GmbH & Co. KG and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 * $Id: $
 *******************************************************************************/
package org.jacoco.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.ICoverageNode;

/**
 * A formatter that is composed from multiple other formatters. This can be used
 * to create more than one report format in one run.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */

public class MultiFormatter implements IReportFormatter {

	private final List<IReportFormatter> formatters = new ArrayList<IReportFormatter>();

	/**
	 * Adds the given formatter to the processing chain.
	 * 
	 * @param formatter
	 *            formatter to add
	 */
	public void add(final IReportFormatter formatter) {
		formatters.add(formatter);
	}

	public IReportVisitor createReportVisitor(final ICoverageNode session)
			throws IOException {
		final List<IReportVisitor> visitors = new ArrayList<IReportVisitor>();
		for (final IReportFormatter f : formatters) {
			visitors.add(f.createReportVisitor(session));
		}
		return new MultiVisitor(visitors);
	}

	private static class MultiVisitor implements IReportVisitor {

		private final List<IReportVisitor> visitors;

		MultiVisitor(final List<IReportVisitor> visitors) {
			this.visitors = visitors;
		}

		public IReportVisitor visitChild(final ICoverageNode node)
				throws IOException {
			final List<IReportVisitor> children = new ArrayList<IReportVisitor>();
			for (final IReportVisitor v : visitors) {
				children.add(v.visitChild(node));
			}
			return new MultiVisitor(children);
		}

		public void visitEnd(final ISourceFileLocator sourceFileLocator)
				throws IOException {
			for (final IReportVisitor v : visitors) {
				v.visitEnd(sourceFileLocator);
			}
		}
	}

}