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

package org.metawidget.test.faces.widgetbuilder.icefaces;

import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.Date;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import junit.framework.TestCase;

import org.metawidget.faces.component.html.widgetbuilder.icefaces.IceFacesWidgetBuilder;
import org.metawidget.test.faces.FacesMetawidgetTests.MockFacesContext;
import org.metawidget.util.CollectionUtils;

import com.icesoft.faces.component.selectinputdate.SelectInputDate;

/**
 * @author Richard Kennard
 */

public class IceFacesWidgetBuilderTest
	extends TestCase
{
	//
	// Private members
	//

	private FacesContext mContext;

	//
	// Public methods
	//

	public void testIceFacesWidgetBuilder()
		throws Exception
	{
		IceFacesWidgetBuilder widgetBuilder = new IceFacesWidgetBuilder();

		// SelectInputDate

		Map<String, String> attributes = CollectionUtils.newHashMap();
		attributes.put( TYPE, Date.class.getName() );
		attributes.put( DATETIME_PATTERN, "dd-MM-yyyy" );
		SelectInputDate calendar = (SelectInputDate) widgetBuilder.buildWidget( PROPERTY, attributes, null );
		assertTrue( "dd-MM-yyyy".equals( calendar.getPopupDateFormat() ) );
	}

	//
	// Protected methods
	//

	@Override
	protected void setUp()
		throws Exception
	{
		super.setUp();

		mContext = new MockIceFacesFacesContext();
	}

	@Override
	protected void tearDown()
		throws Exception
	{
		super.tearDown();

		mContext.release();
	}


	//
	// Inner class
	//

	protected static class MockIceFacesFacesContext
		extends MockFacesContext
	{
		//
		// Protected methods
		//

		@Override
		public UIComponent createComponent( String componentName )
			throws FacesException
		{
			if ( "com.icesoft.faces.SelectInputDate".equals( componentName ))
				return new SelectInputDate();

			return super.createComponent( componentName );
		}
	}
}