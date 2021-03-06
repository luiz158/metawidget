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

package org.metawidget.statically.html.widgetprocessor;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.Map;

import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.html.StaticHtmlMetawidget;
import org.metawidget.statically.html.widgetbuilder.IdHolder;
import org.metawidget.widgetprocessor.iface.WidgetProcessor;

/**
 * WidgetProcessor that adds an 'id' attribute to a StaticXmlWidget.
 *
 * @author Richard Kennard
 */

public class IdProcessor
	implements WidgetProcessor<StaticXmlWidget, StaticHtmlMetawidget> {

	//
	// Public methods
	//

	public StaticXmlWidget processWidget( StaticXmlWidget widget, String elementName, Map<String, String> attributes, StaticHtmlMetawidget metawidget ) {

		if ( widget instanceof IdHolder ) {

			String id = attributes.get( NAME );

			if ( id == null ) {
				id = metawidget.getId();
			} else if ( metawidget.getId() != null ) {
				id = metawidget.getId() + '-' + id;
			}

			((IdHolder) widget).setId( id );
		}

		return widget;
	}
}
