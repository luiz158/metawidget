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

package org.metawidget.swing.widgetprocessor.binding.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSpinner;

import junit.framework.TestCase;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.metawidget.iface.MetawidgetException;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.swing.SwingMetawidget;
import org.metawidget.util.CollectionUtils;

/**
 * @author Richard Kennard
 */

public class BeansBindingProcessorTest
	extends TestCase
{
	//
	// Public methods
	//

	public void testBinding()
		throws Exception
	{
		// Model

		Foo foo = new Foo();
		foo.setBar( 42 );

		// Inspect

		SwingMetawidget metawidget = new SwingMetawidget();
		metawidget.addWidgetProcessor( new BeansBindingProcessor() );
		metawidget.setInspector( new PropertyTypeInspector() );
		metawidget.setToInspect( foo );

		// Test UpdateStrategy.READ_ONCE
		//
		// Also test correct mapping of Long in JSpinner

		JSpinner spinner = (JSpinner) metawidget.getComponent( 1 );
		assertTrue( 42 == (Long) spinner.getValue() );
		JLabel label = (JLabel) metawidget.getComponent( 3 );
		assertTrue( "4".equals( label.getText() ) );

		// Test UpdateStrategy.READ

		metawidget.setParameter( UpdateStrategy.class, UpdateStrategy.READ );

		spinner = (JSpinner) metawidget.getComponent( 1 );
		foo.setBar( 43 );
		assertTrue( 43 == (Long) spinner.getValue() );
		spinner.setValue( 44l );
		assertTrue( 43 == foo.getBar() );
		metawidget.getWidgetProcessor( BeansBindingProcessor.class ).save( metawidget );
		assertTrue( 44 == foo.getBar() );

		// Test UpdateStrategy.READ_WRITE

		metawidget.setParameter( UpdateStrategy.class, UpdateStrategy.READ_WRITE );

		spinner = (JSpinner) metawidget.getComponent( 1 );
		spinner.setValue( spinner.getModel().getNextValue() );
		assertTrue( 45 == foo.getBar() );
	}

	public void testSingleComponentBinding()
		throws Exception
	{
		// Model

		Foo foo = new Foo();
		foo.setBar( 42 );

		// Inspect

		SwingMetawidget metawidget = new SwingMetawidget();
		metawidget.addWidgetProcessor( new BeansBindingProcessor() );
		metawidget.setParameter( UpdateStrategy.class, UpdateStrategy.READ_WRITE );
		metawidget.setInspector( new PropertyTypeInspector() );
		metawidget.setLayoutClass( null );
		metawidget.setToInspect( foo );
		metawidget.setPath( Foo.class.getName() + "/bar" );

		JSpinner spinner = (JSpinner) metawidget.getComponent( 0 );
		assertTrue( 42l == (Long) spinner.getValue() );
		spinner.setValue( 43l );
		assertTrue( 43l == foo.getBar() );
	}

	public void testReadOnlyToStringConverter()
		throws Exception
	{
		// Model

		ReadOnlyToStringConverter<Boolean> converter = new ReadOnlyToStringConverter<Boolean>();

		assertTrue( "true".equals( converter.convertForward( Boolean.TRUE ) ) );

		try
		{
			converter.convertReverse( "true" );
			assertTrue( false );
		}
		catch ( UnsupportedOperationException e )
		{
			assertTrue( e.getMessage().indexOf( "cannot convertReverse" ) != -1 );
		}
	}

	public void testConvert()
		throws Exception
	{
		// convertReverse with built in Converter

		BeansBindingProcessor binding = new BeansBindingProcessor();
		assertTrue( 1 == (Integer) binding.convertFromString( "1", int.class ) );

		// convertForward with given Converter

		final StringBuilder builder = new StringBuilder();

		BeansBindingProcessor.registerConverter( String.class, Integer.class, new Converter<String, Integer>()
		{
			@Override
			public Integer convertForward( String value )
			{
				builder.append( "convertedForward" );
				return Integer.valueOf( value );
			}

			@Override
			public String convertReverse( Integer value )
			{
				return String.valueOf( value );
			}
		} );

		assertTrue( 1 == (Integer) binding.convertFromString( "1", int.class ) );
		assertTrue( "convertedForward".equals( builder.toString() ) );
	}

	public void testNoGetterSetterType()
		throws Exception
	{
		SwingMetawidget metawidget = new SwingMetawidget();
		metawidget.addWidgetProcessor( new BeansBindingProcessor() );
		metawidget.setInspector( new PropertyTypeInspector() );
		metawidget.setToInspect( new NoGetSetFoo() );

		try
		{
			metawidget.getComponent( 0 );
			assertTrue( false );
		}
		catch ( MetawidgetException e )
		{
			assertTrue( "Property 'bar' has no getter and no setter".equals( e.getMessage() ) );
		}
	}

	public void testUnknownType()
		throws Exception
	{
		SwingMetawidget metawidget = new SwingMetawidget();
		metawidget.addWidgetProcessor( new BeansBindingProcessor() );
		metawidget.setInspector( new PropertyTypeInspector() );

		// Clear out any DateConverters registered by previous unit tests

		BeansBindingProcessor.unregisterConverter( Date.class, String.class );

		// Saving

		CantLoadSaveFoo cantLoadSaveFoo = new CantLoadSaveFoo();
		metawidget.setToInspect( cantLoadSaveFoo );

		try
		{
			metawidget.setValue( "1/1/2001", "bar" );
			metawidget.getWidgetProcessor( BeansBindingProcessor.class ).save( metawidget );
			assertTrue( false );
		}
		catch ( MetawidgetException e )
		{
			assertTrue( "When saving from javax.swing.JTextField to org.jdesktop.beansbinding.BeanProperty[bar] (have you used BeansBinding.registerConverter?)".equals( e.getMessage() ) );
		}

		// Loading

		cantLoadSaveFoo.setBar( new Date() );
		metawidget.setToInspect( cantLoadSaveFoo );

		try
		{
			metawidget.getComponent( 0 );
			assertTrue( false );
		}
		catch ( MetawidgetException e )
		{
			assertTrue( "When binding org.metawidget.swing.widgetprocessor.binding.beansbinding.BeansBindingProcessorTest$CantLoadSaveFoo/bar to class javax.swing.JTextField.text (have you used BeansBinding.registerConverter?)".equals( e.getMessage() ) );
		}
	}

	//
	// Inner class
	//

	protected static class Foo
	{
		//
		// Private members
		//

		private final PropertyChangeSupport	mPropertyChangeSupport	= new PropertyChangeSupport( this );

		private long						mBar;

		private int							mBaz					= 4;

		private List<String>				mList					= CollectionUtils.unmodifiableList( "element1", "element2" );

		//
		// Public methods
		//

		public long getBar()
		{
			return mBar;
		}

		public void setBar( long bar )
		{
			long oldBar = mBar;
			mBar = bar;
			mPropertyChangeSupport.firePropertyChange( "bar", oldBar, mBar );
		}

		public int getBaz()
		{
			return mBaz;
		}

		public void addPropertyChangeListener( PropertyChangeListener listener )
		{
			mPropertyChangeSupport.addPropertyChangeListener( listener );
		}

		public void removePropertyChangeListener( PropertyChangeListener listener )
		{
			mPropertyChangeSupport.removePropertyChangeListener( listener );
		}

		public List<String> getList()
		{
			return mList;
		}

		public void setList( List<String> list )
		{
			mList = list;
		}
	}

	protected static class CantLoadSaveFoo
	{
		private Date	mBar;

		//
		// Public methods
		//

		public Date getBar()
		{
			return mBar;
		}

		public void setBar( Date bar )
		{
			mBar = bar;
		}
	}

	protected static class NoGetSetFoo
	{
		//
		// Public methods
		//

		public float	bar;
	}
}
