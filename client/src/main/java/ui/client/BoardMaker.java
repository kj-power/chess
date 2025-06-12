package ui.client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import service.BadRequestException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class BoardMaker {
    private ChessGame.TeamColor color;
    private ChessGame game;

    public BoardMaker(ChessGame.TeamColor color, ChessGame game) {
        this.color = color;
        this.game = game;
    }

    private static final int BOARD_SIZE_IN_SQUARES = 10;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    private static Random rand = new Random();

    public static void main(ChessGame.TeamColor color, ChessGame game, ChessPosition position) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        if (color == null) {
            color = WHITE;
        }

        Set<ChessPosition> highlights = new HashSet<>();
        if (position != null) {
            if (game.validMoves(position) == null) {
                throw new BadRequestException("Error: no valid moves for selected position");
            }
            for (ChessMove move : game.validMoves(position)) {
                highlights.add(move.getEndPosition());
            }
        }

        drawHeaders(out, color);

        for (int row = 0; row < 8; row++) {
            drawRow(out, color, row, game, highlights);
            out.println();
        }

        drawHeaders(out, color);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor color) {

        setBlack(out);

        String[] whiteHeaders = { EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY};
        String[] blackHeaders = { EMPTY, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY};
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(SET_BG_COLOR_BLUE);
            out.print(SET_TEXT_COLOR_BLACK);

            if (color == WHITE) {
                out.print(whiteHeaders[boardCol]);;
            }
            else if (color == BLACK) {
                out.print(blackHeaders[boardCol]);
            }
            out.print(RESET_BG_COLOR);
        }
        out.println();
    }

    private static void drawRow(PrintStream out, ChessGame.TeamColor color, int row, ChessGame game, Set<ChessPosition> highlights) {
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; boardCol++) {
            String square;
            int actualRow = (color == WHITE) ? 8 - row : row + 1;
            int actualCol = (color == WHITE) ? boardCol : 9 - boardCol;

            ChessPosition pos = new ChessPosition(actualRow, actualCol);
            square = getPiece(actualRow, actualCol, game, color);

            if (boardCol == 0 || boardCol == 9) {
                out.print(SET_BG_COLOR_BLUE);
                out.print(" " + actualRow + " ");
            }
            else if ((row % 2 == 0 && boardCol % 2 == 1) || (row % 2 == 1 && boardCol % 2 == 0)) {
                if (highlights.contains(pos)) {
                    out.print(SET_BG_COLOR_GREEN);
                    out.print(square);
                }
                else {
                    out.print(SET_BG_COLOR_WHITE);
                    out.print(square);
                }
            }
            else {
                if (highlights.contains(pos)) {
                    out.print(SET_BG_COLOR_DARK_GREEN);
                    out.print(square);
                }
                else {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                    out.print(square);
                }
            }
            out.print(RESET_BG_COLOR);
        }
    }

    public static String getPiece(int row, int col, ChessGame game, ChessGame.TeamColor color) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = game.getBoard().getPiece(position);
        StringBuilder output = new StringBuilder();

        if (piece == null) {
            output.append(EMPTY);
            return output.toString();
        }

        if (piece.getTeamColor() == WHITE) {
            switch (piece.getPieceType()) {
                case null -> output.append(EMPTY);
                case QUEEN -> output.append(WHITE_QUEEN);
                case KING -> output.append(WHITE_KING);
                case BISHOP -> output.append(WHITE_BISHOP);
                case KNIGHT -> output.append(WHITE_KNIGHT);
                case ROOK -> output.append(WHITE_ROOK);
                case PAWN -> output.append(WHITE_PAWN);
            }
        }
        if (piece.getTeamColor() == BLACK) {
            switch (piece.getPieceType()) {
                case null -> output.append(EMPTY);
                case QUEEN -> output.append(BLACK_QUEEN);
                case KING -> output.append(BLACK_KING);
                case BISHOP -> output.append(BLACK_BISHOP);
                case KNIGHT -> output.append(BLACK_KNIGHT);
                case ROOK -> output.append(BLACK_ROOK);
                case PAWN -> output.append(BLACK_PAWN);
            }
        }
        if (output.isEmpty()) {
            return EMPTY;
        }
        return output.toString();
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
