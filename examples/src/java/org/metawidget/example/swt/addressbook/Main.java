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

package org.metawidget.example.swt.addressbook;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swt.MigLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.metawidget.example.shared.addressbook.model.ContactSearch;
import org.metawidget.example.shared.addressbook.model.ContactType;
import org.metawidget.inspector.annotation.UiAction;
import org.metawidget.inspector.annotation.UiComesAfter;
import org.metawidget.swt.Facet;
import org.metawidget.swt.SwtMetawidget;

/**
 * @author Stefan Ackermann, Richard Kennard
 */

public class Main
{
	//
	// Public statics
	//

	public static void main( String[] args )
	{
		Display display = new Display();
		Shell shell = new Shell( display );
		shell.setLayout( new MigLayout( new LC().fill().debug( 500 ) ) );

		new Main( shell );

		shell.setVisible( true );
		shell.open();

		while ( !shell.isDisposed() )
		{
			if ( !display.readAndDispatch() )
				display.sleep();
		}

		display.dispose();
	}

	//
	// Private members
	//

	private ContactSearch	mContactSearch;

	private SwtMetawidget	mSearchMetawidget;

	//
	// Constructor
	//

	public Main( Shell shell )
	{
		mContactSearch = new ContactSearch();
		createSearchSection( shell );
	}

	//
	// Public methods
	//

	@UiAction
	public void search()
	{
		// Example of manual mapping. See ContactDialog for an example of using automatic Bindings

		mContactSearch.setFirstname( (String) mSearchMetawidget.getValue( "firstname" ) );
		mContactSearch.setSurname( (String) mSearchMetawidget.getValue( "surname" ) );
		mContactSearch.setType( (ContactType) mSearchMetawidget.getValue( "type" ));
	}

	@UiAction
	@UiComesAfter( "search" )
	public void addPersonal()
	{
		// addPersonal
	}

	@UiAction
	@UiComesAfter( "addPersonal" )
	public void addBusiness()
	{
		// addBusiness
	}

	//
	// Private methods
	//

	private SwtMetawidget createSearchSection( Composite parent )
	{
		// Metawidget

		mSearchMetawidget = new SwtMetawidget( parent, SWT.None );
		mSearchMetawidget.setLayoutData( new CC().grow() );
		mSearchMetawidget.setConfig( "org/metawidget/example/swt/addressbook/metawidget.xml" );

		// Embedded buttons

		Facet facetButtons = new Facet( mSearchMetawidget, SWT.None );
		facetButtons.setData( "name", "buttons" );

		SwtMetawidget buttonsMetawidget = new SwtMetawidget( facetButtons, SWT.None );
		buttonsMetawidget.setConfig( "org/metawidget/example/swt/addressbook/metawidget.xml" );
		buttonsMetawidget.setToInspect( this );

		mSearchMetawidget.setToInspect( mContactSearch );
		return mSearchMetawidget;
	}
}