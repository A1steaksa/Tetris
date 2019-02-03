package Main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Pieces.Piece;

public class NewMainPanel extends JPanel implements KeyListener {

	/*
	 * 
	 * Timers
	 * 
	 */
	
	//How often the main game loop goes
	public static int updateTime = 10;
	
	//Roughly how often the piece drops down in milliseconds
	public static long dropTime = 1000;
	
	//What time it should be that we drop the piece down another notch
	public static long nextDropTime = 0;
	
	//When we will clear the lines and continue the game
	public static long clearedLinesClearTime = 0;
	
	//How long to wait before clearing the lines
	public static long clearedLinesClearDelay = 750;
	
	//How long to wait between placing a piece and showing the next one
	public static long placementWaitDelay = 175;
	
	//When the next piece spawns in
	public static long placementWaitTime = 0;
	
	/*
	 * 
	 * Board layout
	 * 
	 */
	
	//Size of the board in blocks
	public static int boardW = 10;
	public static int boardH = 23;
	
	//How much of the board's height is visible
	public static int boardViewH = 20;
	
	//The size of each block on the board
	public static int blockSize = 30;
	
	//The static, placed pieces on the board
	public static int[][] gameBoard = new int[ boardW ][ boardH ];
	
	//Position of the board
	int boardX = 150;
	int boardY = 20;
	
	//Position of the queue
	int queueX = 500;
	int queueY = 50;
	
	//Spacing between queue pieces
	int queueSpacing = 0;
	
	//Size of the space above skyline that is shown
	int skylineView = 10;
	
	//Position of the held piece
	int heldPieceX = 20;
	int heldPieceY = 20;
	
	//Position of the current level text
	int levelX = 475;
	int levelY = 525;
	
	//Position of the number of cleared lines text
	int linesX = 475;
	int linesY = 450;
	
	/*
	 * 
	 * Font
	 * 
	 */
	
	//The size of the main font
	public static int fontSize = 35;
	
	//The main font itself
	public static Font font;
	
	/*
	 * 
	 * Images
	 * 
	 */
	
	//Holds block images
	BufferedImage[] blockImages = new BufferedImage[ 8 ];
	
	//The ghost block image
	BufferedImage ghostImage = ImageIO.read( new File( "ghost.png" ) );
	
	/*
	 * 
	 * Fast Drop
	 * 
	 */
	
	//Tracks whether or not we're doing a fast drop
	boolean fastDrop = false;
	
	/*
	 * 
	 * Delaying and Locking
	 * 
	 */
	
	//How many delays are we allowed before it locks
	static int maxMovements = 15;
	
	//How many movements the active piece has made
	static int movementCount = 0;
	
	//How long after touching a block to lock
	static long lockDelay = 500;
	
	//When we will lock
	static long lockTime = 0;
	
	//Tracks the lowest Y the active piece has seen
	static int lowestY = 23;
	
	/*
	 * 
	 * Active Piece
	 * 
	 */

	//The piece currently falling
	public static Piece activePiece;

	/*
	 * 
	 * Line Clearing
	 * 
	 */
	
	//The lines being cleared
	static ArrayList<Integer> clearedLines = new ArrayList<Integer>();
	
	/*
	 * 
	 * Level system
	 * 
	 */
	
	//Tracks the number of lines we've cleared so far in total
	static int lineCount = 0;
	
	//Tracks the goal we're shooting for next
	static int lineGoal = 5;
	
	//How much to increase the goal by every level
	static int lineGoalIncrease = 5;
	
	//What level we're on
	static int currentLevel = 1;
	
	/*
	 * 
	 * Ghost system
	 * 
	 */
	
	public static Piece ghostPiece;
	
	/*
	 * 
	 * Piece Generation and Handling
	 * 
	 */
	
	//This is the multiple of 7 that the bag should be
	public static int bagSize = 9;
	
