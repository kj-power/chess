package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        if (type == PieceType.BISHOP) {
            ChessPosition start = myPosition;

            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();

            while (currRow < 8 && currCol < 8) {
                currRow++;
                currCol++;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(start, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                }
                else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                    break;
                }
                else if (this.getTeamColor() == otherPiece.getTeamColor()){
                    break;
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            while (currRow > 1 && currCol < 8) {
                currRow--;
                currCol++;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(start, end,null);

                if (otherPiece == null) {
                    moves.add(aMove);
                }
                else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                    break;
                }
                else if (this.getTeamColor() == otherPiece.getTeamColor()){
                    break;
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            while (currRow > 1 && currCol > 1) {
                currRow--;
                currCol--;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(start, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                }
                else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                    break;
                }
                else if (this.getTeamColor() == otherPiece.getTeamColor()){
                    break;
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            while (currRow < 8 && currCol > 1) {
                currRow++;
                currCol--;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(start, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                }
                else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                    break;
                }
                else if (this.getTeamColor() == otherPiece.getTeamColor()){
                    break;
                }
            }
        }

        else if (type == PieceType.KING) {

            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();

            // down
            currRow++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            // down and right
            currCol++;
            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            // down and left
            currCol = currCol - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            // up
            currRow--;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            // up and right
            currCol++;
            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            // up and left
            currCol = currCol - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            //right
            currCol++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            //right
            currCol = currCol - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }
        }

        else if (type == PieceType.KNIGHT) {

            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();

            // 2 down
            currRow = currRow + 2;

            // 2 down 1 right
            currCol++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            //2 down 1 left
            currCol = currCol - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            // 2 up
            currRow = currRow - 2;

            // 2 up 1 right
            currCol++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            //2 up 1 left
            currCol = currCol - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            // 2 right
            currCol = currCol + 2;

            // 2 right 1 down
            currRow++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            //2 right 1 up
            currRow = currRow - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            // 2 left
            currCol = currCol - 2;

            // 2 left 1 down
            currRow++;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

            //2 left 1 up
            currRow = currRow - 2;

            if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                ChessMove aMove = new ChessMove(myPosition, end, null);

                if (otherPiece == null) {
                    moves.add(aMove);
                } else if (this.getTeamColor() != otherPiece.getTeamColor()) {
                    moves.add(aMove);
                }
            }

        }
            return moves;
    }

}
