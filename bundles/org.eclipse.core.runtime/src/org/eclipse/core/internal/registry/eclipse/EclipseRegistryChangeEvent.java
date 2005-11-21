/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.internal.registry.eclipse;

import java.util.Map;
import org.eclipse.core.internal.registry.RegistryChangeEvent;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IRegistryChangeEvent;

/**
 * A registry change event implementation provided for backward compatibility.
 * 
 * For general use consider RegistryChangeEvent.
 */
public final class EclipseRegistryChangeEvent implements IRegistryChangeEvent {

	private org.eclipse.equinox.registry.IRegistryChangeEvent theEquinoxHandle;

	public EclipseRegistryChangeEvent(org.eclipse.equinox.registry.IRegistryChangeEvent event) {
		theEquinoxHandle = event;
	}

	public EclipseRegistryChangeEvent(Map deltas, String filter) {
		theEquinoxHandle = new RegistryChangeEvent(deltas, filter);
	}

	public IExtensionDelta[] getExtensionDeltas() {
		return EclipseRegistryAdaptor.adapt(theEquinoxHandle.getExtensionDeltas());
	}

	public IExtensionDelta[] getExtensionDeltas(String hostName) {
		return EclipseRegistryAdaptor.adapt(theEquinoxHandle.getExtensionDeltas(hostName));
	}

	public IExtensionDelta[] getExtensionDeltas(String hostName, String extensionPoint) {
		return EclipseRegistryAdaptor.adapt(theEquinoxHandle.getExtensionDeltas(hostName, extensionPoint));
	}

	public IExtensionDelta getExtensionDelta(String hostName, String extensionPoint, String extension) {
		return EclipseRegistryAdaptor.adapt(theEquinoxHandle.getExtensionDelta(hostName, extensionPoint, extension));
	}

	/**
	 * Unwraps handle to obtain underlying Equinox handle form Eclipse handle
	 * @return - Equinox handle 
	 */
	public org.eclipse.equinox.registry.IRegistryChangeEvent getInternalHandle() {
		return theEquinoxHandle;
	}
}
