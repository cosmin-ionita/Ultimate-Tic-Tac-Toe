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
import java.util.List;
import java.util.Random;

/**
 * BotStarter class
 *
 * Magic happens here. You should edit this file, or more specifically
 * the makeTurn() method to make your bot do more than random moves.
 *
 * @author Jim van Eeden <jim@starapple.nl>
 */

public class BotStarter {
    
    /**
     * Makes a turn. Edit this method to make your bot smarter.
     * Currently does only random moves.
     *
     * @return The column where the turn was made.
     */
    
    
    /** Conventie de nume: microBoard = casuta mica de dimensiune 3 x 3
     *                     macroBoard = field-ul jocului, de dimensiune 9 x 9
     *
     */
    
    public int[][] empty;
    
    private void initializeEmptyArray() {
        
        empty=new int[3][3];
        
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                empty[i][j]=0;
    }
    
    public BotStarter()
    {
        initializeEmptyArray();
    }
    
    //Verifica daca noi incepem jocul(tabla goala) sau adversarul.
    public boolean checkFirstMove(Field f){
        
        int[][] moves = f.getAvailableMoves();
        
        for(int i = 0; i < 9; i++)
            for(int j = 0; j < 9; j++)
                if(moves[i][j] != 0)
                    return false;
        
        return true;
    }
    
    //Daca noi incepem jocul, muta in coltul stanga sus al tablei din mijloc, altfel
    //alegea cea mai buna mutare.
    public Move makeFirstMove(Field f){
        
        Move next;
        
        if(checkFirstMove(f) == true)
            next = new Move(3,3);
        else
            next = makeTurn(f);
        
        return next;
    }
    
