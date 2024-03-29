/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package ultimate.tic.tac.toe;

// // Copyright 2016 theaigames.com (developers@theaigames.com)
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//        http://www.apache.org/licenses/LICENSE-2.0
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * BotStarter class
 *
 * Magic happens here. You should edit this file, or more specifically the
 * makeTurn() method to make your bot do more than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */
public class BotStarter {

    /**
     * Makes a turn. Edit this method to make your bot smarter. Currently does
     * only random moves.
     *
     * @return The column where the turn was made.
     */
    /**
     * Conventie de nume: microBoard = casuta mica de dimensiune 3 x 3
     * macroBoard = field-ul jocului, de dimensiune 9 x 9
     *
     */
    // Inf reprezinta valoarea maxima a unei mutari pe care o vom utiliza in
    // Algoritmul Minimax.
    private static int Inf = 1008;
    private static int contor = 0;
    private int[][] empty;

    // Aceasta matrice de 3 x 3 are 1 daca microBoard-ul corespunzator
    // din field-ul mare are cel putin o piesa plasata pe el, sau 0 daca
    // respectivul microBoard este complet gol.
    private void initializeEmptyArray() {

        empty = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                empty[i][j] = 0;
            }
        }
    }

    public BotStarter() {
        initializeEmptyArray();
    }

    //Verifica daca microBoard-ul dat prin limitele sale (bonuds) este liber
    private boolean checkEmptyCadran(int[][] moves, Bounds b) {

        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {

                if (moves[i][j] != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    //Verifica daca mai sunt microBoard-uri libere
    private boolean areEmptyCells() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (empty[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Intoarce numarul de celule libere dintr-un microBoard (3 x 3)
    private int getFreeCells(Field field, Bounds microBoard) {

        int result = 0;

        int[][] moves = field.getAvailableMoves();

        for (int i = microBoard.x_min; i < microBoard.x_max; i++) {
            for (int j = microBoard.y_min; j < microBoard.y_max; j++) {
                if (moves[i][j] == 0) {
                    result++;
                }
            }
        }

        return result;
    }

    //  Verifica daca mutarea M1 este mai buna decat mutarea M2 prin analiza
    //  dominantei pe care o avem in microBoard-urile corespunzatoare.
    private boolean checkIfBetter(Move m1, Move m2, Field f) {

        Bounds microBoard_m1 = getMacroboardBounds(m1.mX, m1.mY);
        Bounds microBoard_m2 = getMacroboardBounds(m2.mX, m2.mY);

        if (dominance(f, microBoard_m1) > dominance(f, microBoard_m2)) {

            if (getEnemyCloseMoves(f, microBoard_m1) != null) {
                return false;
            }

            return true;
        }

        return false;
    }

    public int dominance(Field f, Bounds b) {

        int[][] moves = f.getAvailableMoves();
        int i, j, count1 = 0, count2 = 0;

        for (i = b.x_min; i < b.x_max; i++) {
            for (j = b.y_min; j < b.y_max; j++) {
                if (moves[i][j] == BotParser.mBotId) {
                    count1++;
                }
                if (moves[i][j] == BotParser.hBotId) {
                    count2++;
                }
            }
        }

        //pentru a nu ma trimite intr=o casuta castigata
        if (f.isHisMicroboard(b.x_min + 1, b.y_min + 1) || f.isMyMicroboard(b.x_min + 1, b.y_min + 1)) {
            return -100;
        }

        return count1 - count2;
    }

    // Returneaza true daca putem inchide linia (care poate fi si diagonala)
    // formata din pozitiile x, y si z (dintre care o pozitie trebuie sa fie goala).
    boolean canWeCloseTheLine(int x, int y, int z) {

        return ((x == BotParser.mBotId && y == BotParser.mBotId && z == 0)
                || (x == BotParser.mBotId && y == 0 && z == BotParser.mBotId)
                || (x == 0 && y == BotParser.mBotId && z == BotParser.mBotId));
    }

    boolean canEnemyCloseTheLine(int x, int y, int z) {

        return ((x == BotParser.hBotId && y == BotParser.hBotId && z == 0)
                || (x == BotParser.hBotId && y == 0 && z == BotParser.hBotId)
                || (x == 0 && y == BotParser.hBotId && z == BotParser.hBotId));
    }

    // Intoarce mutarile de inchidere pe care le avem in microBoard-ul curent, pentru
    // o alege pe cea mai buna dintre ele. Daca NU putem inchide microBoard-ul curent
    // vom intoarce null
    List<Move> getCloseMoves(Field f, Bounds b) {

        // Lista de mutari de inchidere pe care le avem
        List<Move> closeMoves = new ArrayList<Move>();

        int[][] moves = f.getAvailableMoves();
        int i, j;
        int m1, m2, m3, m11, m22, m33, m13, m31;

        int emptyCellY = -1; // retine Y-ul celulei libere
        int emptyCellX = -1; // retine X-ul celulei libere

        //verific daca pot inchide pe linii
        for (i = b.x_min; i < b.x_max; i++) {

            m1 = moves[i][b.y_min]; // casuta stanga
            emptyCellY = (m1 == 0) ? b.y_min : emptyCellY;

            m2 = moves[i][b.y_min + 1]; //casuta mijloc
            emptyCellY = (m2 == 0) ? (b.y_min + 1) : emptyCellY;

            m3 = moves[i][b.y_max - 1]; //casuta dreapta
            emptyCellY = (m3 == 0) ? (b.y_max - 1) : emptyCellY;

            if (canWeCloseTheLine(m1, m2, m3)) {
                closeMoves.add(new Move(i, emptyCellY));
            }
        }

        //verific daca pot inchide pe coloane
        for (j = b.y_min; j < b.y_max; j++) {

            m1 = moves[b.x_min][j];       //casuta sus
            emptyCellX = (m1 == 0) ? b.x_min : emptyCellX;

            m2 = moves[b.x_min + 1][j];   //casuta mijloc
            emptyCellX = (m2 == 0) ? (b.x_min + 1) : emptyCellX;

            m3 = moves[b.x_max - 1][j];   //casuta jos
            emptyCellX = (m3 == 0) ? (b.x_max - 1) : emptyCellX;

            if (canWeCloseTheLine(m1, m2, m3)) {
                closeMoves.add(new Move(emptyCellX, j));
            }
        }

        //verific daca pot inchide pe diagonala principala
        m11 = moves[b.x_min][b.y_min];
        emptyCellX = (m11 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m11 == 0) ? b.y_min : emptyCellY;

        m22 = moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;

        m33 = moves[b.x_max - 1][b.y_max - 1];
        emptyCellX = (m33 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m33 == 0) ? b.y_max - 1 : emptyCellY;

        if (canWeCloseTheLine(m11, m22, m33)) {
            closeMoves.add(new Move(emptyCellX, emptyCellY));
        }

        //Verificam daca putem inchide pe diagonala secundara
        m13 = moves[b.x_min][b.y_max - 1];
        emptyCellX = (m13 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m13 == 0) ? b.y_max - 1 : emptyCellY;

        m22 = moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;

        m31 = moves[b.x_max - 1][b.y_min];
        emptyCellX = (m31 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m31 == 0) ? b.y_min : emptyCellY;

        if (canWeCloseTheLine(m13, m22, m31)) {
            closeMoves.add(new Move(emptyCellX, emptyCellY));
        }

        // Daca nu am putut inchide nicio linie (diagonala), intoarcem null
        if (closeMoves.isEmpty()) {
            return null;
        }

        return closeMoves;
    }

    // Intoarce mutarile de inchidere pe care le are adversarul.
    // Functia este identica cu cea de mai sus in care facem acelasi lucru
    // pentru noi.
    List<Move> getEnemyCloseMoves(Field f, Bounds b) {

        // Lista de mutari de inchidere pe care le are adversarul
        List<Move> enemyCloseMoves = new ArrayList<Move>();

        int[][] moves = f.getAvailableMoves();
        int i, j;
        int m1, m2, m3, m11, m22, m33, m13, m31;

        int emptyCellY = -1; // retine Y-ul celulei libere
        int emptyCellX = -1; // retine X-ul celulei libere

        //verific daca adversarul poate inchide pe linii
        for (i = b.x_min; i < b.x_max; i++) {

            m1 = moves[i][b.y_min]; // casuta stanga
            emptyCellY = (m1 == 0) ? b.y_min : emptyCellY;

            m2 = moves[i][b.y_min + 1]; //casuta mijloc
            emptyCellY = (m2 == 0) ? b.y_min + 1 : emptyCellY;

            m3 = moves[i][b.y_max - 1]; //casuta dreapta
            emptyCellY = (m3 == 0) ? b.y_max - 1 : emptyCellY;

            if (canEnemyCloseTheLine(m1, m2, m3)) {
                enemyCloseMoves.add(new Move(i, emptyCellY));
            }
        }

        //verific daca adversarul poate inchide pe coloane
        for (j = b.y_min; j < b.y_max; j++) {

            m1 = moves[b.x_min][j];//casuta sus
            emptyCellX = (m1 == 0) ? b.x_min : emptyCellX;

            m2 = moves[b.x_min + 1][j];//casuta mijloc
            emptyCellX = (m2 == 0) ? b.x_min + 1 : emptyCellX;

            m3 = moves[b.x_max - 1][j];//casuta jos
            emptyCellX = (m3 == 0) ? b.x_max - 1 : emptyCellX;

            if (canEnemyCloseTheLine(m1, m2, m3)) {
                enemyCloseMoves.add(new Move(emptyCellX, j));
            }
        }

        //verific daca adversarul poate inchide pe diagonala principala
        m11 = moves[b.x_min][b.y_min];
        emptyCellX = (m11 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m11 == 0) ? b.y_min : emptyCellY;

        m22 = moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;

        m33 = moves[b.x_max - 1][b.y_max - 1];
        emptyCellX = (m33 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m33 == 0) ? b.y_max - 1 : emptyCellY;

        if (canEnemyCloseTheLine(m11, m22, m33)) {
            enemyCloseMoves.add(new Move(emptyCellX, emptyCellY));
        }

        //verific daca adversarul poate inchide pe diagonala secundara
        m13 = moves[b.x_min][b.y_max - 1];
        emptyCellX = (m13 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m13 == 0) ? b.y_max - 1 : emptyCellY;

        m22 = moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;

        m31 = moves[b.x_max - 1][b.y_min];
        emptyCellX = (m31 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m31 == 0) ? b.y_min : emptyCellY;

        if (canEnemyCloseTheLine(m13, m22, m31)) {
            enemyCloseMoves.add(new Move(emptyCellX, emptyCellY));
        }

        // daca adversarul nu a putut inchide nicio linie (diagonala)
        if (enemyCloseMoves.isEmpty()) {
            return null;
        }

        return enemyCloseMoves;
    }

    public Bounds getMacroboardBounds(int x, int y) {
        if (x == 0 || x == 3 || x == 6) {
            if (y == 0 || y == 3 || y == 6) {
                return new Bounds(0, 3, 0, 3);
            }
            if (y == 1 || y == 4 || y == 7) {
                return new Bounds(0, 3, 3, 6);
            }
            if (y == 2 || y == 5 || y == 8) {
                return new Bounds(0, 3, 6, 9);
            }
        }
        if (x == 1 || x == 4 || x == 7) {
            if (y == 0 || y == 3 || y == 6) {
                return new Bounds(3, 6, 0, 3);
            }
            if (y == 1 || y == 4 || y == 7) {
                return new Bounds(3, 6, 3, 6);
            }
            if (y == 2 || y == 5 || y == 8) {
                return new Bounds(3, 6, 6, 9);
            }
        }
        if (x == 2 || x == 5 || x == 8) {
            if (y == 0 || y == 3 || y == 6) {
                return new Bounds(6, 9, 0, 3);
            }
            if (y == 1 || y == 4 || y == 7) {
                return new Bounds(6, 9, 3, 6);
            }
            if (y == 2 || y == 5 || y == 8) {
                return new Bounds(6, 9, 6, 9);
            }
        }
        return new Bounds();
    }

    private Move getBestMoveToWin(List<Move> closeMoves, Field field) {

        Move best = closeMoves.get(0);

        for (int i = 1; i < closeMoves.size(); i++) {
            if (checkIfBetter(closeMoves.get(i), best, field)) {
                best = closeMoves.get(i);
            }
        }

        // Aici intoarcem mutarea care trimite adversarul in microBoard-ul
        // in care noi avem dominanta maxima.
        return best;
    }

    private Move getBestBlockMove(List<Move> closeMoves, Field field) {

        Move best = closeMoves.get(0);

        for (int i = 1; i < closeMoves.size(); i++) {
            if (checkIfBetter(closeMoves.get(i), best, field)) {
                best = closeMoves.get(i);
            }
        }

        return best;
    }

    private Move getMoveByMonteCarlo(Field field, Bounds microBoard) {

        return monteCarlo(field, make_Monte(field, microBoard), BotParser.mBotId, BotParser.mBotId);
    }

    private Move getMoveByNegamax(Field field, Bounds microBoard) {

        set_equal(field);

        Pair<Integer, Move> p = minimax_abeta(field, BotParser.mBotId, 8, -BotStarter.Inf, BotStarter.Inf);

        return p.second;
    }

    // Aici se ia decizia de adaptare a algoritmului la starea curenta a jocului.
    // Pentru primele 13 mutari vom aplica MonteCalro, altfel vom aplica Negamax
    private Move getBestMove(Field field, Bounds microBoard) {

        if (BotStarter.contor < 14) {

            return getMoveByMonteCarlo(field, microBoard);

        } else {

            return getMoveByNegamax(field, microBoard);
        }
    }

    public Move makeTurn(Field field) {

        //returneaza tabela 9x9 cu mutari de la runda curenta
        Move move = null;                    // mutarea pe care o vom intoarce
        int[][] moves = field.getAvailableMoves();

        int y_min = 0, y_max = 3, x_min = 0, x_max = 3;    // coordonatele primului microBoard

        BotStarter.contor++;

        // Daca suntem la inceputul jocului, facem prima mutare
        if (field.atTheBeginning()) {

            setEmpty(getCadran(makeBounds(3, 3)));
            setEmpty(getCadran(makeBounds(1, 1)));

            return new Move(3, 3);  // pozitia de start
        }

        if (field.entireBoardAvailable()) {  // daca am la dispozitie tot field-ul

            return getMoveByNegamax(field, null);

        } else {

            //Verifica daca macroboard-ul activ este stanga sus
            if (field.isInActiveMicroboard(1, 1)) {

                //Stabilesc limitele macroboard-ului in tabela mare
                x_min = 0;
                x_max = 3;
                y_min = 0;
                y_max = 3;

                //Incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este mijloc sus
            if (field.isInActiveMicroboard(1, 4)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 0;
                x_max = 3;
                y_min = 3;
                y_max = 6;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este dreapta sus
            if (field.isInActiveMicroboard(1, 7)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 0;
                x_max = 3;
                y_min = 6;
                y_max = 9;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este mijloc stanga
            if (field.isInActiveMicroboard(4, 1)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 3;
                x_max = 6;
                y_min = 0;
                y_max = 3;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este mijloc mijloc
            if (field.isInActiveMicroboard(4, 4)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 3;
                x_max = 6;
                y_min = 3;
                y_max = 6;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este mijloc dreapta
            if (field.isInActiveMicroboard(4, 7)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 3;
                x_max = 6;
                y_min = 6;
                y_max = 9;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este stanga jos
            if (field.isInActiveMicroboard(7, 1)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 6;
                x_max = 9;
                y_min = 0;
                y_max = 3;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

            //verifica daca macroboard-ul activ este mijloc jos
            if (field.isInActiveMicroboard(7, 4)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 6;
                x_max = 9;
                y_min = 3;
                y_max = 6;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }
            //verifica daca macroboard-ul activ este drepta jos
            if (field.isInActiveMicroboard(7, 7)) {

                //stabilesc limitele macroboard-ului in tabela mare
                x_min = 6;
                x_max = 9;
                y_min = 6;
                y_max = 9;

                //incerc sa gasesc o mutare decisiva in macroboard
                move = this.getBestMove(field, new Bounds(x_min, x_max, y_min, y_max));

                if (move != null) {
                    return move;
                }
            }

        } // Am inchis else-ul

        //Daca nu am gasit nicio mutare decisiva punem prima mutare
        //valabila din ultimul macroboard activ gasit
        for (int x = x_min; x < x_max; x++) {
            for (int y = y_min; y < y_max; y++) {
                if (moves[x][y] == 0) {
                    return new Move(x, y);
                }
            }
        }

        return move;
    }

    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }

    //Afla microBoard-ul actual in care ne aflam
    public Bounds makeBounds(int x, int y) {

        Bounds b = null;
        if ((x >= 0) && (x <= 2)) {

            if ((y >= 0) && (y <= 2)) {
                b = new Bounds(0, 3, 0, 3);
            } else if ((y >= 3) && (y <= 5)) {
                b = new Bounds(0, 3, 3, 6);
            } else {
                b = new Bounds(0, 3, 6, 9);
            }
        }
        if ((x >= 3) && (x <= 5)) {

            if ((y >= 0) && (y <= 2)) {
                b = new Bounds(3, 6, 0, 3);
            } else if ((y >= 3) && (y <= 5)) {
                b = new Bounds(3, 6, 3, 6);
            } else {
                b = new Bounds(3, 6, 6, 9);
            }
        }
        if ((x >= 6) && (x <= 8)) {

            if ((y >= 0) && (y <= 2)) {
                b = new Bounds(6, 9, 0, 3);
            } else if ((y >= 3) && (y <= 5)) {
                b = new Bounds(6, 9, 3, 6);
            } else {
                b = new Bounds(6, 9, 6, 9);
            }
        }
        return b;
    }

    //In functie de Bounds returneaza numarul microBoard-ului (intre 0 si 8)
    public int getCadran(Bounds b) {

        if (b.x_min == 0) {
            if (b.y_min == 0) {
                return 0;
            }
            if (b.y_min == 3) {
                return 1;
            } else {
                return 2;
            }
        }

        if (b.x_min == 3) {
            if (b.y_min == 0) {
                return 3;
            }
            if (b.y_min == 3) {
                return 4;
            } else {
                return 5;
            }
        }

        if (b.x_min == 6) {

            if (b.y_min == 0) {
                return 6;
            }
            if (b.y_min == 3) {
                return 7;
            } else {
                return 8;
            }
        }

        return 0;
    }

    //Seteaza 1 pe pozitia microBoard-ului in care am introdus o noua valoare
    public void setEmpty(int pos) {

        if (pos == 0) {
            empty[0][0] = 1;
        }
        if (pos == 1) {
            empty[0][1] = 1;
        }
        if (pos == 2) {
            empty[0][2] = 1;
        }
        if (pos == 3) {
            empty[1][0] = 1;
        }
        if (pos == 4) {
            empty[1][1] = 1;
        }
        if (pos == 5) {
            empty[1][2] = 1;
        }
        if (pos == 6) {
            empty[2][0] = 1;
        }
        if (pos == 7) {
            empty[2][1] = 1;
        }
        if (pos == 8) {
            empty[2][2] = 1;
        }
    }

    //Returneaza numarul de ordine al microBoard-ului in care vrem sa punem este liber sau nu
    public int getEmpty(int x, int y) {

        int pos = getCadran(makeBounds(x, y));
        int val = 0;

        if (pos == 0) {
            val = empty[0][0];
        }
        if (pos == 1) {
            val = empty[0][1];
        }
        if (pos == 2) {
            val = empty[0][2];
        }
        if (pos == 3) {
            val = empty[1][0];
        }
        if (pos == 4) {
            val = empty[1][1];
        }
        if (pos == 5) {
            val = empty[1][2];
        }
        if (pos == 6) {
            val = empty[2][0];
        }
        if (pos == 7) {
            val = empty[2][1];
        }
        if (pos == 8) {
            val = empty[2][2];
        }
        return val;
    }

    public int getEmptyByBounds(Bounds bounds) {

        int pos = getCadran(bounds);
        int val = 0;

        if (pos == 0) {
            val = empty[0][0];
        }
        if (pos == 1) {
            val = empty[0][1];
        }
        if (pos == 2) {
            val = empty[0][2];
        }
        if (pos == 3) {
            val = empty[1][0];
        }
        if (pos == 4) {
            val = empty[1][1];
        }
        if (pos == 5) {
            val = empty[1][2];
        }
        if (pos == 6) {
            val = empty[2][0];
        }
        if (pos == 7) {
            val = empty[2][1];
        }
        if (pos == 8) {
            val = empty[2][2];
        }
        return val;
    }

 //====================================START MONTE CARLO========================================//
    
    //Creaza o clona a field-ului actual pentru a nu modifica field-ul cat timp utilizam Monte Carlo
    public Field clone(Field old) {

        Field newField = new Field();

        int i, j;

        for (i = 0; i < 9; i++) {
            for (j = 0; j < 9; j++) {
                newField.mBoard[i][j] = old.mBoard[i][j];
            }
        }
        for (i = 0; i < 3; i++) {
            for (j = 0; j < 3; j++) {
                newField.mMacroboard[i][j] = old.mMacroboard[i][j];
            }
        }
        return newField;
    }

    //Monte Carlo algorithm - formeaza lista de optiuni si pentru fiecare posibila
    //miscare, calculeaza un scor pe baza celor mai eficiente mutari random
    public Move monteCarlo(Field field, ArrayList<Move> options, int player, int original_player) {

        Move next = null;

        int max = -1000;

        for (int i = 0; i < options.size(); i++) {

            Field myClone = clone(field);

            myClone.mBoard[options.get(i).mX][options.get(i).mY] = player;

            if (player == 1) {
                player = 2;
            } else {
                player = 1;
            }

            Bounds b = getMacroboardBounds(options.get(i).mX, options.get(i).mY);

            int val = random_value(myClone, b, player, 0, 10, original_player);

            if (val >= max) {

                max = val;
                next = options.get(i);
            }
        }

        return next;
    }

    // Monte Carlo algorithm
    public int random_value(Field field, Bounds b, int player, int total, int steps, int original_player) {

        if (steps == 0) {
            return total;
        }

        Move move = null;

        Random randomGenerator = new Random();

        int max_score = -1000;

        ArrayList<Move> move_list = new ArrayList<Move>();

        int[][] moves = field.getAvailableMoves();

        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {

                if (moves[i][j] == 0) {

                    int sc = 0;
                    
                    if (player == original_player) {
                        if (isAligned(field, b, i, j, player) == true) {
                            sc += 2;
                        }
                        sc += score(field, getMacroboardBounds(i, j));
                    } else {
                        if (isAligned(field, b, i, j, player) == true) {
                            sc += 2;
                        }
                        sc += enemyScore(field, getMacroboardBounds(i, j));
                    }
                    if (sc >= max_score) {
                        max_score = sc;
                    }
                }
            }
        }
        
        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {
                if (moves[i][j] == 0) {

                    int sc = 0;
                    if (player == original_player) {
                        if (isAligned(field, b, i, j, player) == true) {
                            sc += 2;
                        }
                        sc += score(field, getMacroboardBounds(i, j));
                    } else {
                        if (isAligned(field, b, i, j, player) == true) {
                            sc += 2;
                        }
                        sc += enemyScore(field, getMacroboardBounds(i, j));
                    }
                    if (sc == max_score) {
                        move_list.add(new Move(i, j));
                    }
                }
            }
        }
        
        int index = randomGenerator.nextInt(move_list.size());
        
        Move picked = move_list.get(index);
        
        total += max_score;
        
        steps--;
        
        Field myClone = clone(field);
        
        myClone.mBoard[picked.mX][picked.mY] = player;
        
        if (player == 2) {
            player = 1;
        } else {
            player = 2;
        }
        
        Bounds b2 = getMacroboardBounds(picked.mX, picked.mY);
        
        return random_value(myClone, b2, player, total, steps, original_player);
    }

    //Formeaza o lista de posibile viitoare mutari
    public ArrayList<Move> make_Monte(Field field, Bounds b) {

        int max_score = -1000;
        
        ArrayList<Move> random_moves = new ArrayList<Move>();
        
        int[][] moves = field.getAvailableMoves();
        
        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {

                if (moves[i][j] == 0) {

                    int sc = 0;
                    
                    if (isAligned(field, b, i, j, BotParser.mBotId) == true) {
                        sc += 2;
                    }
                    sc += score(field, getMacroboardBounds(i, j));
                    if (sc >= max_score) {
                        max_score = sc;
                    }
                }
            }
        }
        
        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {
                
                if (moves[i][j] == 0) {

                    int sc = 0;
                    if (isAligned(field, b, i, j, BotParser.mBotId) == true) {
                        sc += 2;
                    }
                    sc += score(field, getMacroboardBounds(i, j));
                    if (sc == max_score) {
                        random_moves.add(new Move(i, j));
                    }
                }
            }
        }
        return random_moves;
    }

    //Functia care intoarce scorul pentru mutarea noastra
    public int score(Field field, Bounds future) {

        int total = 0;
        
        int dominanata = dominance(field, future);
        
        if (getCloseMoves(field, future) != null) {
            total -= 5;
        }
        total += dominanata;
        
        if (getEnemyCloseMoves(field, future) != null) {
            total -= 10;
        }
        if (field.entireBoardAvailable()) {
            total -= 15;
        }
        if (getCadran(future) == 4) {
            total -= 3;
        }
        return total;
    }

    //Functia care intoarce scorul pentru bot-ul rival
    public int enemyScore(Field field, Bounds future) {

        int total = 0;
        int dominanta = 0;
        dominanta -= dominance(field, future);
        total += dominanta;
        if (getEnemyCloseMoves(field, future) != null) {
            total -= 5;
        }
        if (getCloseMoves(field, future) != null) {
            total -= 10;
        }
        if (field.entireBoardAvailable()) {
            total -= 15;
        }
        return total;
    }

    //Verifica daca posibila mutarea se va afla pe aceasi linie, coloana sau diagonala
    // cu o valoarea pusa de noi deja existenta.
    public boolean isAligned(Field field, Bounds b, int x, int y, int player) {

        int[][] moves = field.getAvailableMoves();
        int sum = 0;
        for (int i = b.x_min; i < b.x_max; i++) {
            if (moves[i][y] == player) {
                sum++;
            } else if (moves[i][y] != 0) {
                sum--;
            }
        }
        if (sum > 0) {
            return true;
        }
        sum = 0;
        for (int j = b.y_min; j < b.y_max; j++) {

            if (moves[x][j] == player) {
                sum++;
            } else if (moves[x][j] != 0) {
                sum--;
            }
        }
        if (sum > 0) {
            return true;
        }
        sum = 0;

        if ((x == b.x_min && y == b.y_min) || (x == b.x_min + 1 && y == b.y_min + 1) || (x == b.x_min + 2 && y == b.y_min + 2)) {

            if (moves[b.x_min][b.y_min] == player) {
                sum++;
            } else if (moves[b.x_min][b.y_min] != 0) {
                sum--;
            }
            if (moves[b.x_min + 1][b.y_min + 1] == player) {
                sum++;
            } else if (moves[b.x_min + 1][b.y_min + 1] != 0) {
                sum--;
            }
            if (moves[b.x_min + 2][b.y_min + 2] == player) {
                sum++;
            } else if (moves[b.x_min + 2][b.y_min + 2] != 0) {
                sum--;
            }
            if (sum > 0) {
                return true;
            }
        }
        sum = 0;
        if ((x == b.x_min + 2 && y == b.y_min) || (x == b.x_min + 1 && y == b.y_min + 1) || (x == b.x_min && y == b.y_min + 2)) {

            if (moves[b.x_min + 2][b.y_min] == player) {
                sum++;
            } else if (moves[b.x_min + 2][b.y_min] != 0) {
                sum--;
            }
            if (moves[b.x_min + 1][b.y_min + 1] == player) {
                sum++;
            } else if (moves[b.x_min + 1][b.y_min + 1] != 0) {
                sum--;
            }
            if (moves[b.x_min][b.y_min + 2] == player) {
                sum++;
            } else if (moves[b.x_min][b.y_min + 2] != 0) {
                sum--;
            }
            if (sum > 0) {
                return true;
            }
        }
        return false;
    }
//=======================================END MONTE CARLO====================================================//

//========================================START MINIMAX ====================================================//
    public Pair<Integer, Move> minimax_abeta(Field init, int player, int depth, int alfa, int beta) {

        if (depth != 0) {
            int score = evaluate(init, player);
            if (score == 1000) {
                return new Pair<Integer, Move>(score + depth, null);
            }
            if (score == -1000) {
                return new Pair<Integer, Move>(score - depth, null);
            }
        } else {
            return new Pair<Integer, Move>(evaluate(init, player), null);
        }

        ArrayList<Move> moves = init.getAvailableMovesMM();

        if (moves.isEmpty()) {
            return new Pair<Integer, Move>(evaluate(init, player), null);
        }

        int score;

        Move fin = null;

        for (Iterator i = moves.iterator(); i.hasNext();) {

            Move crt = (Move) i.next();

            Field myMove = (Field) clone(init);

            myMove.mBoard[crt.mX][crt.mY] = player;

            Pair<Integer, Integer> coord = getMacroboardCoord(makeBounds(crt.mX, crt.mY));

            myMove.mMacroboard[coord.first][coord.second] = setId(myMove, makeBounds(crt.mX, crt.mY), player);

            Pair<Integer, Integer> coordNext = getMacroboardCoord(getMacroboardBounds(crt.mX, crt.mY));

            activateMacroboard(myMove, coordNext, getMacroboardBounds(crt.mX, crt.mY));

            if (player == 1) {
                player = 2;
            } else {
                player = 1;
            }

            Pair<Integer, Move> p = minimax_abeta(myMove, player, depth - 1, -beta, -alfa);

            if (player == 1) {
                player = 2;
            } else {
                player = 1;
            }

            score = -p.first;

            if (score > alfa) {
                alfa = score;
                fin = crt;
            }

            if (alfa >= beta) {
                break;
            }
        }
        return new Pair<Integer, Move>(alfa, fin);
    }

    public int setId(Field crt, Bounds b, int player) {
        int i, j;

        //parcurgere pe linii
        for (i = b.x_min; i < b.x_max; i++) {
            if (crt.mBoard[i][b.y_min] == player && crt.mBoard[i][b.y_min + 1] == player && crt.mBoard[i][b.y_min + 2] == player) {
                return player;
            }
        }

        for (j = b.y_min; j < b.y_max; j++) {
            if (crt.mBoard[b.x_min][j] == player && crt.mBoard[b.x_min + 1][j] == player && crt.mBoard[b.x_min + 2][j] == player) {
                return player;
            }
        }

        int m11, m22, m33, m13, m31;
        m11 = crt.mBoard[b.x_min][b.y_min];
        m22 = crt.mBoard[b.x_min + 1][b.y_min + 1];
        m33 = crt.mBoard[b.x_min + 2][b.y_min + 2];

        if (m11 == player && m22 == player && m33 == player) {
            return player;
        }

        m13 = crt.mBoard[b.x_min][b.y_min + 2];
        m31 = crt.mBoard[b.x_min + 2][b.y_min];

        if (m13 == player && m22 == player && m31 == player) {
            return player;
        }

        int full = 0;
        for (i = b.x_min; i < b.x_max; i++) {
            for (j = b.y_min; j < b.y_max; j++) {
                if (crt.mBoard[i][j] == 0) {
                    return 0;
                }
            }
        }

        return 3;//3 = este plina, deci nu mai putem juca in ea
    }

    public void activateMacroboard(Field crt, Pair<Integer, Integer> coord, Bounds b) {
        int x, y, i, j;
        x = coord.first;
        y = coord.second;

        if (crt.mMacroboard[x][y] == 1 || crt.mMacroboard[x][y] == 2 || crt.mMacroboard[x][y] == 3) {
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (crt.mMacroboard[i][j] == 0) {
                        crt.mMacroboard[i][j] = -1;
                    }
                }
            }
        } else {
            for (i = 0; i < 3; i++) {
                for (j = 0; j < 3; j++) {
                    if (crt.mMacroboard[i][j] == -1) {
                        crt.mMacroboard[i][j] = 0;
                    }
                }
            }
            crt.mMacroboard[x][y] = -1;
        }
    }

    public Pair<Integer, Integer> getMacroboardCoord(Bounds bounds) {

        int pos = getCadran(bounds);
        Pair<Integer, Integer> coord = null;

        if (pos == 0) {
            coord = new Pair(0, 0);
        }
        if (pos == 1) {
            coord = new Pair(0, 1);
        }
        if (pos == 2) {
            coord = new Pair(0, 2);
        }
        if (pos == 3) {
            coord = new Pair(1, 0);
        }
        if (pos == 4) {
            coord = new Pair(1, 1);
        }
        if (pos == 5) {
            coord = new Pair(1, 2);
        }
        if (pos == 6) {
            coord = new Pair(2, 0);
        }
        if (pos == 7) {
            coord = new Pair(2, 1);
        }
        if (pos == 8) {
            coord = new Pair(2, 2);
        }
        return coord;
    }

    // Functia de evaluate la Negamax
    public int evaluate(Field f, int player) {
        
        // daca player e in favoare -> scor pozitiv
        // daca adversarul lui player e in favoare -> scor negativ

        int enemy;
        if (player == 1) {
            enemy = 2;
        } else {
            enemy = 1;
        }

        int score = 0, i, j, nrPlayer, nrEnemy, nrFull;

        //parcrugere pe linii
        for (i = 0; i < 3; i++) {
            nrPlayer = 0;
            nrEnemy = 0;
            nrFull = 0;
            for (j = 0; j < 3; j++) {
                if (f.mMacroboard[i][j] == player) {
                    nrPlayer++;
                }
                if (f.mMacroboard[i][j] == enemy) {
                    nrEnemy++;
                }
                if (f.mMacroboard[i][j] == 3) {
                    nrFull++;
                }
            }
            if (nrPlayer == 3) {
                return 1000;
            }
            if (nrEnemy == 3) {
                return -1000;
            }
            if (nrPlayer == 0 && nrEnemy == 0 && nrFull == 0) {
                score++;
            }
            if (nrPlayer == 1 && nrEnemy == 0 && nrFull == 0) {
                score = score + 10;
            }
            if (nrPlayer == 2 && nrEnemy == 0 && nrFull == 0) {
                score = score + 100;
            }
            if (nrEnemy == 1 && nrPlayer == 0 && nrFull == 0) {
                score = score - 10;
            }
            if (nrEnemy == 2 && nrPlayer == 0 && nrFull == 0) {
                score = score - 100;
            }
        }

        //parcrugere pe coloane
        for (i = 0; i < 3; i++) {
            nrPlayer = 0;
            nrEnemy = 0;
            nrFull = 0;
            for (j = 0; j < 3; j++) {
                if (f.mMacroboard[j][i] == player) {
                    nrPlayer++;
                }
                if (f.mMacroboard[j][i] == enemy) {
                    nrEnemy++;
                }
                if (f.mMacroboard[j][i] == 3) {
                    nrFull++;
                }
            }
            if (nrPlayer == 3) {
                return 1000;
            }
            if (nrEnemy == 3) {
                return -1000;
            }
            if (nrPlayer == 0 && nrEnemy == 0 && nrFull == 0) {
                score++;
            }
            if (nrPlayer == 1 && nrEnemy == 0 && nrFull == 0) {
                score = score + 10;
            }
            if (nrPlayer == 2 && nrEnemy == 0 && nrFull == 0) {
                score = score + 100;
            }
            if (nrEnemy == 1 && nrPlayer == 0 && nrFull == 0) {
                score = score - 10;
            }
            if (nrEnemy == 2 && nrPlayer == 0 && nrFull == 0) {
                score = score - 100;
            }
        }

        nrPlayer = 0;
        nrEnemy = 0;
        nrFull = 0;
        for (i = 0; i < 3; i++) {
            if (f.mMacroboard[i][i] == player) {
                nrPlayer++;
            }
            if (f.mMacroboard[i][i] == enemy) {
                nrEnemy++;
            }
            if (f.mMacroboard[i][i] == 3) {
                nrFull++;
            }

        }

        if (nrPlayer == 3) {
            return 1000;
        }
        if (nrEnemy == 3) {
            return -1000;
        }
        if (nrPlayer == 0 && nrEnemy == 0 && nrFull == 0) {
            score++;
        }
        if (nrPlayer == 1 && nrEnemy == 0 && nrFull == 0) {
            score = score + 10;
        }
        if (nrPlayer == 2 && nrEnemy == 0 && nrFull == 0) {
            score = score + 100;
        }
        if (nrEnemy == 1 && nrPlayer == 0 && nrFull == 0) {
            score = score - 10;
        }
        if (nrEnemy == 2 && nrPlayer == 0 && nrFull == 0) {
            score = score - 100;
        }

        nrPlayer = 0;
        nrEnemy = 0;
        nrFull = 0;
        
        for (i = 0; i < 3; i++) {

            if (f.mMacroboard[i][2 - i] == player) {
                nrPlayer++;
            }
            if (f.mMacroboard[i][2 - i] == enemy) {
                nrEnemy++;
            }
            if (f.mMacroboard[i][2 - i] == 3) {
                nrFull++;
            }
        }
        
        if (nrPlayer == 3) {
            return 1000;
        }
        if (nrEnemy == 3) {
            return -1000;
        }
        if (nrPlayer == 0 && nrEnemy == 0 && nrFull == 0) {
            score++;
        }
        if (nrPlayer == 1 && nrEnemy == 0 && nrFull == 0) {
            score = score + 10;
        }
        if (nrPlayer == 2 && nrEnemy == 0 && nrFull == 0) {
            score = score + 100;
        }
        if (nrEnemy == 1 && nrPlayer == 0 && nrFull == 0) {
            score = score - 10;
        }
        if (nrEnemy == 2 && nrPlayer == 0 && nrFull == 0) {
            score = score - 100;
        }

        return score;
    }

    public void set_equal(Field field) {
        int i, j;
        if (field.isInActiveMicroboards(1, 1)) {

            if (check_if_full(field, makeBounds(1, 1))) {
                field.mMacroboard[1 / 3][1 / 3] = 3;
            }
        }

        // Similar pentru toate cele 8 microBoard-uri ramase
        if (field.isInActiveMicroboards(1, 4)) {

            if (check_if_full(field, makeBounds(1, 4))) {
                field.mMacroboard[1 / 3][4 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(1, 7)) {

            if (check_if_full(field, makeBounds(1, 7))) {
                field.mMacroboard[1 / 3][7 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(4, 1)) {

            if (check_if_full(field, makeBounds(4, 1))) {
                field.mMacroboard[4 / 3][1 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(4, 4)) {

            if (check_if_full(field, makeBounds(4, 4))) {
                field.mMacroboard[4 / 3][4 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(4, 7)) {

            if (check_if_full(field, makeBounds(4, 7))) {
                field.mMacroboard[4 / 3][7 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(7, 1)) {

            if (check_if_full(field, makeBounds(7, 1))) {
                field.mMacroboard[7 / 3][1 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(7, 4)) {

            if (check_if_full(field, makeBounds(7, 4))) {
                field.mMacroboard[7 / 3][4 / 3] = 3;
            }
        }

        if (field.isInActiveMicroboards(7, 7)) {

            if (check_if_full(field, makeBounds(7, 7))) {
                field.mMacroboard[7 / 3][7 / 3] = 3;
            }
        }
    }

    public boolean check_if_full(Field f, Bounds b) {
        for (int i = b.x_min; i < b.x_max; i++) {
            for (int j = b.y_min; j < b.y_max; j++) {

                if (f.mBoard[i][j] == 0) {
                    return false;
                }
            }
        }

        return true;
    }
}

//=============================================END MINIMAX================================================//
