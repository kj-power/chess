package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
            currRow = myPosition.getRow();
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
            currRow = myPosition.getRow();
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

        }

        else if (type == PieceType.PAWN) {
            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();

            if (this.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (currCol > 1) {
                    ChessPosition capture1 = new ChessPosition(currRow - 1, currCol - 1);
                    ChessPiece otherPiece1 = board.getPiece(capture1);
                    if (otherPiece1 != null && otherPiece1.getTeamColor() != this.getTeamColor()) {
                        if (currRow - 1 == 1) {
                            ChessMove aMove = new ChessMove(myPosition, capture1, PieceType.QUEEN);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.KNIGHT);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.ROOK);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.BISHOP);
                            moves.add(aMove);
                        }
                        else {
                            ChessMove aMove = new ChessMove(myPosition, capture1, null);
                            moves.add(aMove);
                        }
                    }
                }

                if (currCol < 8) {
                    ChessPosition capture1 = new ChessPosition(currRow - 1, currCol + 1);
                    ChessPiece otherPiece1 = board.getPiece(capture1);
                    if (otherPiece1 != null && otherPiece1.getTeamColor() != this.getTeamColor()) {
                        if (currRow - 1 == 1) {
                            ChessMove aMove = new ChessMove(myPosition, capture1, PieceType.QUEEN);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.KNIGHT);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.ROOK);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.BISHOP);
                            moves.add(aMove);
                        }
                        else {
                            ChessMove aMove = new ChessMove(myPosition, capture1, null);
                            moves.add(aMove);
                        }
                    }
                }

                currRow--;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);

                if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {

                    if (currRow == 1 && otherPiece == null) {
                        ChessMove aMove = new ChessMove(myPosition, end, PieceType.QUEEN);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.KNIGHT);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.ROOK);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.BISHOP);
                        moves.add(aMove);
                    }
                    else if (otherPiece == null) {
                        ChessMove aMove = new ChessMove(myPosition, end, null);
                        moves.add(aMove);
                    }
                }

                if (myPosition.getRow() == 7 && otherPiece == null) {
                    currRow--;

                    if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                        end = new ChessPosition(currRow, currCol);
                        otherPiece = board.getPiece(end);
                        ChessMove aMove = new ChessMove(myPosition, end, null);

                        if (otherPiece == null) {
                            moves.add(aMove);
                        }
                    }
                }
            }

            if (this.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (currCol > 1) {
                    ChessPosition capture1 = new ChessPosition(currRow + 1, currCol - 1);
                    ChessPiece otherPiece1 = board.getPiece(capture1);
                    if (otherPiece1 != null && otherPiece1.getTeamColor() != this.getTeamColor()) {
                        if (currRow + 1 == 8) {
                            ChessMove aMove = new ChessMove(myPosition, capture1, PieceType.QUEEN);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.KNIGHT);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.ROOK);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.BISHOP);
                            moves.add(aMove);
                        }
                        else {
                            ChessMove aMove = new ChessMove(myPosition, capture1, null);
                            moves.add(aMove);
                        }
                    }
                }

                if (currCol < 8) {
                    ChessPosition capture1 = new ChessPosition(currRow + 1, currCol + 1);
                    ChessPiece otherPiece1 = board.getPiece(capture1);
                    if (otherPiece1 != null && otherPiece1.getTeamColor() != this.getTeamColor()) {
                        if (currRow + 1 == 8) {
                            ChessMove aMove = new ChessMove(myPosition, capture1, PieceType.QUEEN);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.KNIGHT);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.ROOK);
                            moves.add(aMove);
                            aMove = new ChessMove(myPosition, capture1, PieceType.BISHOP);
                            moves.add(aMove);
                        }
                        else {
                            ChessMove aMove = new ChessMove(myPosition, capture1, null);
                            moves.add(aMove);
                        }
                    }
                }

                currRow++;
                ChessPosition end = new ChessPosition(currRow, currCol);
                ChessPiece otherPiece = board.getPiece(end);
                if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {

                    if (currRow == 8 && otherPiece == null) {
                        ChessMove aMove = new ChessMove(myPosition, end, PieceType.QUEEN);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.KNIGHT);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.ROOK);
                        moves.add(aMove);
                        aMove = new ChessMove(myPosition, end, PieceType.BISHOP);
                        moves.add(aMove);
                    }
                    else if (otherPiece == null) {
                        ChessMove aMove = new ChessMove(myPosition, end, null);
                        moves.add(aMove);
                    }
                }

                if (myPosition.getRow() == 2 && otherPiece == null) {
                    currRow++;

                    if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
                        end = new ChessPosition(currRow, currCol);
                        otherPiece = board.getPiece(end);
                        ChessMove aMove = new ChessMove(myPosition, end, null);

                        if (otherPiece == null) {
                            moves.add(aMove);
                        }
                    }
                }
            }
        }

        else if (type == PieceType.ROOK) {
            ChessPosition start = myPosition;

            int currRow = myPosition.getRow();
            int currCol = myPosition.getColumn();

            while (currRow < 8) {
                currRow++;
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

            while (currCol < 8) {
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

            while (currRow > 1) {
                currRow--;
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

            while (currCol > 1) {
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

        if (type == PieceType.QUEEN) {
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

            currRow = myPosition.getRow();
            currCol = myPosition.getColumn();

            while (currRow < 8) {
                currRow++;
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

            while (currCol < 8) {
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

            while (currRow > 1) {
                currRow--;
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

            while (currCol > 1) {
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

        return moves;
    }

}
