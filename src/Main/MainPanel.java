package Main;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Pieces.IPiece;
import Pieces.JPiece;
import Pieces.LPiece;
import Pieces.OPiece;
import Pieces.Piece;
import Pieces.SPiece;
import Pieces.TPiece;
import Pieces.ZPiece;

public class MainPanel extends JPanel implements KeyListener {

	//The dimensions of the play space
	int boardW = 10;
	int boardH = 23;

	//The height that we actually draw
	int viewH = 20;

	//The size of a single square
	int squareSize = 40;

	//Game board is 23 tiles high, only 20 are drawn
	int[][] gameBoard;

	//Stores the system nanotime
	long oldTime;
	long newTime;

	//Holds square images
	BufferedImage[] squareImages = new BufferedImage[ 7 ];

	//The speed that the piece moves downward
	int moveSpeed = 1;

	//The piece that is currently dropping
	Piece activePiece;

	MainPanel() throws IOException{
		//Initialize the game board to 7 which will be our empty space number
		gameBoard = new int[boardW][boardH];
		for (int x = 0; x < gameBoard.length; x++) {
			for (int y = 0; y < gameBoard[x].length; y++) {
				gameBoard[x][y] = 7;
			}
		}

		//Preprocess the squares to their colors
		//Load in the square image
		BufferedImage squareImage = ImageIO.read( new File( "square.png" ) );
		for (int i = 0; i < 7; i++) {

			//The output image
			BufferedImage image = new BufferedImage( squareSize, squareSize, BufferedImage.TYPE_INT_ARGB );

			//Draw the square image tinted to the color of this enum
			Graphics2D g = image.createGraphics();
			g.drawImage( squareImage, 0, 0, squareSize, squareSize, null );
			g.setComposite(AlphaComposite.SrcAtop);
			g.setColor( Piece.colors[i] );
			g.fillRect( 0, 0, squareSize, squareSize );
			g.dispose();

			squareImages[ i ] = image;

		}

		//Get the starting piece
		activePiece = getNextPiece();

	}
	
	public Piece getNextPiece() {
		
		Random random = new Random();
		
		int pieceNum = random.nextInt( 7 );
		
		//Should've thought this one out before I did what I done
		switch( pieceNum ) {
			case 0:
				return new OPiece();
			case 1:
				return new IPiece();
			case 2:
				return new TPiece();
			case 3:
				return new LPiece();
			case 4:
				return new JPiece();
			case 5:
				return new SPiece();
			case 6:
				return new ZPiece();		
		}
		
		return new IPiece();
		
	}