	//This holds the bag of pieces to be put into the queue
	public static ArrayList<Piece> bag = new ArrayList<Piece>();
	
	//The size of the queue
	public static int queueSize = 3;
	
	public static //The queue of pieces to be played
	ArrayList<Piece> queue = new ArrayList<Piece>();
	
	//The piece we currently have in holding
	public static Piece heldPiece;
	
	//Tracks whether or not we can currently swap for the held piece
	public static boolean canSwap = true;
	
	/*
	 * 
	 * Controls
	 * 
	 */
	
	//Auto repeat delay after pressing left/right to repeat the movement faster
	public static long autoRepeatStartDelay = 300;
	
	//The timer to check if auto repeat should start
	public static long autoRepeatStartTimer = 0;
	
	//The time between auto repeat movements
	//We're shooting for ~0.5 seconds from one side of the matrix to the other
	public static long autoRepeatMoveDelay = 15;
	
	//The timer for auto repeat movements
	public static long autoRepeatMoveTimer = 0;
	
	//The direction that was most recently pressed
	//-1 indicates left, 1 indicates right, 0 indicates no movement
	public static int moveDirection = 0;
	
	//Tracks if left/right keys are held currently
	public static boolean leftDown = false;
	public static boolean rightDown = false;
	
	public NewMainPanel() throws IOException {
		
		//Preprocess the blocks to their colors
		//Load in the block image
		BufferedImage blockImage = ImageIO.read( new File( "block.png" ) );
		for (int i = 0; i < 8; i++) {

			//The output image
			BufferedImage image = new BufferedImage( blockSize, blockSize, BufferedImage.TYPE_INT_ARGB );

			//Draw the block image tinted to the color of this enum
			Graphics2D g = image.createGraphics();
			g.drawImage( blockImage, 0, 0, blockSize, blockSize, null );
			g.setComposite(AlphaComposite.SrcAtop);
			g.setColor( Piece.colors[i] );
			g.fillRect( 0, 0, blockSize, blockSize );
			g.dispose();
			
			blockImages[ i ] = image;
		}
		
		//Fill the queue
		fillQueue();
		
		//Set up the first piece
		loadNextActivePiece();
		
		//Create the main font
		font = new Font( "Arial", Font.BOLD, fontSize );
	}
	
	//Empties and then fills the piece bag
	public static void fillBag() {
		
		//Empty the bag
		bag.clear();
		
		//Add bagSize number of each piece to the bag
		for (int i = 1; i <= 7; i++) {
			for (int j = 0; j < bagSize; j++) {
				bag.add( Piece.getPiece( i ) );
			}
		}
		
		//Shuffle the bag contents
		Collections.shuffle( bag );
		
	}
	
	//Fill the queue from the bag until it's full
	public static void fillQueue() {
		
		//Go until we've filled the queue
		while( queue.size() < queueSize ) {
			
			//If the bag is empty, fill it
			if( bag.isEmpty() ) {
				fillBag();
			}
			
			//Add the top item from the bag
			queue.add( bag.remove( 0 ) );
			
		}
		
	}
	
	//Returns the next piece to drop
	public static void loadNextActivePiece() {
		
		//Get the next piece from the queue and make it the active piece
		activePiece = queue.remove( 0 );
		
		//Fill the queue
		fillQueue();
		
		//Reset the new active piece
		resetActivePiece();
		
	}
	
	//Reset a piece so it can be the new active piece
	public static void resetActivePiece() {
		//Move it to the top of the matrix
		activePiece.y = 23;
		
		//Center the piece
		activePiece.x = boardW / 2 - activePiece.getRotationShape()[0].length / 2;
		
		//Reset the number of movements it has had so far
		movementCount = 0;
		
		//Reset the piece's rotation
		activePiece.rotation = 0;
		
		//Reset the y
		lowestY = boardH;
	}
	
