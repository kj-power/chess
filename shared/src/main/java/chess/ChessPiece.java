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
    private boolean captured;
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
    public ChessPosition newPos(ChessPosition startPos, int rowIt, int colIt) {
        int currRow = startPos.getRow();
        int currCol = startPos.getColumn();
        int finalRow = currRow + rowIt;
        int finalCol = currCol + colIt;
        ChessPosition endPos = new ChessPosition(finalRow, finalCol);
        return endPos;
    }

    public ChessMove bishopHelper(ChessBoard board, ChessPosition startPos, ChessPosition endPos) {
        ChessPiece otherPiece = board.getPiece(endPos);
        if (otherPiece == null) {
            ChessMove move = new ChessMove(startPos, endPos, null);
            return move;
        }
        else if (otherPiece.getTeamColor() != this.getTeamColor()) {
            captured = true;
            ChessMove move = new ChessMove(startPos, endPos, null);
            return move;
        }
        else {
            return null;
        }
    }

    public Collection<ChessMove> pawnHelper(ChessBoard board, ChessPosition startPos, ChessPosition endPos) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        ChessPiece otherPiece = board.getPiece(endPos);

        if (otherPiece == null) {
            ChessMove move;
            if (endPos.getRow() == 1 || endPos.getRow() == 8) {
                move = new ChessMove(startPos, endPos, PieceType.BISHOP);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.KNIGHT);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.QUEEN);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.ROOK);
                pawnMoves.add(move);
            }
            else {
                move = new ChessMove(startPos, endPos, null);
                pawnMoves.add(move);
            }
            return pawnMoves;
        }
        else {
            return null;
        }
    }

    public Collection<ChessMove> pawnCapture(ChessBoard board, ChessPosition startPos, ChessPosition endPos) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        ChessPiece otherPiece = board.getPiece(endPos);

        if (otherPiece == null) {
            return null;
        }
        else if (otherPiece.getTeamColor() != this.getTeamColor()) {
            ChessMove move;

            if (endPos.getRow() == 1 || endPos.getRow() == 8) {
                move = new ChessMove(startPos, endPos, PieceType.BISHOP);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.KNIGHT);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.QUEEN);
                pawnMoves.add(move);
                move = new ChessMove(startPos, endPos, PieceType.ROOK);
                pawnMoves.add(move);
            }
            else {
                move = new ChessMove(startPos, endPos, null);
                pawnMoves.add(move);
            }
            return pawnMoves;
        }
        else {
            return null;
        }
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPosition startPos = myPosition;

        if (type == PieceType.PAWN) {
            Collection<ChessMove> pawnMoves = new ArrayList<>();
            ChessPosition endPos;
            ChessMove move;
            int rowIt;
            int startRow;

            if (this.getTeamColor() == ChessGame.TeamColor.BLACK) {
                rowIt = -1;
                startRow = 7;
            }
            else {
                rowIt = 1;
                startRow = 2;
            }

            if (myPosition.getRow() == startRow) {
                endPos = newPos(myPosition, rowIt, 0);
                pawnMoves = pawnHelper(board, myPosition, endPos);
                if (pawnMoves != null) {
                    endPos = newPos(myPosition, (2 * rowIt), 0);
                    pawnMoves = pawnHelper(board, myPosition, endPos);
                    if (pawnMoves != null) {
                        moves.addAll(pawnMoves);
                    }
                }
            }

            endPos = newPos(myPosition, rowIt, 0);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                pawnMoves = pawnHelper(board, myPosition, endPos);
                if (pawnMoves != null) {
                    moves.addAll(pawnMoves);
                }
            }

            endPos = newPos(myPosition, rowIt, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                pawnMoves = pawnCapture(board, myPosition, endPos);
                if (pawnMoves != null) {
                    moves.addAll(pawnMoves);
                }
            }

            endPos = newPos(myPosition, rowIt, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                pawnMoves = pawnCapture(board, myPosition, endPos);
                if (pawnMoves != null) {
                    moves.addAll(pawnMoves);
                }
            }
        }

        if (type == PieceType.KNIGHT) {
            ChessPosition endPos;
            ChessMove move;

            endPos = newPos(myPosition, 2, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 2, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -2, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -2, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 1, 2);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -1, 2);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 1, -2);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -1, -2);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }
        }

        if (type == PieceType.KING) {
            ChessPosition endPos;
            ChessMove move;

            endPos = newPos(myPosition, 1, 0);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -1, 0);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 0, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 0, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 1, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, 1, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -1, 1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }

            endPos = newPos(myPosition, -1, -1);
            if (endPos.getRow() <= 8 && endPos.getRow() >= 1 && endPos.getColumn() <= 8 && endPos.getColumn() >= 1) {
                move = bishopHelper(board, myPosition, endPos);
                if (move != null) {
                    moves.add(move);
                }
            }
        }

        if (type == PieceType.BISHOP || type == PieceType.QUEEN) {
            captured = false;

            ChessPosition endPos;
            ChessMove move;

            while (myPosition.getRow() < 8 && myPosition.getColumn() < 8 && !captured) {
                endPos = newPos(myPosition, 1, 1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getRow() > 1 && myPosition.getColumn() < 8 && !captured) {
                endPos = newPos(myPosition, -1, 1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getRow() < 8 && myPosition.getColumn() > 1 && !captured) {
                endPos = newPos(myPosition, 1, -1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getRow() > 1 && myPosition.getColumn() > 1 && !captured) {
                endPos = newPos(myPosition, -1, -1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }
        }

        if (type == PieceType.ROOK || type == PieceType.QUEEN) {
            captured = false;
            ChessPosition endPos;
            ChessMove move;

            myPosition = startPos;
            while (myPosition.getRow() < 8 && !captured) {
                endPos = newPos(myPosition, 1, 0);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getColumn() < 8 && !captured) {
                endPos = newPos(myPosition, 0, 1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getColumn() > 1 && !captured) {
                endPos = newPos(myPosition, 0, -1);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }

            myPosition = startPos;
            captured = false;
            while (myPosition.getRow() > 1 && !captured) {
                endPos = newPos(myPosition, -1, 0);
                move = bishopHelper(board, startPos, endPos);
                if (move != null) {
                    moves.add(move);
                } else {break;}
                myPosition = endPos;
            }
        }

        return moves;
    }

}
