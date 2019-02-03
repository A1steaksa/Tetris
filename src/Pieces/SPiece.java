package Pieces;

public class SPiece extends Piece {
	public SPiece() {
		this.pieceNumber = Piece.STet;
		this.rotation = 0;
		this.x = 5;
		
		rotations = new int[][][] {
			{
				{ 0, 1, 1 },
				{ 1, 1, 0 },
				{ 0, 0, 0 },
			},
			{
				{ 0, 1, 0 },
				{ 0, 1, 1 },
				{ 0, 0, 1 },
			},
			{
				{ 0, 0, 0 },
				{ 0, 1, 1 },
				{ 1, 1, 0 },
			},
			{
				{ 1, 0, 0 },
				{ 1, 1, 0 },
				{ 0, 1, 0 },
			},
		};
	}
}