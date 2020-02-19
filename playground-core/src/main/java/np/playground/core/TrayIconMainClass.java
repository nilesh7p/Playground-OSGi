package np.playground.core;

import np.playground.core.util.PlaygroundUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Main class of the Tray icon executable jar
 *
 * @author Sylvain Bugat
 *
 */
public class TrayIconMainClass {

	private static final String TRAY_ICON_SKELETON_PROJECT_URL = "https://github.com/Sylvain-Bugat/tray-icon-skeleton";

	/** Load the tray icon image to display in the tray bar*/
	//private static final Image TRAY_ICON_IMAGE = Toolkit.getDefaultToolkit().getImage( TrayIconMainClass.class.getClassLoader().getResource( "TrayIcon.png" ) ); //$NON-NLS-1$
	private static Image TRAY_ICON_IMAGE = null;

	static {
		//TRAY_ICON_IMAGE = ImageIO.read(new URL("http://icons.iconarchive.com/icons/scafer31000/bubble-circle-3/16/GameCenter-icon.png"));
		//TRAY_ICON_IMAGE = PlaygroundUtil.decodeBase64ToImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAADeElEQVQ4T12Tf0wbZRjHv/er1ytwd+0YMZkksOhQRKkzLnHOuGUumbpkfxhxFISGItqs28DMzAllRF1UspAwCEbdFlaIG6vxL9FMqQFiWF3FSYZYTBCxG39QruuA0t61d2fuDAnx+9f75nmfT77fN89D4H+qqXE/vf2hEt8jZY8eikZnLblcjqBpKvXnbHSYZbkvA4FLI5tbiI2L290slm5/4KtXXj28X1qKIzQyitjdGFRVw9aiQhQVFcEuiAj9EPr29u+/vTYzM7Nm9JqApqYmZteuZ254PO6n5hf+hrveA0VW8FhFOURexK9Tt0CRJGzWPDzhfBzLieWFSDhcPjk5uW4C/P6O4fb21pdomkb3+T709vbg2d270d9/0TT49snTCE/8BJuNgyzLeLhsB9ZWVq4Hg9cOEo2Nbx44ccL3fUVFBXQA73/wIQYDg3A6KxEMDpmAt44242Z4AnxBATRNRSYto/LJSvw1N7+faGvr+Lz9zOk3dJ2AhWEwMHgFLc3N2LatGCUlxXA4HJiamjJrIElA16GqKhjGAkAbIC5c6L/l8dQ711Ip0BSF9Hoa7gYPxsfHIQiC6cBht8OIZzRuyDiTFL1E9PT0KT6flzEAWUUBy1qxnl5HIDCI4W+GkZDikBL3QICAIPAAQZguNE0DTVMZorenTz7q81oMgJrLIZORQTM0rFYWKyspqJqGO3fu4uOzZxH55WfwhisdJoBl2Qxx+fJAtK6utmwDoChZKFkFiqLAynJQNR2ykoEOEk2NHkSnb4MXRTOO1cotEa2tZ744deqdRs5mRWp1FUo2ByWngLWw+DE0ilBoBK/X12PLlkKMjY3B/9674EUeuqqDZJh+4siR2ucaPA3jB17Yh2QyCcMBRZFI3r+P6upaRG7ewDHfcXiPHUdcSqDq8CHY8vJAgoSFs7xsDlJLy8lRv7/teUHksRxfhmaE1HV0dnYhOHQFH33SCefOnYj9E0Odqxr5+XmgKOr63PzcQRPgcrnse/bsC9e6qnbY8vMhJSTIGRkaCNyTJDAch4ICAd1d53Dxs0/hKNy6ICty+eLi4n+jbKij41yhJMWCLlf13uLiByHa7UinZfOzVtdSuHZ1COe7u0CT5HeCaq+aiW9aps3r6fV6X5TlXE1pacleAqqYzargbHxydvaPiUgkfHV6evrrze//BSDBgjwyng1OAAAAAElFTkSuQmCC");
		//TRAY_ICON_IMAGE = PlaygroundUtil.decodeBase64ToImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAACf0lEQVQ4T2NkwAEyI6yXigmxSoOkX737/XT6iqPR2JQyIgsmBllWfv7598ibr78uh1kLnw+y41cAya859On+mqNvjMS4WPW4OVis56873g7TBzcgNcyqI8FLOPPthz+/bj75+dFOj0fx4aufr0EK5cXYRQ9d+nJfQ5adX4ifhW3+1ncz56w+WgaSAxsAsjnFT6SCm52Jj5uD6de0jW9Of3vPNNeQhf8ySP78n4+6XIL/krP8RUy//vjH9u3nv09rD3zK6V1wcDHYgDAvU7tYd5G1GnIcfC2LXyz4xiyT1SEoKM3OyAgOg5///z+teP/+KdffJ9NqYiUSVGTYnjP8/qfJaLX6O9gABwcDAW8ToTPP3vx+8eSrhH0fP78tMwNDwj9GRnAYMP3//+AvA8OCoo8fD8twvzioIcshvPzwa8sDBy58YASFtq4yh5WxOrfcvLUfU8rE1fdy/vvXFHXjwgKGfwwO4MBiYjiwTMMg4TsTU133ixsuiSECs8/e/Pro8t0fxxjrsxwOWGhz2X/+/vflu+PsPj4ikqwM//61x1w9f+DAsTMNYBdamTQs0TZ0YGBiqtzy5vlvIcufW3g5mcVPXP12kHIDQF7QVua0NFXnkifFC6dvfnt49e734/BAjLQVPX7j8Y+3xASilAirxNYz70zAgQjy4/9joZwMrEw37jz5JUFMNF5/+OPTot1vg9ZsPXUYbEBxgn1ssAPfFC6khPT9HeM8A1aBSyD5C78/6HEK/U+CJaSvP/99mrPpTQcoScOTckqodVeCp2DGu09/f+JKyuoy7PzCAixsC7a9nT571bEKeFKGZQxCmUmEm02Xl53ZBmtmQs+qxGZnADK/fDSODV+FAAAAAElFTkSuQmCC");
		TRAY_ICON_IMAGE = PlaygroundUtil.decodeBase64ToImage("data:image/png;base64,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");
	}