	//The game is over
	public static void gameOver() {
		
		System.out.println( "Game Over!" );
		
		//Empty the board
		gameBoard = new int[ boardW ][ boardH ];
		
		//Get a new active piece
		loadNextActivePiece();
		
	}
	
	//Checks for level changes and handles them
	public static void handleLeveling() {
		
		//If we achieved our current goal
		if( lineCount >= lineGoal ) {
			
			//Increase the level
			currentLevel++;
			
			//Generate a new goal
			lineGoal += currentLevel * lineGoalIncrease;
			
			//Change speed
			//Equation from Tetris design doc
			dropTime = (long) (Math.pow( 0.8 - ( (  currentLevel - 1 ) * 0.007 ), ( currentLevel - 1 ) ) * 1000L);
			
		}
		
	}
	
	//Checks the board for cleared lines
	public static void checkForClearLines() {
		
		//Make sure we don't get any false positives
		clearedLines.clear();
		
		//Check every line
		for (int y = 0; y < boardH; y++) {
			
			//Tracks if this line was found to be cleared
			boolean cleared = true;
			
			for (int x = 0; x < boardW; x++) {
				
				//If we find an empty block, this line is not clear
				if( gameBoard[x][y] == 0 ) {
					cleared = false;
					continue;
				}
				
			}
			
			//If this line is cleared, mark it down as being cleared
			if( cleared ) {
				clearedLines.add( y );
				lineCount++;
			}
			
		}
		
		//If we found any cleared lines, we need to set the show cleared lines timer
		if( clearedLines.size() > 0 ) {
			clearedLinesClearTime = System.currentTimeMillis() + clearedLinesClearDelay;
		}
		
		//Handle leveling up if needed
		handleLeveling();
		
	}
	
	//Clears any lines in the clearedLines array list
	public static void clearLines() {
		
		//Lines are added bottom-up so we need to go top down
		for ( int i = clearedLines.size() - 1; i >= 0; i-- ) {
			int line = clearedLines.get( i );
			
			//Move every line above this line down by 1
			//Loop over every line above and including this line
			for (int y = line + 1; y < boardH; y++) {
				for (int x = 0; x < gameBoard.length; x++) {
					//Move every block down 1
					gameBoard[x][y-1] = gameBoard[x][y];
				}
			}
			
			
		}
		
	}
	
