package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    @Override
    public String toString() {
        return "ChessBoard{" +
                "squares=" + Arrays.toString(squares) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */

    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8) {
            return squares[position.getRow() - 1][position.getColumn() - 1];
        } else {
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for (int i = 1; i <= 8; i++) {
            ChessPosition pos = new ChessPosition(7, i);
            addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        for (int i = 1; i <= 8; i++) {
            ChessPosition pos = new ChessPosition(2, i);
            addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        ChessPosition pos = new ChessPosition(8, 1);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        pos = new ChessPosition(8, 8);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        pos = new ChessPosition(1, 1);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        pos = new ChessPosition(1, 8);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        pos = new ChessPosition(8, 2);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        pos = new ChessPosition(8, 7);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        pos = new ChessPosition(1, 2);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        pos = new ChessPosition(1, 7);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));

        pos = new ChessPosition(8, 3);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        pos = new ChessPosition(8, 6);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        pos = new ChessPosition(1, 3);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        pos = new ChessPosition(1, 6);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));

        pos = new ChessPosition(8,4);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        pos = new ChessPosition(1,4);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));

        pos = new ChessPosition(8,5);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        pos = new ChessPosition(1,5);
        addPiece(pos, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
    }
}
