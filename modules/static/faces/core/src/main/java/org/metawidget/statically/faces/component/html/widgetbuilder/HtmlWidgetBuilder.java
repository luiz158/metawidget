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

package org.metawidget.statically.faces.component.html.widgetbuilder;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.StaticStub;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.WidgetBuilderUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Richard Kennard
 */

public class HtmlWidgetBuilder
	implements WidgetBuilder<StaticWidget, StaticMetawidget> {

	//
	// Private statics
	//

	private final static String	MAX_LENGTH					= "maxLength";

	//
	// Public methods
	//

	public StaticWidget buildWidget( String elementName, Map<String, String> attributes, StaticMetawidget metawidget ) {

		// Hidden

		if ( TRUE.equals( attributes.get( HIDDEN ) ) ) {
			return new StaticStub();
		}

		// Action

		if ( ACTION.equals( elementName ) ) {
			return new StaticStub();
		}

		// Faces Lookups

		String facesLookup = attributes.get( FACES_LOOKUP );

		if ( facesLookup != null && !"".equals( facesLookup ) ) {
			HtmlSelectOneMenu select = new HtmlSelectOneMenu();
			addSelectItems( select, facesLookup, attributes );
			return select;
		}

		String type = WidgetBuilderUtils.getActualClassOrType( attributes );

		// If no type, fail gracefully with a text box

		if ( type == null ) {
			return createHtmlInputText( attributes );
		}

		// Lookup the Class

		Class<?> clazz = ClassUtils.niceForName( type );

		// Support mandatory Booleans (can be rendered as a checkbox, even though they have a
		// Lookup)

		if ( Boolean.class.equals( clazz ) && TRUE.equals( attributes.get( REQUIRED ) ) ) {
			return new HtmlSelectBooleanCheckbox();
		}

		// Lookups

		String lookup = attributes.get( LOOKUP );

		if ( lookup != null && !"".equals( lookup ) ) {
			HtmlSelectOneMenu select = new HtmlSelectOneMenu();
			addSelectItems( select, CollectionUtils.fromString( lookup ), CollectionUtils.fromString( attributes.get( LOOKUP_LABELS ) ), attributes );

			return select;
		}

		if ( clazz != null ) {
			// booleans

			if ( boolean.class.equals( clazz ) ) {
				return new HtmlSelectBooleanCheckbox();
			}

			// Primitives

			if ( clazz.isPrimitive() ) {
				return createHtmlInputText( attributes );
			}

			// String

			if ( String.class.equals( clazz ) ) {
				if ( TRUE.equals( attributes.get( LARGE ) ) ) {
					return new HtmlInputTextarea();
				}

				if ( TRUE.equals( attributes.get( MASKED ) ) ) {
					HtmlInputSecret inputSecret = new HtmlInputSecret();
					inputSecret.putAttribute( MAX_LENGTH, attributes.get( MAXIMUM_LENGTH ) );
					return inputSecret;
				}

				return createHtmlInputText( attributes );
			}

			// Character

			if ( Character.class.equals( clazz ) ) {
				HtmlInputText inputText = new HtmlInputText();
				inputText.putAttribute( MAX_LENGTH, "1" );
				return inputText;
			}

			// Dates

			if ( Date.class.equals( clazz ) ) {
				return createHtmlInputText( attributes );
			}

			// Numbers

			if ( Number.class.isAssignableFrom( clazz ) ) {
				return createHtmlInputText( attributes );
			}

			// Supported Collections

			if ( List.class.isAssignableFrom( clazz ) /* || DataModel.class.isAssignableFrom( clazz ) */|| clazz.isArray() ) {
				return createDataTableComponent( clazz, attributes, metawidget );
			}

			if ( Collection.class.isAssignableFrom( clazz ) ) {
				return new StaticStub();
			}
		}

		// Not simple, but don't expand

		if ( TRUE.equals( attributes.get( DONT_EXPAND ) ) ) {
			return createHtmlInputText( attributes );
		}

		// Not simple

		return null;
	}

	//
	// Protected methods
	//

	/**
	 * Create a UIColumn component for the given attributes.
	 * <p>
	 * Clients can override this method to modify the column contents. For example, to place a link
	 * around the text.
	 *
	 * @return the UIColumn, or null to suppress the column
	 */

	protected StaticXmlWidget createColumnComponent( String dataTableVar, String elementName, Map<String, String> attributes, StaticMetawidget metawidget ) {

		HtmlColumn column = new HtmlColumn();

		// Make the column contents...

		HtmlOutputText columnText = new HtmlOutputText();

		String valueExpression = dataTableVar;
		if ( !ENTITY.equals( elementName ) ) {
			valueExpression += StringUtils.SEPARATOR_DOT_CHAR + attributes.get( NAME );
		}
		columnText.putAttribute( "value", StaticFacesUtils.wrapExpression( valueExpression ) );
		column.getChildren().add( columnText );

		// ...with a localized header

		HtmlOutputText headerText = new HtmlOutputText();
		headerText.putAttribute( "value", metawidget.getLabelString( attributes ) );
		Facet headerFacet = new Facet();
		headerFacet.putAttribute( "name", "header" );
		headerFacet.getChildren().add( headerText );
		column.getChildren().add( 0, headerFacet );

		return column;
	}

	//
	// Private methods
	//

	private HtmlInputText createHtmlInputText( Map<String, String> attributes ) {

		HtmlInputText inputText = new HtmlInputText();
		inputText.putAttribute( MAX_LENGTH, attributes.get( MAXIMUM_LENGTH ) );

		return inputText;
	}

	private void addSelectItems( HtmlSelectOneMenu select, List<String> values, List<String> labels, Map<String, String> attributes ) {

		if ( values == null ) {
			return;
		}

		// Empty option

		if ( WidgetBuilderUtils.needsEmptyLookupItem( attributes ) ) {
			addSelectItem( select, "", null );
		}

		// Add the select items

		for ( int loop = 0, length = values.size(); loop < length; loop++ ) {
			String value = values.get( loop );
			String label = null;

			if ( labels != null && !labels.isEmpty() ) {
				label = labels.get( loop );
			}

			addSelectItem( select, value, label );
		}
	}

	private void addSelectItem( HtmlSelectOneMenu select, String value, String label ) {

		SelectItem selectItem = new SelectItem();
		selectItem.putAttribute( "itemLabel", label );
		selectItem.putAttribute( "itemValue", value );

		select.getChildren().add( selectItem );
	}

	private void addSelectItems( HtmlSelectOneMenu select, String valueExpression, Map<String, String> attributes ) {

		// Empty option

		if ( WidgetBuilderUtils.needsEmptyLookupItem( attributes ) ) {
			addSelectItem( select, "", null );
		}

		// Add the select items

		SelectItems selectItems = new SelectItems();
		selectItems.putAttribute( "value", valueExpression );

		select.getChildren().add( selectItems );
	}

	private StaticXmlWidget createDataTableComponent( Class<?> clazz, Map<String, String> attributes, StaticMetawidget metawidget ) {

		HtmlDataTable dataTable = new HtmlDataTable();
		String dataTableVar = "_item";
		dataTable.putAttribute( "var", dataTableVar );

		// Inspect component type

		String componentType;

		if ( clazz.isArray() ) {
			componentType = clazz.getComponentType().getName();
		} else {
			componentType = attributes.get( PARAMETERIZED_TYPE );
		}

		String inspectedType = null;

		if ( componentType != null ) {
			inspectedType = metawidget.inspect( null, componentType, (String[]) null );
		}

		// If there is no type...

		if ( inspectedType == null ) {
			// ...resort to a single column table...

			StaticXmlWidget column = createColumnComponent( dataTableVar, ENTITY, attributes, metawidget );

			if ( column != null ) {
				dataTable.getChildren().add( column );
			}
		}

		// ...otherwise, iterate over the component type...

		else {
			Element root = XmlUtils.documentFromString( inspectedType ).getDocumentElement();
			NodeList elements = root.getFirstChild().getChildNodes();

			// ...and try to add columns for just the 'required' fields...

			addColumnComponents( elements, dataTable, metawidget, true );

			// ...but, failing that, add columns for every field

			if ( dataTable.getChildren().isEmpty() ) {
				addColumnComponents( elements, dataTable, metawidget, false );
			}
		}

		return dataTable;
	}

	private void addColumnComponents( NodeList elements, HtmlDataTable dataTable, StaticMetawidget metawidget, boolean onlyRequired ) {

		// For each property...

		for ( int loop = 0, length = elements.getLength(); loop < length; loop++ ) {
			Node node = elements.item( loop );

			if ( !( node instanceof Element ) ) {
				continue;
			}

			Element element = (Element) node;

			// ...(not action)...

			if ( ACTION.equals( element.getNodeName() ) ) {
				continue;
			}

			// ...that is visible...

			if ( TRUE.equals( element.getAttribute( HIDDEN ) ) ) {
				continue;
			}

			// ...and is required...
			//
			// Note: this is a controversial choice. Our logic is that a) we need to limit
			// the number of columns somehow, and b) displaying all the required fields should
			// be enough to uniquely identify the row to the user. However, users may wish
			// to override this default behaviour

			if ( onlyRequired && !TRUE.equals( element.getAttribute( REQUIRED ) ) ) {
				continue;
			}

			// ...add a column

			StaticXmlWidget column = createColumnComponent( dataTable.getAttribute( "var" ), PROPERTY, XmlUtils.getAttributesAsMap( element ), metawidget );

			if ( column == null ) {
				continue;
			}

			dataTable.getChildren().add( column );
		}
	}
}
