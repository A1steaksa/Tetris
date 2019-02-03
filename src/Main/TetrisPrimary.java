package Main;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JFrame;

public class TetrisPrimary {
	
	public static void main(String args[]) throws IOException {
		
		JFrame frame = new JFrame( "Tetris" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( new Dimension( 700, 750 ) );
		frame.setResizable( false );
		
		NewMainPanel panel = new NewMainPanel();
		frame.add( panel );
		frame.addKeyListener( panel );
		
		//Center the frame
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation( dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2 );
		
		frame.setVisible( true );
		
	}
	
}
