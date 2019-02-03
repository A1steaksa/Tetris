package Pieces;

public class JPiece extends Piece {
	public JPiece() {
		this.pieceNumber = Piece.JTet;
		this.rotation = 0;
		this.x = 5;
		
		rotations = new int[][][] {
			{
				{ 1, 0, 0 },
				{ 1, 1, 1 },
				{ 0, 0, 0 },
			},
			{
				{ 0, 1, 1 },
				{ 0, 1, 0 },
				{ 0, 1, 0 },
			},
			{
				{ 0, 0, 0 },
				{ 1, 1, 1 },
				{ 0, 0, 1 },
			},
			{
				{ 0, 1, 0 },
				{ 0, 1, 0 },
				{ 1, 1, 0 },
			},
		};
	}
}