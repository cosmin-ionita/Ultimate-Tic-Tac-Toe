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
    
    private int[][] empty;
    
    // Aceasta matrice de 3 x 3 are 1 daca microBoard-ul corespunzator
    // din field-ul mare are cel putin o piesa plasata pe el, sau 0 daca
    // respectivul microBoard este complet gol.
    
    private void initializeEmptyArray() {
        
        empty = new int[3][3];
        
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                empty[i][j] = 0;
    }
    
    public BotStarter()
    {
        initializeEmptyArray();
    }
    
    
    //Verifica daca microBoard-ul dat prin limitele sale (bonuds) este liber
    public boolean checkEmptyCadran(int[][] moves, Bounds b){
        
        for(int i = b.x_min; i < b.x_max; i++)
            for(int j = b.y_min; j < b.y_max; j++){
                
                if(moves[i][j] != 0)
                    return false;
            }
        
        return true;
    }
    
    //Verifica daca mai sunt microBoard-uri libere
    public boolean areEmptyCells(){
        
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
                if(empty[i][j] == 0)
                    return true;
        return false;
    }
    
    
    //  Verifica daca mutarea M1 este mai buna decat mutarea M2 prin analiza
    //  dominantei pe care o avem in microBoard-urile corespunzatoare.
    
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
        
        //Verificam daca putem inchide pe diagonala secundara
        
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
        
        // Daca nu am putut inchide nicio linie (diagonala), intoarcem null
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
    
    private Move getBestBlockMove(List<Move> closeMoves, Field field) {
        
        Move best = closeMoves.get(0);
        
        for(int i = 1; i < closeMoves.size(); i++) {
            if(checkIfBetter(closeMoves.get(i), best, field))
                best = closeMoves.get(i);
        }
        
        return best;
    }
    
    private Move calculate(Field field, Bounds position) {
        
        Move move = null;
        int[][] moves = field.getAvailableMoves();
        
        Bounds bestNextMove = null;
        
        // Obtinem lista de mutari de inchidere pe care le avem
        List<Move> closeMoves = getCloseMoves(field, position);
        
        if(closeMoves != null)
            return getBestMoveToWin(closeMoves, field); // returneaza mutarea de inchidere cea mai buna
        
        // Obtinem lista de mutari de inchidere pe care le are adversarul
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
                        
                        // Luam cea mai buna mutare din microBoard-ul curent
                        if(bestNextMove != null) {
                            
                            if(dominance(field, bestNextMove) < dominance(field, getMacroboardBounds(i, j))) {
                                
                                bestNextMove = getMacroboardBounds(i, j);
                                move = new Move(i, j);
                                
                            }
                        } else {
                            
                            bestNextMove = getMacroboardBounds(i, j);
                            move = new Move(i, j);
                        }
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
        
        Move move = null;                    // mutarea pe care o vom intoarce
        int[][] moves = field.getAvailableMoves();
        
        int y_min=0,y_max=3,x_min=0,x_max=3;    // coordonatele primului microBoard
        
        
        // Daca suntem la inceputul jocului, facem prima mutare
        if(field.atTheBeginning()) {
            
            setEmpty(getCadran(makeBounds(3, 3)));
            setEmpty(getCadran(makeBounds(1, 1)));
            
            return new Move(3, 3);  // pozitia de start
        }
        
        if(field.entireBoardAvailable()) {  // daca am la dispozitie tot field-ul
            
            
            // Layer_1 -> (default) -> inchid prima casuta care poate fi inchisa
            
            List<Move> closeMoves = null;
            
            // Daca sunt in microBoard-ul activ (din stanga sus) (daca nu e inchis deja)
            if(field.isInActiveMicroboard(1, 1)) {
                
                // Iau toate mutarile de inchidere (daca exista)
                closeMoves = getCloseMoves(field, makeBounds(1, 1));
                
                // Returnam mutarea de inchidere cea mai buna
                if(closeMoves != null) {
                    return getBestMoveToWin(closeMoves, field);
                }
            }
            
            // Similar pentru toate cele 8 microBoard-uri ramase
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
            
            // Layer_2 -> daca nu a intrat pe nicio situatie, iau prima pozitie libera

            for(int i = 0; i < 9; i++) {
                for(int j = 0; j < 9; j++) {
                    if(moves[i][j] == 0 && field.isInActiveMicroboard(i, j))
                        return new Move(i, j);
                }
            }
            
        } else {
            
            //Verifica daca macroboard-ul activ este stanga sus
            if(field.isInActiveMicroboard(1, 1))
            {
                
                //Stabilesc limitele macroboard-ului in tabela mare
                x_min=0;
                x_max=3;
                y_min=0;
                y_max=3;
                
                //Incerc sa gasesc o mutare decisiva in macroboard
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
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
                move = this.calculate(field, new Bounds(x_min,x_max,y_min,y_max));
                
                if(move != null)
                    return move;
            }
            
        } // Am inchis else-ul
        
        //Daca nu am gasit nicio mutare decisiva punem prima mutare
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
    
    //Afla microBoard-ul actual in care ne aflam
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
    
    //In functie de Bounds returneaza numarul microBoard-ului (intre 0 si 8)
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
    
    //Seteaza 1 pe pozitia microBoard-ului in care am introdus o noua valoare
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
    
    //Returneaza numarul de ordine al microBoard-ului in care vrem sa punem este liber sau nu
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
    
    //move m = monteCarlo(field, make_Monte(field, new Bounds(x_min,x_max,y_min,y_max)), player, player);
    //Ading stuff
    
    //Creaza o clona a field-ului actual pentru a nu modifica field-ul cat timp utilizam Monte Carlo
    public Field clone(Field old) {
        Field newField = new Field();
        int i,j;
        for(i=0;i<9;i++)
            for(j=0;j<9;j++)
                newField.mBoard[i][j]=old.mBoard[i][j];
        for(i=0;i<3;i++)
            for(j=0;j<3;j++)
                newField.mMacroboard[i][j]=old.mMacroboard[i][j];
        return newField;
    }
    
    //Monte Carlo algorithm - formeaza lista de optiune is pentru fiecare posibila
    //miscare, calculeaza un scor pe baza celor mai eficiente mutari random
    public Move monteCarlo(Field field, ArrayList<Move> options, int player, int original_player){
             
        Move next = null;
        int max = -1000;
        for(int i = 0; i < options.size(); i++){
                
            Field myClone = clone(field);
                 myClone.mBoard[options.get(i).mX][options.get(i).mY] = player;
                 if(player == 1)
                     player = 2;
                 else
                     player = 1;
                 Bounds b = getMacroboardBounds(options.get(i).mX, options.get(i).mY);
                 int val = random_value(myClone, b, player, 0, 5, original_player);
                 if(val >= max){
                    
                    max = val;
                    next = options.get(i);
                }
             }
             return next;
         }
         
         // Monte Carlo algorithm
         public int random_value(Field field, Bounds b, int player, int total, int steps, int original_player){
             
             if(steps == 0)
                 return total;
             Move move = null;
             Random randomGenerator = new Random();
             int max_score = -1000;
             ArrayList<Move> move_list = new ArrayList<Move>();
             int[][] moves = field.getAvailableMoves();
             for(int i = b.x_min; i < b.x_max; i++)
                 for(int j = b.y_min; j < b.y_max; j++){
                    
                        if(moves[i][j] == 0){
                        
                            int sc;
                            if(player == original_player)
                                sc = score (field, getMacroboardBounds(i, j));
                            else
                                sc = enemyScore(field, getMacroboardBounds(i, j));
                            if(sc >= max_score)
                                max_score = sc;
                        }
                    }
                for(int i = b.x_min; i < b.x_max; i++)
                    for(int j = b.y_min; j < b.y_max; j++)
                        if(moves[i][j] == 0){
                        
                            int sc;
                            if(player == original_player)
                                sc = score (field, getMacroboardBounds(i, j));
                            else
                                sc = enemyScore(field, getMacroboardBounds(i, j));
                            if(sc == max_score)
                                move_list.add(new Move(i,j));
                        }   
                int index = randomGenerator.nextInt(move_list.size());
                Move picked = move_list.get(index);
                total += max_score;
                steps--;
                Field myClone = clone(field);
                myClone.mBoard[picked.mX][picked.mY] = player;
                if(player == 2)
                    player = 1;
                else
                    player = 2;
                Bounds b2 = getMacroboardBounds(picked.mX, picked.mY);
                return random_value(myClone, b2, player, total, steps, original_player);
         }
        
        //Formeaza o lista de posibile viitoare mutari
        public ArrayList<Move> make_Monte(Field field, Bounds b){
            
                int max_score = -1000;
                ArrayList<Move> random_moves = new ArrayList<Move>();
                int[][] moves = field.getAvailableMoves();
                for(int i = b.x_min; i < b.x_max; i++)
                 for(int j = b.y_min; j < b.y_max; j++){
                    
                        if(moves[i][j] == 0){
                        
                            int sc = score (field, getMacroboardBounds(i, j));
                            if(sc >= max_score)
                                max_score = sc;
                        }
                    }
                for(int i = b.x_min; i < b.x_max; i++)
                    for(int j = b.y_min; j < b.y_max; j++)
                        if(moves[i][j] == 0){
                        
                            int sc = score (field, getMacroboardBounds(i, j));
                            if(sc == max_score)
                                random_moves.add(new Move(i,j));
                        }   
                return random_moves;
        }
        
        //Functia care intoarce scorul pentru mutarea noastra
        public int score(Field field, Bounds future){
            
            int total = 0;
            int dominanata = dominance(field, future);
            if(getCloseMoves(field, future) != null)
                total -= 5;
            total += dominanata;
            if(getEnemyCloseMoves(field, future) != null)
                total -= 10;
            if(field.entireBoardAvailable())
                total -= 8;
            return total;
        }
        
        //Functia care intoarce scorul pentru bot-ul rival
        public int enemyScore(Field field, Bounds future){
            
            int total = 0;
            int dominanta = 0;
            dominanta -= dominance(field, future);
            total += dominanta;
            if(getEnemyCloseMoves(field, future) != null)
                total -= 5;
            if(getCloseMoves(field, future) != null)
                total -= 10;
            if(field.entireBoardAvailable())
                total -= 8;
            return total; 
        }
}