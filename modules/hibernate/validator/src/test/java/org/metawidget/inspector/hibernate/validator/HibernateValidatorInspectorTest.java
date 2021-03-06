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

package org.metawidget.inspector.hibernate.validator;

import static org.metawidget.inspector.InspectionResultConstants.*;
import junit.framework.TestCase;

import org.hibernate.validator.Digits;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.Max;
import org.hibernate.validator.Min;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Range;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Richard Kennard
 */

public class HibernateValidatorInspectorTest
	extends TestCase {

	//
	// Public methods
	//

	public void testInspection() {

		HibernateValidatorInspector inspector = new HibernateValidatorInspector();
		Document document = XmlUtils.documentFromString( inspector.inspect( new Foo(), Foo.class.getName() ) );

		assertEquals( "inspection-result", document.getFirstChild().getNodeName() );

		// Entity

		Element entity = (Element) document.getDocumentElement().getFirstChild();
		assertEquals( ENTITY, entity.getNodeName() );
		assertEquals( Foo.class.getName(), entity.getAttribute( TYPE ) );
		assertFalse( entity.hasAttribute( NAME ) );

		// Properties

		Element property = XmlUtils.getChildWithAttributeValue( entity, NAME, "bar" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( TRUE, property.getAttribute( REQUIRED ) );
		assertEquals( 2, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "baz" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( TRUE, property.getAttribute( REQUIRED ) );
		assertEquals( "1", property.getAttribute( MAXIMUM_INTEGER_DIGITS ) );
		assertEquals( "2", property.getAttribute( MAXIMUM_FRACTIONAL_DIGITS ) );
		assertEquals( 4, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "minMax" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "1", property.getAttribute( MINIMUM_VALUE ) );
		assertEquals( "99", property.getAttribute( MAXIMUM_VALUE ) );
		assertEquals( "2", property.getAttribute( MINIMUM_LENGTH ) );
		assertEquals( "25", property.getAttribute( MAXIMUM_LENGTH ) );
		assertEquals( 5, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "range" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "1", property.getAttribute( MINIMUM_VALUE ) );
		assertEquals( "99", property.getAttribute( MAXIMUM_VALUE ) );
		assertEquals( 3, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "email" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( TRUE, property.getAttribute( "validation-email" ) );
		assertEquals( 2, property.getAttributes().getLength() );

		property = XmlUtils.getChildWithAttributeValue( entity, NAME, "telephone" );
		assertEquals( PROPERTY, property.getNodeName() );
		assertEquals( "[A-Za-z ]*", property.getAttribute( VALIDATION_PATTERN ) );
		assertEquals( 2, property.getAttributes().getLength() );
	}

	//
	// Inner class
	//

	public static class Foo {

		@NotNull
		public String getBar() {

			return null;
		}

		public void setBar( @SuppressWarnings( "unused" ) String bar ) {

			// Do nothing
		}

		@NotEmpty
		@Digits( integerDigits = 1, fractionalDigits = 2 )
		public String getBaz() {

			return null;
		}

		public void setBaz( @SuppressWarnings( "unused" ) String baz ) {

			// Do nothing
		}

		@Min( 1 )
		@Max( 99 )
		@Length( min = 2, max = 25 )
		public int getMinMax() {

			return 0;
		}

		public void setMinMax( @SuppressWarnings( "unused" ) int minMax ) {

			// Do nothing
		}

		@Range( min = 1, max = 99 )
		public int getRange() {

			return 0;
		}

		public void setRange( @SuppressWarnings( "unused" ) int range ) {

			// Do nothing
		}

		@Email
		public String getEmail() {

			return null;
		}

		@Pattern( regex = "[A-Za-z ]*" )
		public String getTelephone() {

			return null;
		}
	}
}
