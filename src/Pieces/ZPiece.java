package Pieces;

public class ZPiece extends Piece {
	public ZPiece() {
		this.pieceNumber = Piece.ZTet;
		this.rotation = 0;
		this.x = 5;
		
		this.rotations = new int[][][] {
			{
				{ 1, 1, 0 },
				{ 0, 1, 1 },
				{ 0, 0, 0 },
			},
			{
				{ 0, 0, 1 },
				{ 0, 1, 1 },
				{ 0, 1, 0 },
			},
			{
				{ 0, 0, 0 },
				{ 1, 1, 0 },
				{ 0, 1, 1 },
			},
			{
				{ 0, 1, 0 },
				{ 1, 1, 0 },
				{ 1, 0, 0 },
			},
		};
	}
}