	@Override
	public void paintComponent( Graphics g ){
		
		/*
		 * Drawing
		 */
		
		//Draw the full window background
		g.setColor( Color.LIGHT_GRAY );
		g.fillRect( 0, 0, getWidth(), getHeight() );
		
		//Draw the queue
		//We'll just track how far down we've drawn so far because that's going to be easier than calculating it for each one
		int drawYOffset = queueY;
		
		for (int i = 0; i < queue.size(); i++) {
			
			//Get the piece to draw
			Piece piece = queue.get( i );
			
			//Get the piece's shape
			int[][] queueShape = piece.getRotationShape();
			
			//Calculate the position to draw this piece
			int pieceX = queueX;
			int pieceY = drawYOffset + queueSpacing;
			
			//Update our offset
			drawYOffset += queueSpacing + queueShape.length * blockSize;
			
			//Draw the piece
			for (int y = 0; y < queueShape.length; y++) {
				for (int x = 0; x < queueShape[y].length; x++) {
					
					//Only draw filled blocks
					if( queueShape[y][x] == 0 ) {
						continue;
					}
					
					//The drawn X position of a block is ( the X position of the piece + the X position of the shape's block ) * the block size
					//This also applies to Y
					int drawX = pieceX + x * blockSize;
					int drawY = pieceY + y * blockSize;
					
					g.drawImage( blockImages[ piece.pieceNumber ], drawX, drawY, blockSize, blockSize, null );		
				}
			}
		}
		
		//Draw the held piece
		//Only if we have one
		if( heldPiece != null ) {
			int[][] heldShape = heldPiece.getRotationShape();
			
			//Draw the piece
			for (int y = 0; y < heldShape.length; y++) {
				for (int x = 0; x < heldShape[y].length; x++) {
					
					//Only draw filled blocks
					if( heldShape[y][x] == 0 ) {
						continue;
					}
					
					//The drawn X position of a block is ( the X position of the piece + the X position of the shape's block ) * the block size
					//This also applies to Y
					int drawX = heldPieceX + x * blockSize;
					int drawY = heldPieceX + y * blockSize;
					
					g.drawImage( blockImages[ heldPiece.pieceNumber ], drawX, drawY, blockSize, blockSize, null );		
				}
			}
			
		}
		
		//Draw the line count
		g.setColor( Color.black );
		g.setFont( font );
		g.drawString( "Lines: " + lineCount , linesX, linesY );
		
		//Draw the level number
		g.setColor( Color.black );
		g.setFont( font );
		g.drawString( "Level: " + currentLevel , levelX, levelY );
		
		//Only draw the board and it's pieces in the play matrix and part of the overflow matrix
		g.setClip( boardX, boardY - skylineView, boardW * blockSize, boardH * blockSize + skylineView );
		
		//Calculate the width and height of the play space for drawing the background
		int bgW = boardW * blockSize;
		int bgH = boardViewH * blockSize + skylineView;
		
		//Draw the background
		g.setColor( Color.white );
		g.fillRect( boardX, boardY - skylineView, bgW, bgH );
		
		//Draw the board pieces
		for (int x = 0; x < gameBoard.length; x++) {
			for (int y = 0; y <= boardViewH; y++) {
				
				//Only draw spaces with something in them
				if( gameBoard[x][y] == 0 ) {
					continue;
				}
				
				//We want 0,0 to be the bottom left, so we have to adjust for that
				int drawX = boardX + x * blockSize;
				int drawY = boardY + ( boardViewH - y - 1 ) * blockSize;
				
				g.drawImage( blockImages[ gameBoard[x][y] ] , drawX, drawY, blockSize, blockSize, null );
				
			}
		}
		
//		//For fun let's draw some lines between blocks
//		g.setColor( Color.LIGHT_GRAY );
//		//Columns
//		for (int x = 0; x <= boardW; x++) {
//			g.drawLine( boardX + x * blockSize, boardY + 0, boardX + x * blockSize, boardY + boardViewH * blockSize );
//		}
//		//Rows
//		for (int y = 0; y <= boardViewH; y++) {
//			g.drawLine( boardX + 0, boardY + y * blockSize, boardX + bgW, boardY + y * blockSize );
//		}
		
		//Get the current system time
		long time = System.currentTimeMillis();
		
		//If we are currently drawing cleared lines, do that
		if( clearedLinesClearTime != 0 ) {
			
			//Draw over every cleared line
			for (int i = 0; i < clearedLines.size(); i++) {
				g.setColor( new Color( 50, 50, 50, 200 ) );
				g.fillRect( boardX + 0, boardY + ( boardViewH - clearedLines.get( i ) - 1 ) * blockSize, boardW * blockSize, blockSize );
			}
			
			//If we're done drawing cleared lines
			if( time >= clearedLinesClearTime ) {
				
				//Reset cleared lines time
				clearedLinesClearTime = 0;
				
				//Clear the lines
				clearLines();
				
			}
			
			//Don't draw anything else
			repaint();
			return;
		}
		
		//The shape of the active piece
		int[][] shape = activePiece.getRotationShape();
		
		//Draw ghost
		//Find where we dropped
		int testY = activePiece.y;
		while( !activePiece.wouldCollide( activePiece.x, testY ) ) {
			testY--;
		}
		//Draw the ghost blocks
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				
				//Only draw filled blocks
				if( shape[y][x] == 0 ) {
					continue;
				}
				
				//The drawn X position of a block is ( the X position of the piece + the X position of the shape's block ) * the block size
				//This also applies to Y
				int drawX = boardX + ( activePiece.x + x ) * blockSize;
				int drawY = boardY +  ( boardViewH - ( testY - y + 1 ) ) * blockSize;
				
				g.drawImage( blockImages[ activePiece.pieceNumber ], drawX, drawY, blockSize, blockSize, null );
				g.drawImage( ghostImage, drawX, drawY, blockSize, blockSize, null );
				
			}
		}
		
		//Draw the active piece
		//Importantly, pieces shapes are y, x not x, y
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				
				//Only draw filled blocks
				if( shape[y][x] == 0 ) {
					continue;
				}
				
				//Only draw blocks in the play space and 1 space above the play space
				if( activePiece.y - y > boardViewH + 1 ) {
					continue;
				}
				
				//The drawn X position of a block is ( the X position of the piece + the X position of the shape's block ) * the block size
				//This also applies to Y
				int drawX = boardX + ( activePiece.x + x ) * blockSize;
				int drawY = boardY + ( boardViewH - ( activePiece.y - y ) ) * blockSize;
				
				g.drawImage( blockImages[ activePiece.pieceNumber ], drawX, drawY, blockSize, blockSize, null );
				
			}
		}
		
		//Reset clip
		g.setClip( 0, 0, getWidth(), getHeight() );
		
		//If we're waiting post placement, don't do any logic
		if( time < placementWaitTime ) {
			repaint();
			return;
		}else {
			//If we're done waiting set the timer 
			placementWaitTime = 0;
		}
		
		/*
		 * Game Logic
		 */
		
		//Check if we should drop the active piece
		if( time >= nextDropTime || ( fastDrop && time >= nextDropTime - ( dropTime / 20 * 19 ) ) ) {
			
			//Check if the active piece would collide if it moved down
			if( activePiece.wouldCollide( activePiece.x, activePiece.y - 1 ) ) {
				
				//Start the lock timer if it isn't already started
				if( lockTime == 0 ) {
					//If it would collide, start the placement timer
					lockTime = time + lockDelay;
				}
				
				//If we are past the movement limit, lock the piece when it collides
				if( movementCount >= maxMovements ) {
					//Stop the lock timer
					lockTime = 0;
					
					//Place the piece
					activePiece.place();
					loadNextActivePiece();
				}
				
			}else {
			
				//Move the active piece down
				activePiece.y--;
				
				//If we just went past our lowest Y mark that and reset the placement timer
				if( activePiece.y < lowestY ) {
					
					lowestY = activePiece.y;
					movementCount = 0;
					lockTime = System.currentTimeMillis() + lockDelay;
				}
				
			}
			
			//Update next drop time
			nextDropTime = time + dropTime;
		}
		
		//If the lock timer is up, lock the piece
		if( ( time >= lockTime && lockTime != 0 ) ) {
			//As long as we're not floating
			if( activePiece.wouldCollide( activePiece.x, activePiece.y - 1 ) ) {
				//Stop the lock timer
				lockTime = 0;
				
				//Place the piece
				activePiece.place();
				loadNextActivePiece();
				
				//We can now swap again
				canSwap = true;
			}
		}
		
		
		/*
		 * Movement
		 */
		
		
		//If the auto repeat start timer is up, start the auto repeat move timer
		if( autoRepeatStartTimer != 0 && time >= autoRepeatStartTimer ) {
			autoRepeatStartTimer = 0;
			autoRepeatMoveTimer = time + autoRepeatMoveDelay;
		}
		
		//Check if the movement timer is active and up
		if( autoRepeatMoveTimer != 0 && time >= autoRepeatMoveTimer ) {
			
			//This means that we should begin repeat movement in the movement direction
			
			//Reset the timer
			autoRepeatMoveTimer = time + autoRepeatMoveDelay;
			
			//If we're moving left
			if( moveDirection == -1 ){
				moveLeft();
			}
			
			//If we're moving right
			if( moveDirection == 1 ){
				moveRight();
			}
			
		}
		
		repaint();
		
	}
	
	//Drop the active piece down to the bottom instantly
	public void hardDrop() {
		//Move down until we collide
		int testY = activePiece.y;
		while( !activePiece.wouldCollide( activePiece.x, testY ) ) {
			testY--;
		}
		activePiece.y = testY + 1;
		
		//Give them time to rotate it if they want to
		lockTime = System.currentTimeMillis() + lockDelay;
	}
	
	//Move the active piece leftward, if possible
	public void moveLeft() {
		//If we would go off the side of the screen, stop
		if( Piece.isPastLeftEdge( activePiece.getRotationShape(), activePiece.x - 1 ) ) {
			return;
		}
		
		//We can only move over if we wouldn't collide with something
		if( activePiece.wouldCollide( activePiece.x - 1, activePiece.y ) ) {
			return;
		}
		
		//Modify lock timer if it's started
		if( lockTime != 0 ) {
			//Moving the piece resets the lock timer
			lockTime = System.currentTimeMillis() + lockDelay;
			
			//Increment the movement counter
			movementCount++;
		}
		
		activePiece.x--;
	}
	
	//Move the active piece rightward, if possible
	public void moveRight() {
		//If we would go off the side of the screen, stop
		if( Piece.isPastRightEdge( activePiece.getRotationShape(), activePiece.x + 1 ) ) {
			return;
		}
		
		//We can only move over if we wouldn't collide with something
		if( activePiece.wouldCollide( activePiece.x + 1, activePiece.y ) ) {
			return;
		}
		
		//Modify lock timer if it's started
		if( lockTime != 0 ) {
			//Moving the piece resets the lock timer
			lockTime = System.currentTimeMillis() + lockDelay;
			
			//Increment the movement counter
			movementCount++;
		}
		
		activePiece.x++;
	}
	
	//Rotate the piece clockwise, if possible
	public void rotateClockwise() {
		//The shape we would be if we rotated
		int[][] rotatedShape = activePiece.getClockwiseRotationShape();
		
		//Try to find an open space for this rotated piece
		boolean success = Piece.tryToFitRotation( rotatedShape, activePiece.x, activePiece.y );
		
		//If we found a spot, rotate clockwise
		if( success ) {
			
			//Modify lock timer if it's started
			if( lockTime != 0 ) {
				//Moving the piece resets the lock timer
				lockTime = System.currentTimeMillis() + lockDelay;
				
				//Increment the movement counter
				movementCount++;
			}
			
			activePiece.rotateClockwise();
		}
	}
	
	//Rotate the active piece counter clickwise, if possible
	public void rotateCounterClockwise() {
		//The shape we would be if we rotated
		int[][] rotatedShape = activePiece.getCounterClockwiseRotationShape();
		
		//Try to find an open space for this rotated piece
		boolean success = Piece.tryToFitRotation( rotatedShape, activePiece.x, activePiece.y );
		
		//If we found a spot, rotate counter clockwise
		if( success ) {
			
			//Modify lock timer if it's started
			if( lockTime != 0 ) {
				//Moving the piece resets the lock timer
				lockTime = System.currentTimeMillis() + lockDelay;
				
				//Increment the movement counter
				movementCount++;
			}
			
			activePiece.rotateCounterClockwise();
		}
	}

	@Override
	public void keyPressed( KeyEvent event ) {
		
		//Ignore input while we're drawing cleared lines
		if( clearedLinesClearTime != 0 ) {
			return;
		}
		
		//Ignore input while we wait after placing a piece
		if( placementWaitTime != 0 ) {
			return;
		}
		
		//Hard drop
		if( event.getKeyCode() == KeyEvent.VK_SPACE ) {
			hardDrop();
		}
		
		//Fast drop
		if( event.getKeyCode() == KeyEvent.VK_DOWN ) {
			fastDrop = true;
		}
		
		//Move left
		if( event.getKeyCode() == KeyEvent.VK_LEFT ) {
			
			//If we're already holding left, this is a duplicate key stroke and should be ignored
			if( leftDown ) {
				return;
			}
			
			moveLeft();
			
			//Start the auto repeat timer
			autoRepeatStartTimer = System.currentTimeMillis() + autoRepeatStartDelay;
			
			//Stop any existing auto repeat timers
			autoRepeatMoveTimer = 0;
			
			//Set our move direction to be left
			moveDirection = -1;
			
			//Mark that we're holding left
			leftDown = true;
			
		}
		
		//Move right
		if( event.getKeyCode() == KeyEvent.VK_RIGHT ) {
			
			//If we're already holding right, this is a duplicate key stroke and should be ignored
			if( rightDown ) {
				return;
			}
			
			moveRight();
			
			//Start the auto repeat timer
			autoRepeatStartTimer = System.currentTimeMillis() + autoRepeatStartDelay;
			
			//Stop any existing auto repeat timers
			autoRepeatMoveTimer = 0;
			
			//Set our move direction to be left
			moveDirection = 1;
			
			//Mark that we're holding left
			rightDown = true;
			
		}
		
		//Rotate clockwise
		if( event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_X ) {
			rotateClockwise();
		}
		
		//Rotate counter clockwise
		if( event.getKeyCode() == KeyEvent.VK_CONTROL || event.getKeyCode() == KeyEvent.VK_Z ) {
			rotateCounterClockwise();
		}
		
		//Swap held piece
		if( event.getKeyCode() == KeyEvent.VK_C || event.getKeyCode() == KeyEvent.VK_SHIFT ) {
			
			//If we have already swapped, we can't swap again
			if( !canSwap ) {
				return;
			}
			
			//If there isn't a held piece, the swapped piece will be another one from the queue
			if( heldPiece == null ) {
				heldPiece = activePiece;
				loadNextActivePiece();
			}else{
				//Otherwise, just swap active and held
				Piece temp = activePiece;
				activePiece = heldPiece;
				heldPiece = temp;
			}
			
			//If we swapped we cannot swap again until the piece has been placed
			canSwap = false;
			
			//Reset the swapped piece
			resetActivePiece();
			
		}
		
	}

	@Override
	public void keyReleased( KeyEvent event ) {
		
		//Fast drop
		if( event.getKeyCode() == KeyEvent.VK_DOWN ) {
			fastDrop = false;
		}
		
		//Release left
		if( event.getKeyCode() == KeyEvent.VK_LEFT ) {
			leftDown = false;
			
			//If we aren't holding right when we release left, stop the auto repeat timer
			if( !rightDown ) {
				autoRepeatStartTimer = 0;
				autoRepeatMoveTimer = 0;
			}else{
				
				//If we are holding right, make that our new move direction
				moveDirection = 1;
				
				//Move right
				moveRight();
				
				//Start the auto repeat timer
				autoRepeatStartTimer = System.currentTimeMillis() + autoRepeatStartDelay;
				
				//Stop any existing auto repeat timers
				autoRepeatMoveTimer = 0;
				
			}
			
		}
		
		//Release right
		if( event.getKeyCode() == KeyEvent.VK_RIGHT ) {
			rightDown = false;
			
			//If we aren't holding left when we release left, stop the auto repeat timer
			if( !leftDown ) {
				autoRepeatStartTimer = 0;
				autoRepeatMoveTimer = 0;
			}else{
				
				//If we are holding left, make that our new move direction
				moveDirection = -1;
				
				//Move left
				moveLeft();
				
				//Start the auto repeat timer
				autoRepeatStartTimer = System.currentTimeMillis() + autoRepeatStartDelay;
				
				//Stop any existing auto repeat timers
				autoRepeatMoveTimer = 0;
				
			}
		}
		
	}

	@Override
	public void keyTyped( KeyEvent event ) {}
	
}
