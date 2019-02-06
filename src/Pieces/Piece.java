package Pieces;

import java.awt.Color;
import java.util.Random;

import Main.NewMainPanel;

public abstract class Piece {

	//The enum for this piece shape
	public int pieceNumber;

	//Our current rotation
	//Starting at 0, ending at 3, indicating the number of clockwise rotations from the starting rotation
	public int rotation = 0;
	
	
	/*
	 * Translations
	 * 
	 * Format is:
	 *	[Starting rotation][Rotation Point][x, y]
	 */
	
	//The translations for each clockwise rotation
	public int[][][] clockwiseTranslations;
	//The translations for each counter clockwise rotation
	public int[][][] counterClockwiseTranslations;

	//The position of the piece in the board
	public int x = 0, y = 23;

	//All of the possible rotations of this piece
	public int rotations[][][];
	
	//The piece enums
	public static int OTet = 1;
	public static int ITet = 2;
	public static int TTet = 3;
	public static int LTet = 4;
	public static int JTet = 5;
	public static int STet = 6;
	public static int ZTet = 7;

	//Piece colors
	public static Color[] colors = {
		new Color( 0, 0, 0 ),
		new Color( 192, 192, 192, 128 ),// O
		new Color( 0, 255, 255, 128 ),	// I
		new Color( 255, 255, 0, 128 ),	// T
		new Color( 255, 0, 255, 128 ),	// L
		new Color( 128, 0, 255, 128 ),	// J
		new Color( 0, 255, 0, 128 ),	// S
		new Color( 255, 0, 0, 128 ),	// Z
	};

	//Returns whether this piece would collide at a given position
	public boolean wouldCollide( int testX, int testY ) {
		return wouldShapeCollide( getRotationShape(), testX, testY );
	}

	//Returns whether a shape would collide at a position
	public static boolean wouldShapeCollide( int[][] shape, int checkX, int checkY ) {

		//For every block on this shape, check if it would collide
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {

				//Only check filled blocks
				if( shape[y][x] == 0 ) {
					continue;
				}

				//The test X position of a block is ( the X position of the piece + the X position of the shape's block )
				//This also applies to Y only - 1 to account for the top of the block
				int testX = checkX + x;
				int testY = checkY - y - 1;

				//If we're checking off the board, that's a collision
				if( testX < 0 || testX >= NewMainPanel.boardW ) {
					System.out.println( "Checking off board" );
					return true;
				}

				//If the testY is at or below the bottom of the game board, that's a collision
				if( testY < 0 ) {
					return true;
				}

				//Check for a collision
				if( NewMainPanel.gameBoard[testX][testY] != 0 ) {
					return true;					
				}

			}
		}