	/**
	 * Main program launched in the jar file
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main( final String args[] ) throws IOException{

		//Test if the system support the system tray
		if( SystemTray.isSupported() ) {

			//Try to use the system Look&Feel
			try {
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			}
			catch( final ClassNotFoundException exception ) {
				//If System Look&Feel is not supported, stay with the default one
			}
			catch( final InstantiationException exception ) {
				//If System Look&Feel is not supported, stay with the default one
			}
			catch( final IllegalAccessException exception ) {
				//If System Look&Feel is not supported, stay with the default one
			}
			catch( final UnsupportedLookAndFeelException exception ) {
				//If System Look&Feel is not supported, stay with the default one
			}

			//Add the icon  to the system tray
			final TrayIcon trayIcon = new TrayIcon( TRAY_ICON_IMAGE, "Tray icon skeleton" );
			trayIcon.setImageAutoSize( true );

			// Get the system default browser to open execution details
			final Desktop desktop = Desktop.getDesktop();

			//Action listener to get click on top menu items
			final ActionListener menuListener = new ActionListener() {
				@SuppressWarnings("synthetic-access")
				public void actionPerformed( final ActionEvent e) {

					if( JMenuItem.class.isInstance( e.getSource() ) ){

						JMenuItem jMenuItem = (JMenuItem) e.getSource();
						JOptionPane.showMessageDialog( null, "It works, you clicked on:" + System.lineSeparator() + jMenuItem.getText(), "Your skill is great!!", JOptionPane.INFORMATION_MESSAGE ); //$NON-NLS-1$
					}
				}
			};

			//About menu listener
			final ActionListener aboutListener = new ActionListener() {
				@SuppressWarnings("synthetic-access")
				public void actionPerformed( final ActionEvent e) {

					//Open an URL using the system default browser
					try {
						final URI executionURI = new URI( TRAY_ICON_SKELETON_PROJECT_URL );
						desktop.browse( executionURI );
					}
					catch( final URISyntaxException exception ) {

						final StringWriter stringWriter = new StringWriter();
						exception.printStackTrace( new PrintWriter( stringWriter ) );
						JOptionPane.showMessageDialog( null, exception.getMessage() + System.lineSeparator() + stringWriter.toString(), "Tray icon skeleton redirection error", JOptionPane.ERROR_MESSAGE ); //$NON-NLS-1$
					}
					catch( final IOException exception ) {

						final StringWriter stringWriter = new StringWriter();

						exception.printStackTrace( new PrintWriter( stringWriter ) );
						JOptionPane.showMessageDialog( null, exception.getMessage() + System.lineSeparator() + stringWriter.toString(), "Tray icon skeleton redirection error", JOptionPane.ERROR_MESSAGE ); //$NON-NLS-1$
					}
				}
			};

			//Get the system tray
			final SystemTray tray = SystemTray.getSystemTray();

			//Tray icon skeleton exit listener
			final ActionListener exitListener = new ActionListener() {
				@SuppressWarnings("synthetic-access")
				public void actionPerformed( final ActionEvent e) {
					//Important: remove the icon from the tray to dispose it
					tray.remove( trayIcon );
					System.exit( 0 );
				}
			};

			//Popup menu
			JPopupMenu.setDefaultLightWeightPopupEnabled( true );
			final JPopupMenu popupMenu = new JPopupMenu();

			//Add 10 menu items
			for( int i = 0 ; i < 10 ; i++ ){

				final JMenuItem jMenuItem = new JMenuItem( "menu item " + i );
				popupMenu.add( jMenuItem );
				jMenuItem.addActionListener( menuListener );
			}

			//Adding some menu separator
			popupMenu.addSeparator();

			final JMenuItem aboutItem = new JMenuItem( "About Tray icon skeleton" ); //$NON-NLS-1$
			popupMenu.add( aboutItem );
			aboutItem.addActionListener( aboutListener );

			//Adding some menu separator
			popupMenu.addSeparator();

			//Quit menu to terminate the tray icon by disposing the tray icon
			final JMenuItem exitItem = new JMenuItem( "Quit" ); //$NON-NLS-1$
			popupMenu.add( exitItem );
			exitItem.addActionListener( exitListener );


			//Hidden dialog displayed behing the system tray to auto hide the popup menu when clicking somewhere else on the screen
			final JDialog hiddenDialog = new JDialog ();
			hiddenDialog.setSize( 10, 10 );

			//Listener based on the focus to auto hide the hidden dialog and the popup menu when the hidden dialog box lost focus
			hiddenDialog.addWindowFocusListener(new WindowFocusListener () {

				public void windowLostFocus ( final WindowEvent e ) {
					hiddenDialog.setVisible( false );
				}

				public void windowGainedFocus ( final WindowEvent e ) {
					//Nothing to do
				}
			});


			//Add a listener to display the popupmenu and the hidden dialog box when the tray icon is clicked
			trayIcon.addMouseListener( new MouseAdapter() {

				public void mouseReleased( final MouseEvent e) {

					if( e.isPopupTrigger() ) {
						//Display the menu at the position of the mouse
						//The dialog is also displayed at this position but it is behind the system tray
						popupMenu.setLocation( e.getX(), e.getY() );
						hiddenDialog.setLocation( e.getX(), e.getY() );

						//Important: set the hidden dialog as the invoker to hide the menu with this dialog lost focus
						popupMenu.setInvoker( hiddenDialog );
						hiddenDialog.setVisible( true );
						popupMenu.setVisible( true );
					}
				}
			});

			//Add the icon to the system tray
			try {
				tray.add( trayIcon );
			}
			catch ( final AWTException e ) {

				final StringWriter stringWriter = new StringWriter();
				e.printStackTrace( new PrintWriter( stringWriter ) );
				JOptionPane.showMessageDialog( null, "tray icon cannot be added to the system tray" + System.lineSeparator() + e.getMessage() + System.lineSeparator() + stringWriter.toString(), "Tray icon skeleton initialization error", JOptionPane.ERROR_MESSAGE ); //$NON-NLS-1$

				System.exit( 2 );
			}
		}
		else {
			//if the System is not compatible with SystemTray
			JOptionPane.showMessageDialog( null, "SystemTray cannot be initialized" + System.lineSeparator() + "this system is not compatible!", "Tray icon skeleton initialization error", JOptionPane.ERROR_MESSAGE ); //$NON-NLS-1$ //$NON-NLS-2$

			System.exit( 1 );
		}
	}
}