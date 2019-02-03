package Main;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

public class TetrisPrimary {
	
	public static void main(String args[]) throws IOException {
		
		JFrame frame = new JFrame( "Tetris" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( new Dimension( 600, 1000 ) );
		frame.setResizable( false );
		
		NewMainPanel panel = new NewMainPanel();
		frame.add( panel );
		frame.addKeyListener( panel );
		
		frame.setVisible( true );
		
		
	}
	
}
