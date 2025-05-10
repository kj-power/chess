package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
                "squares=" + Arrays.deepToString(squares) +
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

    public void duplicateBoard(ChessBoard og) {
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                ChessPosition pos = new ChessPosition(row + 1, col + 1);
                this.squares[row][col] = og.getPiece(pos);
            }
        }
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

    public void movePiece(ChessPosition oldPos, ChessPosition newPos, ChessPiece piece) {
        squares[oldPos.getRow() - 1][oldPos.getColumn() - 1] = null;
        squares[newPos.getRow() - 1][newPos.getColumn() - 1] = piece;
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

    public ChessPosition findKing(ChessGame.TeamColor color) {
        ChessPiece king = new ChessPiece(color, ChessPiece.PieceType.KING);
        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                if (squares[row][col] != null && squares[row][col].equals(king)) {
                    ChessPosition pos;
                    pos = new ChessPosition(row + 1, col + 1);
                    return pos;
                }
            }
        }
        return null;
    }

    public Collection<ChessPosition> findPiece(ChessPiece piece) {
        Collection<ChessPosition> positions = new ArrayList<>();
        ChessPosition pos;

        for (int row = 0; row <= 7; row++) {
            for (int col = 0; col <= 7; col++) {
                if (squares[row][col] != null && squares[row][col].equals(piece)) {
                    pos = new ChessPosition(row + 1, col + 1);
                    positions.add(pos);
                }
            }
        }

        return positions;
    }

    public Collection<ChessPosition> findTeamPosition(ChessGame.TeamColor color) {
        ChessPiece piece = new ChessPiece(color, ChessPiece.PieceType.PAWN);
        Collection<ChessPosition> positions = new ArrayList<>(findPiece(piece));
        piece = new ChessPiece(color, ChessPiece.PieceType.ROOK);
        positions.addAll(findPiece(piece));
        piece = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
        positions.addAll(findPiece(piece));
        piece = new ChessPiece(color, ChessPiece.PieceType.KING);
        positions.addAll(findPiece(piece));
        piece = new ChessPiece(color, ChessPiece.PieceType.QUEEN);
        positions.addAll(findPiece(piece));
        piece = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
        positions.addAll(findPiece(piece));

        return positions;
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
