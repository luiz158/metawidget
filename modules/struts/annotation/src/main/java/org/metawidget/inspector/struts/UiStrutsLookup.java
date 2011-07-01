// Metawidget
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package org.metawidget.inspector.struts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates the value returned by the field should belong to the set returned by the named JSP bean
 * and property (as used by Struts' <code>html:options</code>).
 *
 * @author Richard Kennard
 */

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD } )
public @interface UiStrutsLookup {

	/**
	 * Name of the bean containing the lookup. Equivalent to
	 * <code>org.apache.struts.taglib.OptionsTag.setName</code>
	 */

	String name();

	/**
	 * Name of the property to use to build the values collection. Equivalent to
	 * <code>org.apache.struts.taglib.OptionsTag.setProperty</code>
	 */

	String property();

	/**
	 * Name of the bean containing the labels lookup. Equivalent to
	 * <code>org.apache.struts.taglib.OptionsTag.setLabelName</code>
	 */

	String labelName() default "";

	/**
	 * Name of the property to use to build the labels collection. Equivalent to
	 * <code>org.apache.struts.taglib.OptionsTag.setLabelProperty</code>
	 */

	String labelProperty() default "";
}