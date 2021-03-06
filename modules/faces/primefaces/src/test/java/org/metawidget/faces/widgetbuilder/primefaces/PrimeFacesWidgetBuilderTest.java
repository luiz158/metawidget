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

package org.metawidget.faces.widgetbuilder.primefaces;

import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.FacesInspectionResultConstants.*;

import java.awt.Color;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.metawidget.faces.FacesMetawidgetTests.MockFacesContext;
import org.metawidget.faces.component.UIStub;
import org.metawidget.faces.component.html.HtmlMetawidget;
import org.metawidget.faces.component.html.widgetbuilder.primefaces.PrimeFacesWidgetBuilder;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.util.CollectionUtils;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.component.colorpicker.ColorPicker;
import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.slider.Slider;
import org.primefaces.component.spinner.Spinner;

/**
 * @author Richard Kennard
 */

public class PrimeFacesWidgetBuilderTest
	extends TestCase {

	//
	// Private members
	//

	private FacesContext	mContext;

	//
	// Public methods
	//

	public void testPrimeFacesWidgetBuilder()
		throws Exception {

		PrimeFacesWidgetBuilder widgetBuilder = new PrimeFacesWidgetBuilder();

		// Read-only pass throughs

		Map<String, String> attributes = CollectionUtils.newHashMap();
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.put( READ_ONLY, TRUE );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.put( LOOKUP, TRUE );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.remove( LOOKUP );
		attributes.put( FACES_LOOKUP, "#{true}" );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.remove( FACES_LOOKUP );
		attributes.put( HIDDEN, TRUE );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.remove( HIDDEN );
		attributes.put( TYPE, "foo" );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );

		// Active pass throughs

		attributes.remove( READ_ONLY );
		attributes.put( HIDDEN, TRUE );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.remove( HIDDEN );
		attributes.put( TYPE, "foo" );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
		attributes.remove( HIDDEN );

		// Action

		attributes.put( NAME, "Press Me" );
		HtmlMetawidget metawidget = new HtmlMetawidget();
		CommandButton button = (CommandButton) widgetBuilder.buildWidget( ACTION, attributes, metawidget );
		assertEquals( "Press Me", button.getValue() );
		assertTrue( !button.isAjax() );
		attributes.remove( ACTION );

		// Faces lookup

		attributes.put( FACES_LOOKUP, "#{foo.bar}" );
		assertTrue( widgetBuilder.buildWidget( PROPERTY, attributes, null ) instanceof SelectOneMenu );
		attributes.remove( FACES_LOOKUP );

		// Lookup

		attributes.put( TYPE, String.class.getName() );
		attributes.put( LOOKUP, "Foo, Bar, Baz" );
		metawidget.setInspector( new PropertyTypeInspector() );
		assertTrue( widgetBuilder.buildWidget( PROPERTY, attributes, metawidget ) instanceof SelectOneMenu );
		attributes.remove( LOOKUP );

		// Sliders

		attributes.put( TYPE, int.class.getName() );
		attributes.put( MINIMUM_VALUE, "1" );
		attributes.put( MAXIMUM_VALUE, "1024" );
		UIStub stub = (UIStub) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertTrue( stub.getChildren().get( 0 ) instanceof HtmlInputText );
		Slider slider = (Slider) stub.getChildren().get( 1 );
		assertEquals( 1, slider.getMinValue() );
		assertEquals( 1024, slider.getMaxValue() );

		attributes.put( TYPE, Long.class.getName() );
		attributes.put( MINIMUM_VALUE, "2" );
		attributes.put( MAXIMUM_VALUE, "1023" );
		stub = (UIStub) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertTrue( stub.getChildren().get( 0 ) instanceof HtmlInputText );
		slider = (Slider) stub.getChildren().get( 1 );
		assertEquals( 2, slider.getMinValue() );
		assertEquals( 1023, slider.getMaxValue() );

		// Spinners

		attributes.put( TYPE, int.class.getName() );
		attributes.put( MAXIMUM_VALUE, "" );
		Spinner spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( 2d, spinner.getMin() );
		attributes.put( MAXIMUM_VALUE, "1024" );

		attributes.put( MINIMUM_VALUE, "" );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( 1024d, spinner.getMax() );

		// (lower bound)

		attributes.put( TYPE, byte.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Byte.MIN_VALUE, spinner.getMin() );

		attributes.put( TYPE, short.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Short.MIN_VALUE, spinner.getMin() );

		attributes.put( TYPE, int.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Integer.MIN_VALUE, spinner.getMin() );

		attributes.put( TYPE, long.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Long.MIN_VALUE, spinner.getMin() );

		attributes.put( TYPE, float.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) -Float.MAX_VALUE, spinner.getMin() );

		attributes.put( TYPE, double.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( -Double.MAX_VALUE, spinner.getMin() );

		// (upper bound)

		attributes.put( MAXIMUM_VALUE, "" );

		attributes.put( TYPE, byte.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Byte.MAX_VALUE, spinner.getMax() );

		attributes.put( TYPE, short.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Short.MAX_VALUE, spinner.getMax() );

		attributes.put( TYPE, int.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Integer.MAX_VALUE, spinner.getMax() );

		attributes.put( TYPE, long.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Long.MAX_VALUE, spinner.getMax() );

		attributes.put( TYPE, float.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( (double) Float.MAX_VALUE, spinner.getMax() );

		attributes.put( TYPE, double.class.getName() );
		spinner = (Spinner) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( Double.MAX_VALUE, spinner.getMax() );

		// Calendars

		attributes.put( TYPE, Date.class.getName() );
		attributes.put( DATETIME_PATTERN, "dd-MM-yyyy" );
		attributes.put( LOCALE, "en-AU" );
		attributes.put( TIME_ZONE, "Australia/Sydney" );
		Calendar calendar = (Calendar) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertEquals( "dd-MM-yyyy", calendar.getPattern() );
		assertEquals( new Locale( "en-AU" ), calendar.getLocale() );
		assertEquals( TimeZone.getTimeZone( "Australia/Sydney" ), calendar.getTimeZone() );

		// ColorPickers. Note org.primefaces.component.ColorPickerRenderer does *not* support
		// java.awt.Color (http://forum.primefaces.org/viewtopic.php?t=21593) so it isn't much good
		// to us here

		attributes.put( TYPE, Color.class.getName() );
		assertEquals( null, widgetBuilder.buildWidget( PROPERTY, attributes, null ) );
	}

	//
	// Protected methods
	//

	@Override
	protected void setUp()
		throws Exception {

		super.setUp();

		mContext = new MockPrimeFacesFacesContext();
	}

	@Override
	protected void tearDown()
		throws Exception {

		super.tearDown();

		mContext.release();
	}

	//
	// Inner class
	//

	protected static class MockPrimeFacesFacesContext
		extends MockFacesContext {

		//
		// Protected methods
		//

		@Override
		public UIComponent createComponent( String componentName )
			throws FacesException {

			if ( Slider.COMPONENT_TYPE.equals( componentName ) ) {
				return new Slider();
			}

			if ( Spinner.COMPONENT_TYPE.equals( componentName ) ) {
				return new Spinner();
			}

			if ( Calendar.COMPONENT_TYPE.equals( componentName ) ) {
				return new Calendar();
			}

			if ( ColorPicker.COMPONENT_TYPE.equals( componentName ) ) {
				return new ColorPicker();
			}

			if ( SelectOneMenu.COMPONENT_TYPE.equals( componentName ) ) {
				return new SelectOneMenu();
			}

			if ( CommandButton.COMPONENT_TYPE.equals( componentName ) ) {
				return new CommandButton();
			}

			return super.createComponent( componentName );
		}
	}
}
