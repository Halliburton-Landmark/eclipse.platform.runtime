package org.eclipse.core.runtime.model;

/*
 * Licensed Materials - Property of IBM,
 * WebSphere Studio Workbench
 * (c) Copyright IBM Corp 2000
 */

/**
 * A runtime library declared in a plug-in.  Libraries contribute elements to the search path.
 * These contributions are specified as a path to a directory or Jar file.  This path is always
 * considered to be relative to the containing plug-in.  
 * <p>
 * Libraries are typed.  The type is used to determine to which search path the library's
 * contribution should be added.  The valid types are: <code>CODE</code>, 
 * <code>RESOURCE</code> and <code>SOURCE</code>.  
 * </p>
 * <p>
 * This class may be instantiated, or further subclassed.
 * </p>
 */
public class LibraryModel extends PluginModelObject {

	// DTD properties (included in plug-in manifest)
	private String[] exports = null;
	private String type = CODE;
	private String source = null;

	// transient properties (not included in plug-in manifest)
	private boolean isExported = false;
	private boolean isFullyExported = false;

	/**
	 * Constant string (value "code") indicating the code library type.
	 */
	public static final String CODE = "code";
	
	/**
	 * Constant string (value "resource") indicating the resource library type.
	 */
	public static final String RESOURCE = "resource";
	
	/**
	 * Constant string (value "source") indicating the source library type.
	 */
	public static final String SOURCE = "source";
/**
 * Creates a new library model in which all fields
 * are <code>null</code>.
 */
public LibraryModel() {}
/**
 * Returns this library's export mask.
 *
 * @return this library's export mask or <code>null</code>
 */
public String[] getExports() {
	return exports;
}
/**
 * Returns the path to this library's source or <code>null</code>.  
 *
 * @return the path to this library's source.  May be <code>null</code>.
 */
public String getSource() {
	return source;
}
/**
 * Returns this library's type.  
 *
 * @return the type of this library.  The valid types are: <code>CODE</code>, <code>RESOURCE</code>, 
 * <code>SOURCE</code>.
 * @see #CODE
 * @see #RESOURCE
 * @see #SOURCE
 */
public String getType() {
	return type;
}
/**
 * Returns whether or not any of the code in this library is exported.
 *
 * @return whether or not any of the code in this library represents is exported
 */
public boolean isExported() {
	return isExported;
}
/**
 * Returns whether or not all of the code in this library is exported.
 *
 * @return whether or not all of the code in this library is exported
 */
public boolean isFullyExported() {
	return isFullyExported;
}
/**
 * Sets this library's export mask.
 * This object must not be read-only.
 *
 * @param value this library's export mask.  May be <code>null</code>.
 */
public void setExports(String[] value) {
	assertIsWriteable();
	exports = value;
	if (value == null) {
		isExported = false;
		isFullyExported = false;
	} else {
		for (int i = 0; i < value.length; i++) {
			if (!value[i].equals(""))
				isExported = true;
			if (value[i].equals("*"))
				isFullyExported = true;
		}
	}
}
/**
 * Sets the location of this library's source.  This method has no effect if
 * this library is of type <code>RESOURCE</code>.
 * This object must not be read-only.
 *
 * @param value the location of this library's source.  May be <code>null</code>.
 */
public void setSource(String value) {
	assertIsWriteable();
	// cannot set the source for a library of type resource.
	if (type != null && type.equals(RESOURCE))
		return;
	source = value;
}
/**
 * Sets this library's type. The valid types are: <code>CODE</code>, <code>RESOURCE</code>, 
 * <code>SOURCE</code>.  Setting this library to type <code>RESOURCE</code>
 * clears the <code>source</code> field of this library.  The given type value is canonicalized
 * before being set.
 * This object must not be read-only.
 *
 * @param value the type of this library.
 * @see #CODE
 * @see #RESOURCE
 * @see #SOURCE
 */
public void setType(String value) {
	assertIsWriteable();
	type = value.toLowerCase();
	// if this is to be a resource library, there should not be a source value.
	if (type.equals(RESOURCE))
		source = null;
}
}
