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
    public Move calculate(int[][] moves,int x_min,int x_max,int y_min,int y_max)
    {
        Move move;
        int m1,m2,m3,m11,m22,m33,m31,m13;
        move=null;
        for (int y = y_min; y < y_max; y++) 
        {
            m1=moves[x_min][y];
            m2=moves[x_min+1][y];
            m3=moves[x_max-1][y];
            if(m1==1 && m2==1)
            {
                if(m3==0)
                {
                    move=new Move(x_max-1,y);
                    break;
                }
            }
            if(m1==2 && m2==2)
            {
                if(m3==0)
                {
                    move=new Move(x_max-1,y);
                    break;
                }
            }
            if(m1==1 && m3==1)
            {
                if(m2==0)
                {
                    move=new Move(x_min+1,y);
                    break;
                }
            }
            if(m1==2 && m3==2)
            {
                if(m2==0)
                {
                    move=new Move(x_min+1,y);
                    break;
                }
            }
            if (m3==1 && m2==1)
            {
                if(m1==0)
                {
                    move=new Move(x_min,y);
                    break;
                }
            }
            if (m3==2 && m2==2)
            {
                if(m1==0)
                {
                    move=new Move(x_min,y);
                    break;
                }
            }
        }
        if(move==null)
        {
            for (int x = x_min; x < x_max; x++) 
            {
                m1=moves[x][y_min];
                m2=moves[x][y_min+1];
                m3=moves[x][y_max-1];
                if(m1==1 && m2==1)
                {
                    if(m3==0)
                    {
                        move=new Move(x,y_max-1);
                        break;
                    }
                }
                if(m1==2 && m2==2)
                {
                    if(m3==0)
                    {
                        move=new Move(x,y_max-1);
                        break;
                    }
                }
                if(m1==1 && m3==1)
                {
                    if(m2==0)
                    {
                        move=new Move(x,y_min+1);
                        break;
                    }
                }
                if(m1==2 && m3==2)
                {
                    if(m2==0)
                    {
                        move=new Move(x,y_min+1);
                        break;
                    }
                }
                if (m3==1 && m2==1)
                {
                    if(m1==0)
                    {
                        move=new Move(x,y_min);
                        break;
                    }
                }
                if (m3==2 && m2==2)
                {
                    if(m1==0)
                    {
                        move=new Move(x,y_min);
                        break;
                    }
                }
            }
        }
        if(move==null)
        {
            m11=moves[x_min][y_min];
            m22=moves[x_min+1][y_min+1];
            m33=moves[x_max-1][y_max-1];
            m13=moves[x_min][y_max-1];
            m31=moves[x_max-1][y_min];
            if(m11==1 && m22==1)
            {
                if(m33==0)
                    return new Move(x_max-1,y_max-1);
            }
            if(m11==2 && m22==2)
            {
                if(m33==0)
                    return new Move(x_max-1,y_max-1);
            }
            if(m11==1 && m33==1)
            {
                if(m22==0)
                    return new Move(x_min+1,y_min+1);
            }
            if(m11==2 && m33==2)
            {
                if(m22==0)
                    return new Move(x_min+1,y_min+1);
            }
            if(m22==1 && m33==1)
            {
                if(m11==0)
                    return new Move(x_min,y_min);
            }
            if(m22==2 && m33==2)
            {
                if(m11==0)
                    return new Move(x_min,y_min);
            }
            if(m13==1 && m31==1)
            {
                if(m22==0)
                    return new Move(x_min+1,y_min+1);
            }
            if(m31==2 && m13==2)
            {
                if(m22==0)
                    return new Move(x_min+1,y_min+1);
            }
            if(m13==1 && m22==1)
            {
                if(m31==0)
                    return new Move(x_max-1,y_min);
            }
            if(m22==2 && m13==2)
            {
                if(m31==0)
                    return new Move(x_max-1,y_min);
            }
            if(m22==1 && m31==1)
            {
                if(m13==0)
                    return new Move(x_min,y_max-1);
            }
            if(m31==2 && m22==2)
            {
                if(m13==0)
                    return new Move(x_min,y_max-1);
            }
        }
        return move; 
    }
	public Move makeTurn(Field field) {
		int[][] moves = field.getAvailableMoves();
                Move move;
                move=null;
                int y_min=0,y_max=3,x_min=0,x_max=3;
                if(field.isInActiveMicroboard(1, 1))
                {
                    System.out.println("1");
                    x_min=0;
                    x_max=3;
                    y_min=0;
                    y_max=3;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(1, 4))
                {
                    System.out.println("2");
                    x_min=0;
                    x_max=3;
                    y_min=3;
                    y_max=6;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(1, 7))
                {
                    System.out.println("3");
                    x_min=0;
                    x_max=3;
                    y_min=6;
                    y_max=9;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(4, 1))
                {
                    System.out.println("4");
                    x_min=3;
                    x_max=6;
                    y_min=0;
                    y_max=3;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(4, 4))
                {
                    System.out.println("5");
                    x_min=3;
                    x_max=6;
                    y_min=3;
                    y_max=6;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(4, 7))
                {
                    System.out.println("6");
                    x_min=3;
                    x_max=6;
                    y_min=6;
                    y_max=9;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(7, 1))
                {
                    System.out.println("7");
                    x_min=6;
                    x_max=9;
                    y_min=0;
                    y_max=3;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(7, 4))
                {
                    System.out.println("8");
                    x_min=6;
                    x_max=9;
                    y_min=3;
                    y_max=6;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                if(field.isInActiveMicroboard(7, 7))
                {
                    System.out.println("9");
                    x_min=6;
                    x_max=9;
                    y_min=6;
                    y_max=9;
                    move=this.calculate(moves,x_min,x_max,y_min,y_max);
                    if(move!=null)
                        return move;
                }
                for (int x = x_min; x < x_max; x++)
                    for (int y = y_min; y < y_max; y++)
                        if(moves[x][y]==0)
                            return new Move(x,y);
                return move;
	}


	public static void main(String[] args) {
		BotParser parser = new BotParser(new BotStarter());
		parser.run();
	}
}
