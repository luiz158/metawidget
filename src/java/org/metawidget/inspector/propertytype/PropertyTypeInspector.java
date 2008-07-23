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

package org.metawidget.inspector.propertytype;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.propertytype.PropertyTypeInspectionResultConstants.*;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.metawidget.inspector.impl.BasePropertyInspector;
import org.metawidget.inspector.impl.BasePropertyInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.w3c.dom.Element;

/**
 * Inspector to look for types of properties.
 * <p>
 * If the actual type of the property's object is a subtype of the declared type, both the actual
 * and the declared type are returned.
 * <p>
 * The properties are returned in the order defined by their <code>PropertyStyle</code>. For
 * <code>JavaBeanPropertyStyle</code> (the default) this is 'alphabetical by name'. Most clients
 * will want to refine this by using, say, <code>UiComesAfter</code> and
 * MetawidgetAnnotationInspector.
 *
 * @author Richard Kennard
 */

public class PropertyTypeInspector
	extends BasePropertyInspector
{
	//
	//
	// Constructor
	//
	//

	public PropertyTypeInspector()
	{
		this( new BasePropertyInspectorConfig() );
	}

	public PropertyTypeInspector( BasePropertyInspectorConfig config )
	{
		super( config );
	}

	//
	//
	// Protected methods
	//
	//

	@Override
	protected Map<String, String> inspect( Property property, Object toInspect )
		throws Exception
	{
		Map<String, String> attributes = CollectionUtils.newHashMap();

		// ...type...

		Class<?> propertyClass = property.getType();

		// ...(may be polymorphic)...

		Class<?> actualClass = null;

		if ( !Modifier.isFinal( propertyClass.getModifiers() ) )
		{
			if ( property.isReadable() )
			{
				try
				{
					Object actual = property.read( toInspect );

					if ( actual != null )
						actualClass = ClassUtils.getUnproxiedClass( actual.getClass(), mPatternProxy );
				}
				catch ( Throwable t )
				{
					// By definition, a 'getter' method should not affect the state
					// of the object. However, sometimes a getter's implementation
					// may fail if an object is not in a certain state (eg. JSF's
					// DataModel.getRowData) - in which case fall back to property
				}
			}
		}

		if ( actualClass != null && !propertyClass.equals( actualClass ) )
		{
			attributes.put( DECLARED_CLASS, propertyClass.getName() );
			attributes.put( TYPE, actualClass.getName() );
		}
		else
		{
			attributes.put( TYPE, propertyClass.getName() );
		}

		// ...(no-setter/no-getter)...

		if ( !property.isWritable() )
		{
			attributes.put( NO_SETTER, TRUE );
			attributes.put( READ_ONLY, TRUE );
		}

		if ( !property.isReadable() )
			attributes.put( NO_GETTER, TRUE );

		return attributes;
	}

	/**
	 * Overriden to return <code>false<code>.
	 * <p>
	 * <code>PropertyTypeInspector</code> always returns an XML document, even if
	 * just to convey the <code>type</code> attribute of the top-level <code>entity</code> element.
	 */

	@Override
	protected boolean isInspectionEmpty( Element elementEntity, Map<String, String> parentAttributes )
	{
		return false;
	}
}
