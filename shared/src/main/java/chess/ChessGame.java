package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", gameBoard=" + gameBoard +
                '}';
    }

    public ChessGame() {
    }

    private TeamColor teamTurn;
    private ChessBoard gameBoard;

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */

    public boolean validTester(ChessMove move, ChessPiece piece, Collection<ChessPosition> opPositions, ChessPosition kingPos) {
        boolean illegal = false;
        ChessBoard copyBoard = new ChessBoard();
        copyBoard.duplicateBoard(gameBoard);
        // move the piece on the copied board
        copyBoard.movePiece(move.getStartPosition(), move.getEndPosition(), piece);

        // check possible moves of each opposing piece
        for (ChessPosition pos : opPositions) {
            ChessPiece opPiece = copyBoard.getPiece(pos);
            if (opPiece != null) {
                Collection<ChessMove> opMoves = new ArrayList<>();
                opMoves = opPiece.pieceMoves(copyBoard, pos);
                for (ChessMove opMove : opMoves) {
                    if (opMove.getEndPosition().equals(kingPos)) {
                        illegal = true;
                        break;
                    }
                }
            }
        }

        return illegal;
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = gameBoard.getPiece(startPosition);

        // see if there's a piece at that location
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> allMoves = new ArrayList<>();
        Collection<ChessMove> validMoves = new ArrayList<>();
        allMoves = piece.pieceMoves(gameBoard, startPosition);
        if (allMoves == null) {
            return null;
        }

        // initialize opposing pieces
        TeamColor opColor;
        if (teamTurn == TeamColor.WHITE) {
            opColor = TeamColor.BLACK;
        } else {
            opColor = TeamColor.WHITE;
        }

        // find out where the king is
        boolean illegal;
        ChessPiece king = new ChessPiece(teamTurn, ChessPiece.PieceType.KING);
        ChessPosition kingPos = gameBoard.findKing();

        // move piece in question through all potential positions
        for (ChessMove move : allMoves) {
            illegal = false;
            // go through each opposing piece
            Collection<ChessPosition> opPositions = new ArrayList<>(gameBoard.findTeamPosition(opColor));

            if (validTester(move, piece, opPositions, kingPos)) {
                illegal = true;
            }

            // add move if it is valid
            if (!illegal) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
