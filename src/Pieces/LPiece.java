package Pieces;

public class LPiece extends Piece {
	public LPiece() {
		this.pieceNumber = Piece.LTet;
		this.rotation = 0;
		this.x = 5;
		
		rotations = new int[][][] {
			{
				{ 0, 0, 1 },
				{ 1, 1, 1 },
				{ 0, 0, 0 },
			},
			{
				{ 0, 1, 0 },
				{ 0, 1, 0 },
				{ 0, 1, 1 },
			},
			{
				{ 0, 0, 0 },
				{ 1, 1, 1 },
				{ 1, 0, 0 },
			},
			{
				{ 1, 1, 0 },
				{ 0, 1, 0 },
				{ 0, 1, 0 },
			},
		};
	}
}