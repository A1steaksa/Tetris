package Pieces;

import Main.MainPanel;

public class TPiece extends Piece {
	public TPiece() {
		this.pieceNumber = Piece.TTet;
		this.rotation = 0;
		this.x = 3;
		
		rotations = new int[][][] {
			{
				{ 0, 1, 0 },
				{ 1, 1, 1 },
				{ 0, 0, 0 },
			},
			{
				{ 0, 1, 0 },
				{ 0, 1, 1 },
				{ 0, 1, 0 },
			},
			{
				{ 0, 0, 0 },
				{ 1, 1, 1 },
				{ 0, 1, 0 },
			},
			{
				{ 0, 1, 0 },
				{ 1, 1, 0 },
				{ 0, 1, 0 },
			},
		};
	}
}