		return false;
	}

	//Places this piece onto the game board whereever it is
	public void place() {

		int[][] shape = getRotationShape();

		//For every block on this shape
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {

				//Only place filled blocks
				if( shape[y][x] == 0 ) {
					continue;
				}

				//The place X position of a block is ( the X position of the piece + the X position of the shape's block )
				//This also applies to Y only - 1 to account for the top of the block
				int placeX = this.x + x;
				int placeY = this.y - y - 1;

				//Place this block on the board
				NewMainPanel.gameBoard[placeX][placeY] = pieceNumber;
			}
		}

		//If we just went off the top, it's game over
		if( this.y + getTopMax() > NewMainPanel.boardH ) {
			NewMainPanel.gameOver();
		}

		//Once we're placed, see if we just cleared any lines
		NewMainPanel.checkForClearLines();

		//Delay the next piece for a bit
		NewMainPanel.placementWaitTime = System.currentTimeMillis() + NewMainPanel.placementWaitDelay;

	}

	//Returns whether or not we're on the left edge and cannot move left any more
	public boolean isPastLeftEdge() {
		return isPastLeftEdge( this.getRotationShape(), x );
	}

	//Returns whether a shape at a position is past the left edge
	public static boolean isPastLeftEdge( int[][] shape, int checkX ) {
		return ( checkX + Piece.getLeftMax( shape ) ) < 0;
	}

	//Returns whether or not we're on the right edge and cannot move right any more
	public boolean isPastRightEdge() {
		return isPastRightEdge( this.getRotationShape(), x );
	}

	//Returns whether a shape at a position is past the right edge
	public static boolean isPastRightEdge( int[][] shape, int checkX ) {
		return ( checkX + Piece.getRightMax( shape ) ) > ( NewMainPanel.boardW - 1 );
	}

	//Returns this piece's color
	public Color getColor() {
		return colors[ pieceNumber ];
	}

	//Returns the current rotation shape
	public int[][] getRotationShape(){
		return rotations[ rotation ];
	}

	//Rotates this piece to the right
	public void rotateClockwise() {
		rotation = ( rotation + 1 ) % 4;
	}

	//Rotates this piece to the left
	public void rotateCounterClockwise() {
		rotation--;
		if( rotation < 0 ) {
			rotation = 3;
		}
	}

	//Returns the clockwise rotation shape
	public int[][] getClockwiseRotationShape() {
		int[][] nextRotation = rotations[ ( rotation + 1 ) % 4 ];
		return nextRotation;
	}

	//Returns the counter clockwise rotation shape
	public int[][] getCounterClockwiseRotationShape() {

		int tempRotation = rotation - 1;
		if( tempRotation < 0 ) {
			tempRotation = 3;
		}

		int[][] nextRotation = rotations[ tempRotation ];
		return nextRotation;
	}

	//Returns the piece's topmost Y position, relative to the piece's top left corner
	public int getTopMax() {
		return getTopMax( getRotationShape() );
	}
	public static int getTopMax( int[][] shape ) {
		//Topmost y
		int topmost = -1;

		//Check every block
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				//If it's part of the shape and further right than the current largest, it's now the rightmost
				if( shape[y][x] != 0 && y > topmost ) {
					topmost = y;
				}
			}
		}

		return topmost;
	}

	//Returns the piece's rightmost X position, relative to the piece's top left corner
	public int getRightMax() {
		int[][] shape = getRotationShape();
		return getRightMax( shape );
	}
	public static int getRightMax( int[][] shape ) {

		//Rightmost x
		int rightmost = -1;

		//Check every block
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				//If it's part of the shape and further right than the current largest, it's now the rightmost
				if( shape[y][x] != 0 && x > rightmost ) {
					rightmost = x;
				}
			}
		}

		return rightmost;
	}

	//Returns the piece's leftmost X position, relative to the piece's top left corner
	public int getLeftMax() {
		int[][] shape = getRotationShape();
		return getLeftMax( shape );
	}
	public static int getLeftMax( int[][] shape ) {

		//Leftmost x
		int leftmost = 999;

		//Check every block
		for (int y = 0; y < shape.length; y++) {
			for (int x = 0; x < shape[y].length; x++) {
				//If it's part of the shape and further left than the current smallest, it's now the leftmost
				if( shape[y][x] != 0 && x < leftmost ) {
					leftmost = x;
				}
			}
		}

		return leftmost;
	}

	public static boolean tryToFitRotation( int[][] shape, int checkX, int checkY ) {

		//Tracks if we found an open space
		boolean foundSpace = false;

		Piece activePiece = NewMainPanel.activePiece;

		//If rotating would put us past the right edge
		if( Piece.isPastRightEdge( shape, checkX ) ) {

			System.out.println( "Would be past edge" );
			
			//Start testing the shape slid left to see if we can un-stick it
			//With a limit to how far we're willing to slide to find an open space
			int testX = checkX;
			for (int i = 0; i < 2; i++) {
				//Move our testing position left
				testX--;

				//See if we're clear of the edge
				if( !Piece.isPastRightEdge( shape, testX ) ) {

					//Start checking for clear places

					//And if we're clear of another kind of collision
					if( !Piece.wouldShapeCollide( shape, testX, checkY ) ) {

						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;

						//Stop looking
						break;
					}

					//If we would collide here, check 1 space above and 1 space below for open space

					//Check above
					if( !Piece.wouldShapeCollide( shape, testX, checkY + 1 ) ) {
						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;
						activePiece.y = activePiece.y + 1;

						//Stop looking
						break;
					}

					//Check below
					if( !Piece.wouldShapeCollide( shape, testX, checkY - 1 ) ) {
						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;
						activePiece.y = activePiece.y - 1;

						//Stop looking
						break;
					}

				}
			}

		}

		//If rotating would put us past the left edge
		if( Piece.isPastLeftEdge( shape, checkX ) ) {

			//Start testing the shape slid right to see if we can un-stick it
			//With a limit to how far we're willing to slide to find an open space
			int testX = checkX;
			for (int i = 0; i < 2; i++) {
				//Move our testing position left
				testX++;

				//See if we're clear of the edge
				if( !Piece.isPastLeftEdge( shape, testX ) ) {

					//Start checking for clear places

					//And if we're clear of another kind of collision
					if( !Piece.wouldShapeCollide( shape, testX, checkY ) ) {

						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;

						//Stop looking
						break;
					}

					//If we would collide here, check 1 space above and 1 space below for open space

					//Check above
					if( !Piece.wouldShapeCollide( shape, testX, checkY + 1 ) ) {
						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;
						activePiece.y = activePiece.y + 1;

						//Stop looking
						break;
					}

					//Check below
					if( !Piece.wouldShapeCollide( shape, testX, checkY - 1 ) ) {
						//We found an open space
						foundSpace = true;

						//Use the open space
						activePiece.x = testX;
						activePiece.y = activePiece.y - 1;

						//Stop looking
						break;
					}

				}
			}

		}

		//Check if we're just regular colliding
		if( Piece.wouldShapeCollide( shape, checkX, checkY ) ) {

			//If we are, look around for an open space

			//Check above
			if( !Piece.wouldShapeCollide( shape, checkX, checkY + 1 ) ) {
				//We found an open space
				foundSpace = true;

				//Use the open space
				activePiece.x = activePiece.x;
				activePiece.y = activePiece.y + 1;
			}

			//Check below
			if( !Piece.wouldShapeCollide( shape, activePiece.x, activePiece.y - 1 ) ) {
				//We found an open space
				foundSpace = true;

				//Use the open space
				activePiece.x = activePiece.x;
				activePiece.y = activePiece.y - 1;
			}

			//Check left
			if( !Piece.wouldShapeCollide( shape, activePiece.x - 1, activePiece.y ) ) {
				//We found an open space
				foundSpace = true;

				//Use the open space
				activePiece.x = activePiece.x - 1;
				activePiece.y = activePiece.y;
			}

			//Check right
			if( !Piece.wouldShapeCollide( shape, activePiece.x + 1, activePiece.y ) ) {
				//We found an open space
				foundSpace = true;

				//Use the open space
				activePiece.x = activePiece.x + 1;
				activePiece.y = activePiece.y;
			}

		}else {
			//If we wouldn't collide anyway, we found an open space
			foundSpace = true;
		}

		//Report whether we were able to find an open space
		return foundSpace;

	}

	//Returns a piece from its enum
	public static Piece getPiece( int pieceNum ) {

		//Should've thought this one out before I did what I done
		Piece piece = null;
		
		if( pieceNum == OTet ) {
			piece = new OPiece();
		}
		
		if( pieceNum == ITet ) {
			piece = new IPiece();
		}
		
		if( pieceNum == TTet ) {
			piece = new TPiece();
		}

		if( pieceNum == LTet ) {
			piece = new LPiece();
		}
		
		if( pieceNum == JTet ) {
			piece = new JPiece();
		}
		
		if( pieceNum == STet ) {
			piece = new SPiece();
		}
		
		if( pieceNum == ZTet ) {
			piece = new ZPiece();
		}
		
		//If there was an error, give them an I
		if( piece == null ) {
			piece = new IPiece();
		}
		
		piece.y = 23;
		
		return piece;
	}

}