	@Override
	public void paintComponent( Graphics g ){

		//Draw Background
		int drawW = squareSize * boardW;
		int drawH = squareSize * viewH;
		g.setColor( Color.white );
		g.fillRect( 0, 0, drawW, drawH );

		//Draw all pieces on the board
		for (int x = 0; x < gameBoard.length; x++) {
			for (int y = ( boardH - viewH ); y < gameBoard[x].length; y++) {

				//The number of this square
				int squareNum = gameBoard[x][y];

				//If it's an empty space, don't draw it
				if( squareNum == 7 ) {
					continue;
				}

				//Calculate this square's drawX and drawY from it's X, Y position

				int drawX = x * squareSize;
				int drawY = ( y - ( boardH - viewH ) ) * squareSize;

				g.setColor( Piece.colors[ gameBoard[x][y] ] );
				g.drawImage( squareImages[squareNum] , drawX, drawY, squareSize, squareSize, null );

			}
		}

		if( activePiece != null ) {

			//Move the active piece down
			activePiece.y += moveSpeed;

			//Get the shape of the active piece
			int[][] shape = activePiece.getRotationShape();
			
			//Draw the active piece
			for (int x = 0; x < shape.length; x++) {
				for (int y = 0; y < shape[x].length; y++) {

					//If this is a block
					if( shape[x][y] != 0 ) {

						//Adjust our x and y for square size
						int drawX = activePiece.x * squareSize + x * squareSize;
						int drawY =  activePiece.y + ( y - ( boardH - viewH ) ) * squareSize;

						g.setColor( Piece.colors[activePiece.pieceNumber] );
						g.drawImage( squareImages[activePiece.pieceNumber] , drawX, drawY, squareSize, squareSize, null );
						//g.fillRect( drawX, drawY, squareSize, squareSize );

					}

				}
			}

			//Check for downward collisions
			for (int y = 0; y < shape.length; y++) {
				for (int x = 0; x < shape[y].length; x++) {

					int checkX = (int) ( activePiece.x + x );
					int checkY = (int) ( ( activePiece.y / (float) squareSize ) + (float) y + 1 );

					//If we're checking outside the bounds, ignore it.
					if( checkX < 0 || checkX >= boardW ) {
						continue;
					}

					//If we just moved into a space with something in it, we have collided and need to stop
					if( gameBoard[ checkX ][ checkY ] != 7 ) {

						//moveSpeed = 0;
						
						//We can assume that if we hit something in this space, the spaces 1 above in the Y are going to be where we land
						int placeX = (int) activePiece.x;
						int placeY = (int) ( activePiece.y / (float) squareSize );

						int[][] placeShape = activePiece.getRotationShape();
						
						//Place the pieces onto the board
						for (int y1 = 0; y1 < placeShape.length; y1++) {
							for (int x1 = 0; x1 < placeShape[y1].length; x1++) {
								
								//Only place blocks, not empty space
								if( placeShape[y1][x1] != 0 ) {
									
									int testX = placeX + x1;
									int testY = placeY + y1;
									
									System.out.println( "x1 " + x1 );
									System.out.println( "placeX " + placeX );
									System.out.println( testX + ", " + testY );
									System.out.println( "size: " + gameBoard.length + ", " + gameBoard[0].length );
									
									gameBoard[ testY ][ testX ] = activePiece.pieceNumber;
								}
							}
						}

						activePiece = getNextPiece();

					}

				}
			}

		}

		try {
			Thread.sleep( 10 );
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();

	}

	@Override
	public void keyPressed(KeyEvent event) {

		//Move right
		if( event.getKeyCode() == KeyEvent.VK_RIGHT ) {

			//If we're as far right as we can be, stop
			if( ( activePiece.x + activePiece.getRightMax() + 1 ) >= boardW ) {
				return;
			}

			activePiece.x++;
		}

		//Move left
		if( event.getKeyCode() == KeyEvent.VK_LEFT ) {

			//If we're as far left as we can be, stop
			if( ( activePiece.x + activePiece.getLeftMax() ) <= 0 ) {
				return;
			}

			activePiece.x--;
		}

		//Rotate right
		if( event.getKeyCode() == KeyEvent.VK_CONTROL ) {

			int testX = (int) activePiece.x;

			//While we're stuck in a right wall, move left
			while( ( testX + activePiece.getRightMax( activePiece.getClockwiseRotationShape() ) ) >= boardW ) {
				testX--;

				//If we would have to move more than 2 spaces to rotate, we don't rotate
				if( (activePiece.x - testX) > 2 ) {
					return;
				}
			}

			//While we're stuck in a left wall, move right
			while( ( testX + activePiece.getLeftMax( activePiece.getClockwiseRotationShape() ) + 1 ) <= 0 ) {
				testX++;

				//If we would have to move more than 2 spaces to rotate, we don't rotate
				if( (activePiece.x - testX) > 2 ) {
					return;
				}
			}

			activePiece.x = testX;
			activePiece.rotateClockwise();
		}

		//Rotate left
		if( event.getKeyCode() == KeyEvent.VK_SPACE ) {

			int testX = activePiece.x;

			//While we're stuck in a right wall, move left
			while( ( testX + activePiece.getRightMax( activePiece.getCounterClockwiseRotationShape() ) ) >= boardW ) {
				testX--;

				//If we would have to move more than 2 spaces to rotate, we don't rotate
				if( (activePiece.x - testX) > 2 ) {
					return;
				}
			}

			//While we're stuck in a left wall, move right
			while( ( testX + activePiece.getLeftMax( activePiece.getCounterClockwiseRotationShape() ) + 1 ) <= 0 ) {
				testX++;

				//If we would have to move more than 2 spaces to rotate, we don't rotate
				if( (activePiece.x - testX) > 2 ) {
					return;
				}
			}

			activePiece.x = testX;
			activePiece.rotateCounterClockwise();
		}

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
