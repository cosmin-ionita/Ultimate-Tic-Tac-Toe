/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ultimate.tic.tac.toe;

/**
 *
 * @author Adrian
 */
public class Bounds {
	int x_min,x_max,y_min,y_max;
	
	public Bounds() {
	}
	
	public Bounds(int x_min, int x_max, int y_min,int y_max) {
            this.x_min=x_min;
            this.y_min=y_min;
            this.x_max=x_max;
            this.y_max=y_max;
	}
	
	public int getX_MIN() { return x_min; }
	public int getY_MIN() { return y_min; }
        public int getX_MAX() { return x_max; }
	public int getY_MAX() { return y_max; }
	
}