    // Returneaza primul cadran in care nu exista nicio valoarea, si putem sa ne ducem acolo, conditia
    // cu f.isInActiveMicroboard este pusa pentru a nu il trimite in cadranul in care noi trebuie sa mutam acum.
    // Bounds actual sunt coordonatele cadranului in care noi trebuie sa punem.
    public Bounds getEmptyCell(Field f, Bounds actual){
        
        int[][] moves = f.getAvailableMoves();
        
        Bounds b = new Bounds(0, 3, 0, 3);
        
        if((checkEmptyCadran(moves,b) == true) &&
                (f.isInActiveMicroboard(0,0) == false) &&
                (moves[actual.x_min][actual.y_min] == 0))
            
            return b;
        
        b = new Bounds(0, 3, 3, 6);
        
        if((checkEmptyCadran(moves,b) == true) &&
                (f.isInActiveMicroboard(0,4) == false) &&
                (moves[actual.x_min][actual.y_min + 1] == 0))
            
            return b;
        
        b = new Bounds(0, 3, 6, 9);
        
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(0,8) == false) && (moves[actual.x_min][actual.y_min + 2] == 0))
            return b;
        b = new Bounds(3, 6, 0, 3);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(4,0) == false) && (moves[actual.x_min + 1][actual.y_min] == 0))
            return b;
        b = new Bounds(3, 6, 3, 6);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(4,4) == false) && (moves[actual.x_min + 1][actual.y_min + 1] == 0))
            return b;
        b = new Bounds(3, 6, 6, 9);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(4,8) == false) && (moves[actual.x_min + 1][actual.y_min + 2] == 0))
            return b;
        b = new Bounds(6, 9, 0, 3);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(7,0) == false) && (moves[actual.x_min + 2][actual.y_min] == 0))
            return b;
        b = new Bounds(6, 9, 3, 6);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(7,4) == false) && (moves[actual.x_min + 2][actual.y_min + 1] == 0))
            return b;
        b = new Bounds(6, 9, 6, 9);
        if((checkEmptyCadran(moves,b) == true) && (f.isInActiveMicroboard(7,8) == false) && (moves[actual.x_min + 2][actual.y_min + 2] == 0))
            return b;
        return null;
    }
    
    //Verifica daca Cadranul este liber
    public boolean checkEmptyCadran(int[][] moves, Bounds b){
        
        for(int i = b.x_min; i < b.x_max; i++)
            for(int j = b.y_min; j < b.y_max; j++){
                
                if(moves[i][j] != 0)
                    return false;
            }
        
        return true;
    }
    
    //Verifica daca matricea empty mai are cadrane libere
    public boolean areEmptyCells(){
        
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(empty[i][j] == 0)
                    return true;
        return false;
    }
    
    
    /* Verifica daca mutarea M1 este mai buna decat mutarea M2 prin analiza
    dominantei pe care o avem in microBoard-urile corespunzatoare.
    */
    public boolean checkIfBetter(Move m1, Move m2, Field f) {
        
        Bounds microBoard_m1 = getMacroboardBounds(m1.mX, m1.mY);
        Bounds microBoard_m2 = getMacroboardBounds(m2.mX, m2.mY);
        
        if(dominance(f, microBoard_m1) > dominance(f, microBoard_m2)) {
            
            if(getEnemyCloseMoves(f, microBoard_m1) != null)
                return false;
            
            return true;
        }
        
        return false;
    }
    
    public int dominance(Field f, Bounds b) {
        
        int[][] moves = f.getAvailableMoves();
        int i,j, count1 = 0, count2 = 0;
        
        for(i = b.x_min; i < b.x_max; i++)
            for(j = b.y_min; j < b.y_max; j++) {
                if(moves[i][j] == BotParser.mBotId)
                    count1++;
                if(moves[i][j] == BotParser.hBotId)
                    count2++;
            }
        //pentru a nu ma trimite intr=o casuta castigata
        if(f.isHisMicroboard(b.x_min+1,b.y_min+1) || f.isMyMicroboard(b.x_min+1,b.y_min+1))
            return -100;
        return count1 - count2;
    }
    
    // Returneaza true daca putem inchide linia (care poate fi si diagonala)
    // formata din pozitiile x, y si z (dintre care o pozitie trebuie sa fie goala).
    
    boolean canWeCloseTheLine(int x, int y, int z) {
        
        return ((x == BotParser.mBotId && y == BotParser.mBotId && z == 0) ||
                (x == BotParser.mBotId && y == 0 && z == BotParser.mBotId) ||
                (x == 0 && y == BotParser.mBotId && z == BotParser.mBotId));
    }
    
    boolean canEnemyCloseTheLine(int x, int y, int z) {
        
        return ((x == BotParser.hBotId && y == BotParser.hBotId && z == 0) ||
                (x == BotParser.hBotId && y == 0 && z == BotParser.hBotId) ||
                (x == 0 && y == BotParser.hBotId && z == BotParser.hBotId));
    }
    
    
    
    // Intoarce mutarile de inchidere pe care le avem in microBoard-ul curent, pentru
    // o alege pe cea mai buna dintre ele. Daca NU putem inchide microBoard-ul curent
    // vom intoarce null
    
    List<Move> getCloseMoves(Field f, Bounds b) {
        
        // Lista de mutari de inchidere pe care le avem
        List<Move> closeMoves = new ArrayList<Move>();
        
        int[][] moves = f.getAvailableMoves();
        int i,j;
        int m1,m2,m3,m11,m22,m33,m13,m31;
        
        int emptyCellY = -1; // retine Y-ul celulei libere
        int emptyCellX = -1; // retine X-ul celulei libere
        
        //verific daca pot inchide pe linii
        
        for(i = b.x_min; i < b.x_max; i++) {
            
            m1 = moves[i][b.y_min]; // casuta stanga
            emptyCellY = (m1 == 0) ? b.y_min : emptyCellY;
            
            m2 = moves[i][b.y_min + 1]; //casuta mijloc
            emptyCellY = (m2 == 0) ? (b.y_min + 1) : emptyCellY;
            
            m3 = moves[i][b.y_max - 1]; //casuta dreapta
            emptyCellY = (m3 == 0) ? (b.y_max - 1) : emptyCellY;
            
            if(canWeCloseTheLine(m1, m2, m3))
                closeMoves.add(new Move(i, emptyCellY));
        }
        
        //verific daca pot inchide pe coloane
        
        for(j = b.y_min; j < b.y_max; j++) {
            
            m1=moves[b.x_min][j];       //casuta sus
            emptyCellX = (m1 == 0) ? b.x_min : emptyCellX;
            
            m2=moves[b.x_min + 1][j];   //casuta mijloc
            emptyCellX = (m2 == 0) ? (b.x_min + 1) : emptyCellX;
            
            m3=moves[b.x_max - 1][j];   //casuta jos
            emptyCellX = (m3 == 0) ? (b.x_max - 1) : emptyCellX;
            
            if(canWeCloseTheLine(m1, m2, m3))
                closeMoves.add(new Move(emptyCellX, j));
        }
        
        //verific daca pot inchide pe diagonala principala
        
        m11=moves[b.x_min][b.y_min];
        emptyCellX = (m11 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m11 == 0) ? b.y_min : emptyCellY;
        
        m22=moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;
        
        m33=moves[b.x_max - 1][b.y_max - 1];
        emptyCellX = (m33 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m33 == 0) ? b.y_max - 1 : emptyCellY;
        
        if(canWeCloseTheLine(m11, m22, m33))
            closeMoves.add(new Move(emptyCellX, emptyCellY));
        
        
        //verific daca pot inchide pe diagonala secundara
        
        m13=moves[b.x_min][b.y_max - 1];
        emptyCellX = (m13 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m13 == 0) ? b.y_max - 1 : emptyCellY;
        
        m22=moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;
        
        m31=moves[b.x_max - 1][b.y_min];
        emptyCellX = (m31 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m31 == 0) ? b.y_min : emptyCellY;
        
        if(canWeCloseTheLine(m13, m22, m31))
            closeMoves.add(new Move(emptyCellX, emptyCellY));
        
        // daca nu am putut inchide nicio linie (diagonala)
        if(closeMoves.isEmpty())
            return null;
        
        return closeMoves;
    }
    
    
    private boolean canWeFinalClose(int x, int y, int z) {
        
        return ((x == BotParser.mBotId && y == BotParser.mBotId && z == -1) ||
                (x == BotParser.mBotId && y == -1 && z == BotParser.mBotId) ||
                (x == -1 && y == BotParser.mBotId && z == BotParser.mBotId));
    }
    
    // Intoarce lista de microBoard-uri care corespund unei inchideri decisive
    // din partea noastra.
    
    List<Bounds> getFinalCloseMoves(int[][] macroBoard) {
        
        // Lista de mutari de inchidere pe care le avem
        List<Bounds> closeMoves = new ArrayList<Bounds>();
        
        int m1, m2, m3, m11, m22, m33, m13, m31;
        
        int emptyCellX = -1, emptyCellY = -1;
        
        //verific daca pot inchide pe linii
        
        for(int i = 0; i<3; i++) {
            
            m1 = macroBoard[i][0];          // casuta stanga
            emptyCellX = (m1 == -1) ? 0 : emptyCellX;
            
            m2 = macroBoard[i][1];          //casuta mijloc
            emptyCellX = (m2 == -1) ? 1 : emptyCellX;
            
            m3 = macroBoard[i][2];          //casuta dreapta
            emptyCellX = (m3 == -1) ? 2 : emptyCellX;
            
            if(canWeFinalClose(m1, m2, m3)) {
                closeMoves.add(makeBounds(i * 3, emptyCellX * 3));
            }
        }
        
        //verific daca pot inchide pe coloane
        
        for(int i = 0; i<3; i++) {
            
            m1 = macroBoard[0][i];          // casuta stanga
            emptyCellY = (m1 == -1) ? 0 : emptyCellY;
            
            m2 = macroBoard[1][i];          //casuta mijloc
            emptyCellY = (m2 == -1) ? 1 : emptyCellY;
            
            m3 = macroBoard[2][i];          //casuta dreapta
            emptyCellY = (m3 == -1) ? 2 : emptyCellY;
            
            if(canWeFinalClose(m1, m2, m3)) {
                closeMoves.add(makeBounds(emptyCellY * 3, i * 3));
            }
        }
        
        //verific daca pot inchide pe diagonala principala
        
        m11 = macroBoard[0][0];
        emptyCellX = (m11 == -1) ? 0 : emptyCellX;
        emptyCellY = (m11 == -1) ? 0 : emptyCellY;
        
        m22 = macroBoard[1][1];
        emptyCellX = (m22 == -1) ? 1 : emptyCellX;
        emptyCellY = (m22 == -1) ? 1 : emptyCellY;
        
        m33 = macroBoard[2][2];
        emptyCellX = (m33 == -1) ? 2 : emptyCellX;
        emptyCellY = (m33 == -1) ? 2 : emptyCellY;
        
        if(canWeFinalClose(m11, m22, m33)) {
            closeMoves.add(makeBounds(emptyCellX * 3, emptyCellY * 3));
        }
        
        //verific daca pot inchide pe diagonala secundara
        
        m13 = macroBoard[0][2];
        emptyCellX = (m13 == -1) ? 0 : emptyCellX;
        emptyCellY = (m13 == -1) ? 2 : emptyCellY;
        
        m22 = macroBoard[1][1];
        emptyCellX = (m22 == -1) ? 1 : emptyCellX;
        emptyCellY = (m22 == -1) ? 1 : emptyCellY;
        
        m31= macroBoard[2][0];
        emptyCellX = (m31 == -1) ? 2 : emptyCellX;
        emptyCellY = (m31 == -1) ? 0 : emptyCellY;
        
        if(canWeCloseTheLine(m13, m22, m31))
            closeMoves.add(makeBounds(emptyCellX * 3, emptyCellY * 3));
        
        // daca nu am putut inchide nicio linie (diagonala)
        if(closeMoves.isEmpty())
            return null;
        
        return closeMoves;
    }
    
    // Intoarce mutarile de inchidere pe care le are adversarul.
    // Functia este identica cu cea de mai sus in care facem acelasi lucru
    // pentru noi.
    
    List<Move> getEnemyCloseMoves(Field f, Bounds b) {
        
        // Lista de mutari de inchidere pe care le are adversarul
        List<Move> enemyCloseMoves = new ArrayList<Move>();
        
        int[][] moves = f.getAvailableMoves();
        int i,j;
        int m1,m2,m3,m11,m22,m33,m13,m31;
        
        int emptyCellY = -1; // retine Y-ul celulei libere
        int emptyCellX = -1; // retine X-ul celulei libere
        
        //verific daca adversarul poate inchide pe linii
        
        for(i = b.x_min; i < b.x_max; i++) {
            
            m1 = moves[i][b.y_min]; // casuta stanga
            emptyCellY = (m1 == 0) ? b.y_min : emptyCellY;
            
            m2 = moves[i][b.y_min + 1]; //casuta mijloc
            emptyCellY = (m2 == 0) ? b.y_min + 1 : emptyCellY;
            
            m3 = moves[i][b.y_max - 1]; //casuta dreapta
            emptyCellY = (m3 == 0) ? b.y_max - 1 : emptyCellY;
            
            if(canEnemyCloseTheLine(m1, m2, m3))
                enemyCloseMoves.add(new Move(i, emptyCellY));
        }
        
        //verific daca adversarul poate inchide pe coloane
        
        for(j = b.y_min; j < b.y_max; j++) {
            
            m1=moves[b.x_min][j];//casuta sus
            emptyCellX = (m1 == 0) ? b.x_min : emptyCellX;
            
            m2=moves[b.x_min+1][j];//casuta mijloc
            emptyCellX = (m2 == 0) ? b.x_min + 1 : emptyCellX;
            
            m3=moves[b.x_max-1][j];//casuta jos
            emptyCellX = (m3 == 0) ? b.x_max - 1 : emptyCellX;
            
            if(canEnemyCloseTheLine(m1, m2, m3))
                enemyCloseMoves.add(new Move(emptyCellX, j));
        }
        
        //verific daca adversarul poate inchide pe diagonala principala
        
        m11=moves[b.x_min][b.y_min];
        emptyCellX = (m11 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m11 == 0) ? b.y_min : emptyCellY;
        
        m22=moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;
        
        m33=moves[b.x_max - 1][b.y_max - 1];
        emptyCellX = (m33 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m33 == 0) ? b.y_max - 1 : emptyCellY;
        
        if(canEnemyCloseTheLine(m11, m22, m33))
            enemyCloseMoves.add(new Move(emptyCellX, emptyCellY));
        
        
        //verific daca adversarul poate inchide pe diagonala secundara
        
        m13=moves[b.x_min][b.y_max - 1];
        emptyCellX = (m13 == 0) ? b.x_min : emptyCellX;
        emptyCellY = (m13 == 0) ? b.y_max - 1 : emptyCellY;
        
        m22=moves[b.x_min + 1][b.y_min + 1];
        emptyCellX = (m22 == 0) ? b.x_min + 1 : emptyCellX;
        emptyCellY = (m22 == 0) ? b.y_min + 1 : emptyCellY;
        
        m31=moves[b.x_max - 1][b.y_min];
        emptyCellX = (m31 == 0) ? b.x_max - 1 : emptyCellX;
        emptyCellY = (m31 == 0) ? b.y_min : emptyCellY;
        
        if(canEnemyCloseTheLine(m13, m22, m31))
            enemyCloseMoves.add(new Move(emptyCellX, emptyCellY));
        
        // daca adversarul nu a putut inchide nicio linie (diagonala)
        if(enemyCloseMoves.isEmpty())
            return null;
        
        return enemyCloseMoves;
    }
    
    public Bounds getMacroboardBounds(int x,int y)
    {
        if(x==0 || x==3 || x==6)
        {
            if(y==0 || y==3 || y==6)
                return new Bounds(0,3,0,3);
            if(y==1 || y==4 || y==7)
                return new Bounds(0,3,3,6);
            if(y==2 || y==5 || y==8)
                return new Bounds(0,3,6,9);
        }
        if(x==1 || x==4 || x==7)
        {
            if(y==0 || y==3 || y==6)
                return new Bounds(3,6,0,3);
            if(y==1 || y==4 || y==7)
                return new Bounds(3,6,3,6);
            if(y==2 || y==5 || y==8)
                return new Bounds(3,6,6,9);
        }
        if(x==2 || x==5 || x==8)
        {
            if(y==0 || y==3 || y==6)
                return new Bounds(6,9,0,3);
            if(y==1 || y==4 || y==7)
                return new Bounds(6,9,3,6);
            if(y==2 || y==5 || y==8)
                return new Bounds(6,9,6,9);
        }
        return new Bounds();
    }
    
    private Move getBestMoveToWin(List<Move> closeMoves, Field field) {
        
        Move best = closeMoves.get(0);
        
        for(int i = 1; i < closeMoves.size(); i++) {
            if(checkIfBetter(closeMoves.get(i), best, field))
                best = closeMoves.get(i);
        }
        
        
        // Aici intoarcem mutarea care trimite adversarul in microBoard-ul
        // in care noi avem dominanta maxima.
        
        return best;
    }
    
    // DUPLICATE CODE
    
    private Move getBestBlockMove(List<Move> closeMoves, Field field) {
        
        Move best = closeMoves.get(0);
        
        for(int i = 1; i < closeMoves.size(); i++) {
            if(checkIfBetter(closeMoves.get(i), best, field))
                best = closeMoves.get(i);
        }
        
        return best;
    }
    
    public Move calculate(Field field, Bounds position) {
        
        Move move = null;
        int[][] moves = field.getAvailableMoves();
        
        Bounds bestNextMove = null;
        
        // Obtinem lista de mutari de inchidere pe care le avem
        List<Move> closeMoves = getCloseMoves(field, position);
        
        if(closeMoves != null)
            return getBestMoveToWin(closeMoves, field); // returneaza mutarea de inchidere cea mai buna
        
        closeMoves = getEnemyCloseMoves(field, position);
        
        if(closeMoves != null)
            return getBestBlockMove(closeMoves, field); // returneaza mutarea de blocare cea mai buna
        
        
        // Daca sunt trimis intr-un microBoard gol, atunci actualizez empty-ul
        
        if(checkEmptyCadran(moves, position)) {
            setEmpty(getCadran(position));
        }
        
        
        for(int i = position.x_min; i<position.x_max; i++)
        {
            for(int j = position.y_min; j<position.y_max; j++)
            {
                if(moves[i][j] == 0)
                {
                    if(areEmptyCells()) {   // daca sunt microBoard-uri goale pe tabla de joc
                        
                        if(getEmptyByBounds(getMacroboardBounds(i, j)) == 0) {   // daca il trimit intr-un microBoard gol
                            
                            setEmpty(getCadran(getMacroboardBounds(i, j)));     // update pentru adversar
                            
                            return new Move(i, j);
                        }
                        
                    } else {
                        
                        //if(getEnemyCloseMoves(field, getMacroboardBounds(i, j)) != null) {
                        ;
                        //} else {
                        
                        if(bestNextMove != null) {
                            
                            if(dominance(field, bestNextMove) < dominance(field, getMacroboardBounds(i, j))) {
                                
                                bestNextMove = getMacroboardBounds(i, j);
                                move = new Move(i, j);
                                
                            }
                        } else {
                            
                            bestNextMove = getMacroboardBounds(i, j);
                            move = new Move(i, j);
                        }
                        //}
                    }
                }
            }
        }
        
        if(bestNextMove == null) {
            for (int x = position.x_min; x < position.x_max; x++)
                for (int y = position.y_min; y < position.y_max; y++) {
                    
                    if(moves[x][y] == 0) {
                        move = new Move(x, y);
                        break;
                    }
                }
        }
        
        return move;
    }
    
    public Move makeTurn(Field field) {
        
        //returneaza tabela 9x9 cu mutari de la runda curenta
        Move move = null;
        int[][] moves = field.getAvailableMoves();
        
        int y_min=0,y_max=3,x_min=0,x_max=3;
        
        if(field.atTheBeginning()) {
            
            setEmpty(getCadran(makeBounds(3, 3)));
            setEmpty(getCadran(makeBounds(1, 1)));
            
            return new Move(3, 3);  // pozitia de start
        }
        
        if(field.entireBoardAvailable()) {  // daca am la dispozitie tot field-ul
            
            
            // Layer_1 -> (default) -> inchid prima casuta care poate fi inchisa
            
            List<Move> closeMoves = null;
            
            if(field.isInActiveMicroboard(1, 1)) {
                
                closeMoves = getCloseMoves(field, makeBounds(1, 1));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(1, 4)) {
                
                closeMoves = getCloseMoves(field, makeBounds(1, 4));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(1, 7)) {
                
                closeMoves = getCloseMoves(field, makeBounds(1, 7));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(4, 1)) {
                
                closeMoves = getCloseMoves(field, makeBounds(4, 1));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(4, 4)) {
                
                closeMoves = getCloseMoves(field, makeBounds(4, 4));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(4, 7)) {
                
                closeMoves = getCloseMoves(field, makeBounds(4, 7));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(7, 1)) {
                
                closeMoves = getCloseMoves(field, makeBounds(7, 1));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(7, 4)) {
                
                closeMoves = getCloseMoves(field, makeBounds(7, 4));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            if(field.isInActiveMicroboard(7, 7)) {
                
                closeMoves = getCloseMoves(field, makeBounds(7, 7));
                
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
                
            }
            
            // Layer_3 -> daca nu a intrat pe nicio situatie, iau prima pozitie
            // libera
            
            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    if(moves[i][j] == 0 && field.isInActiveMicroboard(i, j))
                        return new Move(i, j);
                }
            }
            
        } else {
            //verifica daca macroboard-ul activ este stanga sus
            if(field.isInActiveMicroboard(1, 1))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=0;
                x_max=3;
                y_min=0;
                y_max=3;
                
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este mijloc sus
            if(field.isInActiveMicroboard(1, 4))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=0;
                x_max=3;
                y_min=3;
                y_max=6;
                
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este dreapta sus
            if(field.isInActiveMicroboard(1, 7))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=0;
                x_max=3;
                y_min=6;
                y_max=9;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este mijloc stanga
            if(field.isInActiveMicroboard(4, 1))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=3;
                x_max=6;
                y_min=0;
                y_max=3;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este mijloc mijloc
            if(field.isInActiveMicroboard(4, 4))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=3;
                x_max=6;
                y_min=3;
                y_max=6;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este mijloc dreapta
            if(field.isInActiveMicroboard(4, 7))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=3;
                x_max=6;
                y_min=6;
                y_max=9;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este stanga jos
            if(field.isInActiveMicroboard(7, 1))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=6;
                x_max=9;
                y_min=0;
                y_max=3;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                if(move!=null)
                    return move;
            }
            
            //verifica daca macroboard-ul activ este mijloc jos
            if(field.isInActiveMicroboard(7, 4))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=6;
                x_max=9;
                y_min=3;
                y_max=6;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move!=null)
                    return move;
            }
            //verifica daca macroboard-ul activ este drepta jos
            if(field.isInActiveMicroboard(7, 7))
            {
                
                //stabilesc limitele macroboard-ului in tabela mare
                x_min=6;
                x_max=9;
                y_min=6;
                y_max=9;
                //incerc sa gasesc o mutare decisiva in macroboard
                move=this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move!=null)
                    return move;
            }
            
        } // am inchis else-ul
        
        //daca nu am gasit nicio mutare decisiva pun prima mutare
        //valabila din ultimul macroboard activ gasit
        
        for (int x = x_min; x < x_max; x++)
            for (int y = y_min; y < y_max; y++)
                
                if(moves[x][y] == 0)
                    return new Move(x,y);
        
        return move;
    }
    
    
    public static void main(String[] args) {
        BotParser parser = new BotParser(new BotStarter());
        parser.run();
    }
    
    //Afla cadranul actual in care ne aflam
    public Bounds makeBounds(int x, int y){
        
        Bounds b = null;
        if((x >= 0) && (x <= 2)){
            
            if((y >= 0) && (y <= 2))
                b = new Bounds (0, 3 ,0, 3);
            else if((y >=3) && (y <= 5))
                b = new Bounds(0 ,3, 3, 6);
            else
                b = new Bounds(0, 3, 6, 9);
        }
        if((x >= 3) && (x <= 5)){
            
            if((y >= 0) && (y <= 2))
                b = new Bounds (3, 6 ,0, 3);
            else if((y >=3) && (y <= 5))
                b = new Bounds(3 ,6, 3, 6);
            else
                b = new Bounds(3, 6, 6, 9);
        }
        if((x >= 6) && (x <= 8)){
            
            if((y >= 0) && (y <= 2))
                b = new Bounds (6, 9 ,0, 3);
            else if((y >=3) && (y <= 5))
                b = new Bounds(6 ,9, 3, 6);
            else
                b = new Bounds(6, 9, 6, 9);
        }
        return b;
    }
    
    //In functie de Bounds returneaza numarul cadranului (intre 0 si 8)
    public int getCadran(Bounds b){
        
        if(b.x_min == 0){
            if(b.y_min == 0)
                return 0;
            if(b.y_min == 3)
                return 1;
            else
                return 2;
        }
        
        if(b.x_min == 3){
            if(b.y_min == 0)
                return 3;
            if(b.y_min == 3)
                return 4;
            else
                return 5;
        }
        
        if(b.x_min == 6){
            
            if(b.y_min == 0)
                return 6;
            if(b.y_min == 3)
                return 7;
            else
                return 8;
        }
        
        return 0;
    }
    
    //Seteaza 1 pe pozitia cadranului in care am introdus o noua valoare
    public void setEmpty(int pos){
        
        if(pos == 0)
            empty[0][0] = 1;
        if(pos == 1)
            empty[0][1] = 1;
        if(pos == 2)
            empty[0][2] = 1;
        if(pos == 3)
            empty[1][0] = 1;
        if(pos == 4)
            empty[1][1] = 1;
        if(pos == 5)
            empty[1][2] = 1;
        if(pos == 6)
            empty[2][0] = 1;
        if(pos == 7)
            empty[2][1] = 1;
        if(pos == 8)
            empty[2][2] = 1;
    }
    
    //Returneaza daca cadranul in care vrem sa punem este liber sau nu
    public int getEmpty(int x, int y){
        
        int pos = getCadran(makeBounds(x,y));
        int val = 0;
        
        if(pos == 0)
            val = empty[0][0];
        if(pos == 1)
            val = empty[0][1];
        if(pos == 2)
            val = empty[0][2];
        if(pos == 3)
            val = empty[1][0];
        if(pos == 4)
            val = empty[1][1];
        if(pos == 5)
            val = empty[1][2];
        if(pos == 6)
            val = empty[2][0];
        if(pos == 7)
            val = empty[2][1];
        if(pos == 8)
            val = empty[2][2];
        return val;
    }
    
    
    public int getEmptyByBounds(Bounds bounds){
        
        int pos = getCadran(bounds);
        int val = 0;
        
        if(pos == 0)
            val = empty[0][0];
        if(pos == 1)
            val = empty[0][1];
        if(pos == 2)
            val = empty[0][2];
        if(pos == 3)
            val = empty[1][0];
        if(pos == 4)
            val = empty[1][1];
        if(pos == 5)
            val = empty[1][2];
        if(pos == 6)
            val = empty[2][0];
        if(pos == 7)
            val = empty[2][1];
        if(pos == 8)
            val = empty[2][2];
        return val;
    }
}