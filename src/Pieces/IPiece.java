package Pieces;
public class IPiece extends Piece {
	public IPiece() {
		this.pieceNumber = Piece.ITet;
		this.rotation = 0;
		this.x = 5;

		rotations = new int[][][] {
			{
				{ 0, 0, 0, 0 },
				{ 1, 1, 1, 1 },
				{ 0, 0, 0, 0 },
				{ 0, 0, 0, 0 },
			},
			{
				{ 0, 0, 1, 0 },
				{ 0, 0, 1, 0 },
				{ 0, 0, 1, 0 },
				{ 0, 0, 1, 0 },
			},
			{
				{ 0, 0, 0, 0 },
				{ 0, 0, 0, 0 },
				{ 1, 1, 1, 1 },
				{ 0, 0, 0, 0 },
			},
			{
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 },
				{ 0, 1, 0, 0 },
			},
		};

		//[Starting rotation][Rotation Point]{x, y}

		//Translations for this piece's clockwise translations
		clockwiseTranslations = new int[][][] {
			{ // North 0
				{ 0, 0 }, 	// 1
				{ -2, 0 }, 	// 2
				{ 1, 0 }, 	// 3
				{ -2, -1 }, // 4
				{ 1, 2 }, 	// 5
			},
			{ // East 1
				{ 0, 0 }, 	// 1
				{ -1, 0 }, 	// 2
				{ 2, 0 }, 	// 3
				{ -1, 2 }, 	// 4
				{ 2, -1 },	// 5
			},
			{ // South 2
				{ 0, 0 }, 	// 1
				{ 2, 0 }, 	// 2
				{ -1, 0 }, 	// 3
				{ 2, 1 }, 	// 4
				{ -1, -2 }, // 5
			},
			{ // West 3
				{ 0, 0 }, 	// 1
				{ 1, 0 }, 	// 2
				{ -2, 0 }, 	// 3
				{ 1, -2 }, 	// 4
				{ -2, 1 }, 	// 5
			}
		};

		//Translations for this piece's counter clockwise translations
		counterClockwiseTranslations = new int[][][] {
			{ // North 0
				{ 0, 0 }, 	// 1
				{ -2, 0 }, 	// 2
				{ 1, 0 }, 	// 3
				{ -2, 1 }, 	// 4
				{ 1, -1 }, 	// 5
			},
			{ // East 1
				{ 0, 0 }, 	// 1
				{ 2, 0 }, 	// 2
				{ -1, 0 }, 	// 3
				{ 2, 1 }, 	// 4
				{ -1, 2 }, 	// 5
			},
			{ // South 2
				{ 0, 0 }, 	// 1
				{ 1, 0 }, 	// 2
				{ -2, 0 }, 	// 3
				{ 1, -2 }, 	// 4
				{ -2, 1 }, 	// 5
			},
			{ // West 3
				{ 0, 0 }, 	// 1
				{ -2, 0 }, 	// 2
				{ 1, 0 }, 	// 3
				{ -2, 1 }, 	// 4
				{ 1, -2 }, 	// 5
			}
		};

	}
}
