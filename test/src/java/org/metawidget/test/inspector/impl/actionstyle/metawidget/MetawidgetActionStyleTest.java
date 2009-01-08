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

package org.metawidget.test.inspector.impl.actionstyle.metawidget;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.metawidget.inspector.annotation.UiAction;
import org.metawidget.inspector.iface.InspectorException;
import org.metawidget.inspector.impl.actionstyle.Action;
import org.metawidget.inspector.impl.actionstyle.metawidget.MetawidgetActionStyle;

/**
 * @author Richard Kennard
 */

public class MetawidgetActionStyleTest
	extends TestCase
{
	//
	// Constructor
	//

	/**
	 * JUnit 3.7 constructor.
	 */

	public MetawidgetActionStyleTest( String name )
	{
		super( name );
	}

	//
	// Public methods
	//

	public void testMetawidgetActionStyle()
	{
		MetawidgetActionStyle actionStyle = new MetawidgetActionStyle();
		Map<String, Action> actions = actionStyle.getActions( Foo.class );

		assertTrue( actions.size() == 1 );
		assertTrue( "bar".equals( actions.get( "bar" ).toString() ) );

		try
		{
			actionStyle.getActions( BadFoo.class );
			assertTrue( false );
		}
		catch( InspectorException e )
		{
			assertTrue( "@UiAction public abstract void org.metawidget.test.inspector.impl.actionstyle.metawidget.MetawidgetActionStyleTest$BadFoo.bar(java.lang.String) must not take any parameters".equals( e.getMessage() ));
		}
	}

	public void testInterfaceBasedActionStyle()
	{
		MetawidgetActionStyle actionStyle = new MetawidgetActionStyle();
		Map<String, Action> actions = actionStyle.getActions( Proxied_$$_javassist_.class );

		assertTrue( actions instanceof TreeMap );
		assertTrue( actions.get( "bar1" ).isAnnotationPresent( UiAction.class ));
		assertTrue( actions.get( "baz" ).isAnnotationPresent( UiAction.class ));
		assertTrue( actions.size() == 2 );

		actions = actionStyle.getActions( new InterfaceBar()
		{
			@Override
			public void baz()
			{
				// Do nothing
			}

		}.getClass() );

		assertTrue( actions instanceof TreeMap );
		assertTrue( actions.isEmpty() );

	}

	//
	// Inner class
	//

	abstract class Foo
	{
		@UiAction
		public abstract void bar();

		@UiAction
		protected abstract void shouldntFindMe();

		@SuppressWarnings("unused")
		@UiAction
		private void shouldntFindMeEither()
		{
			// Do nothing
		}
	}

	abstract class BadFoo
	{
		@UiAction
		public abstract void bar( String baz );
	}

	abstract class Proxied_$$_javassist_
		implements InterfaceFoo, InterfaceBar
	{
		// Abstract
	}

	interface InterfaceFoo
	{
		@UiAction
		void bar1();

		void bar2();
	}

	interface InterfaceBar
	{
		@UiAction
		void baz();
	}
}