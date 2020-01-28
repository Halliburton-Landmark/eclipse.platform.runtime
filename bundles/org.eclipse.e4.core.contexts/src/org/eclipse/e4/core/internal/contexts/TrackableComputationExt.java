/*******************************************************************************
 * Copyright (c) 2009, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.core.internal.contexts;

import java.lang.ref.Reference;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.internal.contexts.EclipseContext.Scheduled;

public class TrackableComputationExt extends Computation {

	final private IEclipseContext originatingContext;
	final private RunAndTrack runnable;

	private ContextChangeEvent cachedEvent;

	public TrackableComputationExt(RunAndTrack runnable, IEclipseContext originatingContext) {
		this.runnable = runnable;
		this.originatingContext = originatingContext;
		init();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	protected int calcHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(originatingContext);
		return prime * result + Objects.hashCode(runnable);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackableComputationExt other = (TrackableComputationExt) obj;
		return Objects.equals(this.originatingContext, other.originatingContext)
				&& Objects.equals(this.runnable, other.runnable);
	}

	@Override
	public void handleInvalid(ContextChangeEvent event, Set<Scheduled> scheduledList) {
		//	don't call super - we keep the link unless uninjected / disposed
		int eventType = event.getEventType();
//		System.err.println(hashCode + " " + runnable + " " + event.getName() + " TrackableComputationExt handleInvalid " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				+ originatingContext
//				+ " " + eventType + " " + (event.getContext() == originatingContext));//$NON-NLS-1$ //$NON-NLS-2$
		if (eventType == ContextChangeEvent.INITIAL || eventType == ContextChangeEvent.DISPOSE
				|| eventType == ContextChangeEvent.REPARENTED) {
			// process structural changes immediately
			update(event);
		} else {
			// schedule processing
			scheduledList.add(new Scheduled(this, event));
		}
	}

	public boolean update(ContextChangeEvent event) {
		// is this a structural event?
		// structural changes: INITIAL, DISPOSE, UNINJECTED, are REPARENTED are always
		// processed immediately
		final int eventType = event.getEventType();
		final EclipseContext eventsContext = (EclipseContext) event.getContext();
		if (eventType == ContextChangeEvent.REPARENTED) {
			if (Arrays.stream(event.getArguments()).anyMatch(c -> c == originatingContext)) {
				System.err.println(this + " " + //$NON-NLS-1$
						originatingContext + " REPARENTED removeRAT from " + eventsContext); //$NON-NLS-1$
//				new Throwable().printStackTrace(System.err);
				eventsContext.removeRAT(this);
				cachedEvent = null;
				return false;
			}
		}
		if ((runnable instanceof RunAndTrackExt) && ((RunAndTrackExt) runnable).batchProcess()) {
			if ((eventType == ContextChangeEvent.ADDED) || (eventType == ContextChangeEvent.REMOVED)) {
				cachedEvent = event;
				eventsContext.addWaiting(this);
				return true;
			}
		}

		((EclipseContext) originatingContext).pushComputation(this);
		boolean result = true;
		try {
			if (cachedEvent != null) {
				if (runnable instanceof RunAndTrackExt) {
					result = ((RunAndTrackExt) runnable).update(event.getContext(), event.getEventType(), event.getArguments());
					if (eventType != ContextChangeEvent.DISPOSE && eventType != ContextChangeEvent.UNINJECTED)
						cachedEvent = null;
				} else {
					if (eventType != ContextChangeEvent.DISPOSE && eventType != ContextChangeEvent.UNINJECTED) {
						result = runnable.changed(originatingContext);
						cachedEvent = null;
					}
				}
			}
			if (eventType != ContextChangeEvent.UPDATE && eventType != ContextChangeEvent.REPARENTED) {
				if (runnable instanceof RunAndTrackExt)
					result = ((RunAndTrackExt) runnable).update(event.getContext(), event.getEventType(), event.getArguments());
				else {
					if (eventType != ContextChangeEvent.DISPOSE && eventType != ContextChangeEvent.UNINJECTED)
						result = runnable.changed(originatingContext);
				}
			}
		} finally {
			((EclipseContext) originatingContext).popComputation(this);
		}

		if (eventType == ContextChangeEvent.DISPOSE) {
			if (originatingContext == eventsContext) {
				System.err.println(this + " " + //$NON-NLS-1$
						originatingContext + " DISPOSE removeRAT " + event.getName()); //$NON-NLS-1$
//				new Throwable().printStackTrace(System.err);
				((EclipseContext) originatingContext).removeRAT(this);
				return false;
			}
		}
		if (!result) {
			if (eventsContext != originatingContext)
				System.err.println(this + " " + //$NON-NLS-1$
						event.getContext() + " removeRAT " + originatingContext + ' ' + event.getName()); //$NON-NLS-1$
			((EclipseContext) originatingContext).removeRAT(this);
		}
		return result;
	}

	@Override
	public String toString() {
		return runnable.toString();
	}

	public Reference<Object> getReference() {
		if (runnable instanceof RunAndTrackExt)
			return ((RunAndTrackExt) runnable).getReference();
		return null;
	}

}
