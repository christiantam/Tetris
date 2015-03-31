import java.util.*;
import javax.swing.*;
import java.applet.*;
import java.awt.*;

//Christian Tam
//ICS3U-1
//Friday, April 8, 2011

//The game I made for the computer science summative this year is tetris.
//The objective of this game is to survive as along as possible by moving and rotating pieces composed of 4 blocks to create lines.
//You move and rotate the pieces using the arrow keys (left and right are to move, up is to rotate clockwise), you can also press space to drop the 
//piece instantly to the bottom.
//Points are awarded when a piece hits the bottom, and when you create lines. The more lines you successfully "destroy" at a time, the higher the score
//bonus.
//You lose when the blocks pile up to the top.


public class Tetris extends Applet implements Runnable{
	//creates the offScreen variable for offG
	//creates variables for the background, pause, and the pieces
	Image offScreen, background, pause, blue, green, lblue, orange, purple, red, yellow;
	//creates variables for the images that show the next piece, and the score ,level, lines, and sound pictures
	Image nblue, ngreen, nlblue, norange, npurple, nred, nyellow, ilevel, iscore, ilines, ipause, soundoff, soundon;
	//random variable to determine the next piece
	Random rnd;
	//creates the fake screen
	Graphics offG;
	//creates the image tracker
	MediaTracker tracker;
	//creates integer variables for game status, the drop button
	//and xy cooridinates for redrawing
	int game, nextX, nextY, scoreX, scoreY, linesX, linesY, soundX, soundY, levelX, levelY, dropplay, dropped;
	//creates integer variables for a random number, level, score, number of lines, what rotation number the piece is at
	//the width of the block, whether sound is on, which piece it is, if the piece has landed, if pause is in effect, pause duration before
	//each block, and a variable to see if a line was made
	int random, level, score, lines, rotation, blockwidth, sound, piece, landing, xpause, duration, chkline;
	//creates integer variables for the xy values of each block
	//and the amount of lines made with one piece, and and accumulator for the level
	int block1X, block2X, block3X, block4X, block1Y, block2Y, block3Y, block4Y, lineamt, multiple, lvlcounter;
	//creates integer arrays for the xy coordinates of the blocks
	int [] c;
	int [] r;
	//creates a 2d array for the tetris board
	int [] [] block;
	//creates variables for each sound
	AudioClip line, lose, rotate, drop, land;
	//creates string variables for the level, score, and lines
	String slevel, sscore, slines;
	//??
	Thread myThread=null;
	String keyStr=null;
	Font gameFont; 
	
	public void init(){
		//import audio
		line = getAudioClip(getCodeBase(),"line.wav");
		land = getAudioClip(getCodeBase(),"land.wav");
		lose = getAudioClip(getCodeBase(),"lose.wav");
		rotate = getAudioClip(getCodeBase(),"rotate.wav");
		drop = getAudioClip(getCodeBase(),"drop.wav");
		
		//import pictures
		offScreen = createImage(627,609);
		offG = offScreen.getGraphics();
		background = getImage(getCodeBase(), "background.JPG");
		iscore = getImage(getCodeBase(), "score.JPG");
		ilines = getImage(getCodeBase(), "lines.JPG");
		ipause = getImage(getCodeBase(), "pause.JPG");
		ilevel = getImage(getCodeBase(), "level.JPG");
		soundon = getImage(getCodeBase(), "soundon.JPG");
		soundoff = getImage(getCodeBase(), "soundoff.JPG");
		
		blue = getImage(getCodeBase(), "blue.JPG");
		green = getImage(getCodeBase(), "green.JPG");
		lblue = getImage(getCodeBase(), "lblue.jpg");
		orange = getImage(getCodeBase(), "orange.jpg");
		purple = getImage(getCodeBase(), "purple.jpg");
		red = getImage(getCodeBase(), "red.jpg");
		yellow = getImage(getCodeBase(), "yellow.jpg");
		
		nblue = getImage(getCodeBase(), "nblue.jpg");
		ngreen = getImage(getCodeBase(), "ngreen.jpg");
		nlblue = getImage(getCodeBase(), "nlblue.jpg");
		norange = getImage(getCodeBase(), "norange.jpg");
		npurple = getImage(getCodeBase(), "npurple.jpg");
		nred = getImage(getCodeBase(), "nred.jpg");
		nyellow = getImage(getCodeBase(), "nyellow.jpg");
		
		//x coordinates for the blocks
		c = new int[10];
		c[0] = 7;
		c[1] = 40;
		c[2] = 73;
		c[3] = 106;
		c[4] = 139;
		c[5] = 172;
		c[6] = 205;
		c[7] = 238;
		c[8] = 271;
		c[9] = 303;
		
		//y coordinates for the blocks
		r = new int[18];
		r[16] = 10;
		r[15] = 45;
		r[14] = 80;
		r[13] = 115;
		r[12] = 150;
		r[11] = 185;
		r[10] = 220;
		r[9] = 255;
		r[8] = 290;
		r[7] = 325;
		r[6] = 360;
		r[5] = 395;
		r[4] = 430;
		r[3] = 465;
		r[2] = 500;
		r[1] = 535;
		r[0] = 570;
				
		//xy coordinates for the level, score, next piece, lines
		nextX = 353;
		nextY = 1;
		scoreX = 353;
		scoreY = 210;
		soundX = 345;
		soundY = 347;
		linesX = 353;
		linesY = 275;
		levelX = 353;
		levelY = 146;
		
		block = new int [10] [17];
		//make all 0 here
		for(int d = 0; d < 17; d++){
			for(int i = 0; i < 10; i++){
				block [i][d] = 0;
			}
		}
		
		//font
		gameFont = new Font("Copperplate Gothic Light", Font.BOLD, 18);
		
		//make some other stuff zero
		rnd = new Random();
		random = rnd.nextInt(6);
		
		//stuff like to see if it was "spaced", pause is in effect, duration between each drop, sound, how many lines are completed with one piece
		//and the counter for 
		dropped = 0;
		xpause = 0;
		duration = 500;
		game = 0;
		sound = 1;
		landing = 0;
		chkline = 0;
		lineamt = 0;
		multiple = 0;
		lvlcounter = 0;
		
		//sets xy for the blocks to 0	
		block1X = 0;
		block2X = 0;
		block3X = 0;
		block4X = 0;
		block1Y = 0;
		block2Y = 0;
		block3Y = 0;
		block4Y = 0;
		
		keyStr="";
		
		//convert lines, score, level to strings
		level = 1;
		slevel = "" + level;
		score = 0;
		sscore = "" + score;
		lines = 0;
		slines = "" + lines;
		
		//track all the images
		tracker = new MediaTracker(this);
		tracker.addImage(background,0);
		tracker.addImage(blue,0);
		tracker.addImage(green,0);
		tracker.addImage(lblue,0);
		tracker.addImage(orange,0);
		tracker.addImage(purple,0);
		tracker.addImage(red,0);
		tracker.addImage(yellow,0);
		tracker.addImage(nblue,0);
		tracker.addImage(ngreen,0);
		tracker.addImage(nlblue,0);
		tracker.addImage(norange,0);
		tracker.addImage(npurple,0);
		tracker.addImage(nred,0);
		tracker.addImage(nyellow,0);
		tracker.addImage(iscore,0);
		tracker.addImage(ilines,0);
		tracker.addImage(ipause,0);
		tracker.addImage(soundoff,0);
		tracker.addImage(soundon,0);
		
		while(tracker.checkAll(true) !=true){}
		if(tracker.isErrorAny()){
			JOptionPane.showMessageDialog(null,"Trouble loading pictures.");
		}
		
		//draws background
		offG.drawImage(background,0,0,this);
	}
	
	public void run(){
		//sets the rnd variable for the first piece
		first();
		
		while(game == 0 && xpause != 1){
			//draws the intial piece at the top
			if(random == 1){
				//blue
				blue();
				piece = 1;
			}
			else if(random == 2){
				//green
				green();
				piece = 2;
			}
			else if(random == 3){
				//lblue
				lblue();
				piece = 3;
			}
			else if(random == 4){
				//orange
				orange();
				piece = 4;
			}
			else if(random == 5){
				//purple
				purple();
				piece = 5;
			}
			else if(random == 6){
				//red
				red();
				piece = 6;
			}
			else if(random == 7){
				//yellow
				yellow();
				piece = 7;
			}
			
			//randoms a number for the next piece
			random = rnd.nextInt(7) + 1;
			//draws the next piece
			//blue, green, lblue, orange, purple, red, yellow
			if(random == 1){
				//blue
				offG.drawImage(nblue,nextX,nextY,this);
				repaint();
			}
			else if(random == 2){
				//green
				offG.drawImage(ngreen,nextX,nextY,this);
				repaint();
			}
			else if(random == 3){
				//lblue
				offG.drawImage(nlblue,nextX,nextY,this);
				repaint();
			}
			else if(random == 4){
				//orange
				offG.drawImage(norange,nextX,nextY,this);
				repaint();
			}
			else if(random == 5){
				//purple
				offG.drawImage(npurple,nextX,nextY,this);
				repaint();
			}
			else if(random == 6){
				//red
				offG.drawImage(nred,nextX,nextY,this);
				repaint();
			}
			else if(random == 7){
				//yellow
				offG.drawImage(nyellow,nextX,nextY,this);
				repaint();
			}
			
			//makes the landing variable 0
			landing = 0;
			
			//make the piece fall 1 block
			while(landing == 0 && xpause != 1){
				//checks if it is dropped
				if(dropped != 1){
					sleep();
				}
				
				//checks if it has landed
				if(block1Y == 0 || block2Y == 0 || block3Y == 0 || block4Y == 0){
					landing = 1;
				}
				if(block1Y != 0){				
					if(block [block1X] [block1Y-1] != 0){
						landing = 1;
					}
				}
				if(block2Y != 0){				
					if(block [block2X] [block2Y-1] != 0){
						landing = 1;
					}
				}
				if(block3Y != 0){				
					if(block [block3X] [block3Y-1] != 0){
						landing = 1;
					}
				}
				if(block4Y != 0){				
					if(block [block4X] [block4Y-1] != 0){
						landing = 1;
					}
				}
				
				//drops the piece 1 block
				if(block1Y != 0 && block2Y != 0 && block3Y != 0 && block4Y != 0){
					if(block[block1X][block1Y-1]==0&& block[block2X][block2Y-1] == 0 && block[block3X][block3Y-1] == 0 && block[block4X][block4Y-1]== 0){					
						block1Y--;
						block2Y--;
						block3Y--;
						block4Y--;
						redraw();					
					}
				}
				dropped = 0;
				
				if(piece == 1){
					//blue
					offG.drawImage(blue,c[block1X],r[block1Y],this);
					offG.drawImage(blue,c[block2X],r[block2Y],this);
					offG.drawImage(blue,c[block3X],r[block3Y],this);
					offG.drawImage(blue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 2){
					//green
					offG.drawImage(green,c[block1X],r[block1Y],this);
					offG.drawImage(green,c[block2X],r[block2Y],this);
					offG.drawImage(green,c[block3X],r[block3Y],this);
					offG.drawImage(green,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 3){
					//lblue
					offG.drawImage(lblue,c[block1X],r[block1Y],this);
					offG.drawImage(lblue,c[block2X],r[block2Y],this);
					offG.drawImage(lblue,c[block3X],r[block3Y],this);
					offG.drawImage(lblue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 4){
					//orange
					offG.drawImage(orange,c[block1X],r[block1Y],this);
					offG.drawImage(orange,c[block2X],r[block2Y],this);
					offG.drawImage(orange,c[block3X],r[block3Y],this);
					offG.drawImage(orange,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 5){
					//purple
					offG.drawImage(purple,c[block1X],r[block1Y],this);
					offG.drawImage(purple,c[block2X],r[block2Y],this);
					offG.drawImage(purple,c[block3X],r[block3Y],this);
					offG.drawImage(purple,c[block4X],r[block4Y],this);
					repaint();	
				}
				else if(piece == 6){
					//red
					offG.drawImage(red,c[block1X],r[block1Y],this);
					offG.drawImage(red,c[block2X],r[block2Y],this);
					offG.drawImage(red,c[block3X],r[block3Y],this);
					offG.drawImage(red,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 7){
					//yellow
					offG.drawImage(yellow,c[block1X],r[block1Y],this);
					offG.drawImage(yellow,c[block2X],r[block2Y],this);
					offG.drawImage(yellow,c[block3X],r[block3Y],this);
					offG.drawImage(yellow,c[block4X],r[block4Y],this);
					repaint();
				}	
			}
			
			//record colour of piece that landed
			block [block1X] [block1Y] = piece;
			block [block2X] [block2Y] = piece;
			block [block3X] [block3Y] = piece;
			block [block4X] [block4Y] = piece;
			rotation = 0;
			
			//plays landing sound
			if(sound == 1 && dropplay == 0){
				land.play();
			}
			//resets dropplay, increases the score
			dropplay = 0;
			score += 14;
			sscore = "" + score;
			//check if there is a line
			line();
		}
		stop();
	}
	
	public void first(){
		//didn't really need this
		random = rnd.nextInt(7) + 1;
		//blue, green, lblue, orange, purple, red, yellow
		return;
	}
	
	public void paint(Graphics g){
		//paint the stuff
		g.drawImage(offScreen,0,0,this);
	}
	
	public void start() {
		//?
		if(myThread == null) {
			myThread = new Thread(this);
			myThread.start();
		}
	}
	
	public void stop() {
		//?
		if(myThread != null){
			myThread = null;
		}
	}
	
	public void update(Graphics g){
		//?
		paint(g);
	}
	
	public boolean keyDown(Event e, int key) {
		//Convert ASCII integer value to a String
		String entered = ""+(char)key;
		keyStr = entered;
		
		//dropppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp
		//copy and pasted from run() refer back to run() if you need to see the comments
		if(entered.equals(" ")){
			if(xpause != 1 && game == 0){
				if(block1Y == 0 || block2Y == 0 || block3Y == 0 || block4Y == 0){
					dropped = 1;
				}
				
				while(landing == 0){
					dropplay = 0;
					dropped = 1;
					if(block1Y == 0 || block2Y == 0 || block3Y == 0 || block4Y == 0){
						landing = 1;
					}
					if(block1Y != 0){				
						if(block [block1X] [block1Y-1] != 0){
							landing = 1;
						}
					}
					if(block2Y != 0){				
						if(block [block2X] [block2Y-1] != 0){
							landing = 1;
						}
					}
					if(block3Y != 0){				
						if(block [block3X] [block3Y-1] != 0){
							landing = 1;
						}
					}
					if(block4Y != 0){				
						if(block [block4X] [block4Y-1] != 0){
							landing = 1;
						}
					}
					if(block1Y != 0 && block2Y != 0 && block3Y != 0 && block4Y != 0){
						if(block[block1X][block1Y-1]==0&& block[block2X][block2Y-1] == 0 && block[block3X][block3Y-1] == 0 && block[block4X][block4Y-1]== 0){
							block1Y--;
							block2Y--;
							block3Y--;
							block4Y--;
							redraw();
						}
					}
					
					if(piece == 1){
						//blue
						offG.drawImage(blue,c[block1X],r[block1Y],this);
						offG.drawImage(blue,c[block2X],r[block2Y],this);
						offG.drawImage(blue,c[block3X],r[block3Y],this);
						offG.drawImage(blue,c[block4X],r[block4Y],this);
						repaint();
					}
					else if(piece == 2){
						//green
						offG.drawImage(green,c[block1X],r[block1Y],this);
						offG.drawImage(green,c[block2X],r[block2Y],this);
						offG.drawImage(green,c[block3X],r[block3Y],this);
						offG.drawImage(green,c[block4X],r[block4Y],this);
						repaint();
					}
					else if(piece == 3){
						//lblue
						offG.drawImage(lblue,c[block1X],r[block1Y],this);
						offG.drawImage(lblue,c[block2X],r[block2Y],this);
						offG.drawImage(lblue,c[block3X],r[block3Y],this);
						offG.drawImage(lblue,c[block4X],r[block4Y],this);
						repaint();
					}
					else if(piece == 4){
						//orange
						offG.drawImage(orange,c[block1X],r[block1Y],this);
						offG.drawImage(orange,c[block2X],r[block2Y],this);
						offG.drawImage(orange,c[block3X],r[block3Y],this);
						offG.drawImage(orange,c[block4X],r[block4Y],this);
						repaint();
					}
					else if(piece == 5){
						//purple
						offG.drawImage(purple,c[block1X],r[block1Y],this);
						offG.drawImage(purple,c[block2X],r[block2Y],this);
						offG.drawImage(purple,c[block3X],r[block3Y],this);
						offG.drawImage(purple,c[block4X],r[block4Y],this);
						repaint();	
					}
					else if(piece == 6){
						//red
						offG.drawImage(red,c[block1X],r[block1Y],this);
						offG.drawImage(red,c[block2X],r[block2Y],this);
						offG.drawImage(red,c[block3X],r[block3Y],this);
						offG.drawImage(red,c[block4X],r[block4Y],this);
						repaint();
					}
					else if(piece == 7){
						//yellow
						offG.drawImage(yellow,c[block1X],r[block1Y],this);
						offG.drawImage(yellow,c[block2X],r[block2Y],this);
						offG.drawImage(yellow,c[block3X],r[block3Y],this);
						offG.drawImage(yellow,c[block4X],r[block4Y],this);
						repaint();
					}
				}				
				
				//record colour of piece that landed
				block [block1X] [block1Y] = piece;
				block [block2X] [block2Y] = piece;
				block [block3X] [block3Y] = piece;
				block [block4X] [block4Y] = piece;
				rotation = 0;				
			}
			//plays the drop sound and sets dropplay to 1
			drop.play();
			dropplay = 1;
		}
		
		//mute or unmute
		if(entered.equals("m")) {
			if(sound == 0){
				sound = 1;
				offG.drawImage(soundon,soundX,soundY,this);
				repaint();
			}
			else if(sound == 1){
				sound = 0;
				offG.drawImage(soundon,soundX-2,soundY-2,this);
				repaint();
			}
		}
		
		//pause
		if(entered.equals("p")) {
			pause();
		}
		
		//rotate the piece
		if(key == Event.UP){
			if(xpause != 1 && game == 0){
				if(sound == 1){
					rotate.play();
				}
				rotate();
			}	
		}
		//drop the piece one block
		else if(key == Event.DOWN){
			if(xpause != 1 && game == 0){
				if(block1Y == 0 || block2Y == 0 || block3Y == 0 || block4Y == 0){
					landing = 1;
				}
				else if(block1Y != 0){				
					if(block [block1X] [block1Y-1] > 0){
						landing = 1;
					}
				}
				else if(block1Y != 0){				
					if(block [block2X] [block2Y-1] > 0){
						landing = 1;
					}
				}
				else if(block1Y != 0){				
					if(block [block3X] [block3Y-1] > 0){
						landing = 1;
					}
				}
				else if(block1Y != 0){				
					if(block [block4X] [block4Y-1] > 0){
						landing = 1;
					}
				}
				
				//drops the piece 1 block
				if(block1Y != 0 && block2Y != 0 && block3Y != 0 && block4Y != 0){
					if(block[block1X][block1Y-1]==0&& block[block2X][block2Y-1] == 0 && block[block3X][block3Y-1] == 0 && block[block4X][block4Y-1]== 0){
						block1Y--;
						block2Y--;
						block3Y--;
						block4Y--;
						redraw();
					}
				}
				
				if(piece == 1){
					//blue
					offG.drawImage(blue,c[block1X],r[block1Y],this);
					offG.drawImage(blue,c[block2X],r[block2Y],this);
					offG.drawImage(blue,c[block3X],r[block3Y],this);
					offG.drawImage(blue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 2){
					//green
					offG.drawImage(green,c[block1X],r[block1Y],this);
					offG.drawImage(green,c[block2X],r[block2Y],this);
					offG.drawImage(green,c[block3X],r[block3Y],this);
					offG.drawImage(green,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 3){
					//lblue
					offG.drawImage(lblue,c[block1X],r[block1Y],this);
					offG.drawImage(lblue,c[block2X],r[block2Y],this);
					offG.drawImage(lblue,c[block3X],r[block3Y],this);
					offG.drawImage(lblue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 4){
					//orange
					offG.drawImage(orange,c[block1X],r[block1Y],this);
					offG.drawImage(orange,c[block2X],r[block2Y],this);
					offG.drawImage(orange,c[block3X],r[block3Y],this);
					offG.drawImage(orange,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 5){
					//purple
					offG.drawImage(purple,c[block1X],r[block1Y],this);
					offG.drawImage(purple,c[block2X],r[block2Y],this);
					offG.drawImage(purple,c[block3X],r[block3Y],this);
					offG.drawImage(purple,c[block4X],r[block4Y],this);
					repaint();	
				}
				else if(piece == 6){
					//red
					offG.drawImage(red,c[block1X],r[block1Y],this);
					offG.drawImage(red,c[block2X],r[block2Y],this);
					offG.drawImage(red,c[block3X],r[block3Y],this);
					offG.drawImage(red,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 7){
					//yellow
					offG.drawImage(yellow,c[block1X],r[block1Y],this);
					offG.drawImage(yellow,c[block2X],r[block2Y],this);
					offG.drawImage(yellow,c[block3X],r[block3Y],this);
					offG.drawImage(yellow,c[block4X],r[block4Y],this);
					repaint();
				}
			}
		}
		//move the piece to the right one block
		else if(key == Event.RIGHT){
			if(xpause != 1 && game == 0 && landing != 1){
				redraw();
				if(block1X != 9 && block2X != 9 && block3X != 9 && block4X != 9){
					if(block[block1X+1][block1Y] == 0 && block[block2X+1][block2Y] == 0 && block[block3X+1][block3Y]==0&&block[block4X+1][block4Y]==0){
						block1X++;
						block2X++;
						block3X++;
						block4X++;
					}
				}
				if(piece == 1){
					//blue
					offG.drawImage(blue,c[block1X],r[block1Y],this);
					offG.drawImage(blue,c[block2X],r[block2Y],this);
					offG.drawImage(blue,c[block3X],r[block3Y],this);
					offG.drawImage(blue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 2){
					//green
					offG.drawImage(green,c[block1X],r[block1Y],this);
					offG.drawImage(green,c[block2X],r[block2Y],this);
					offG.drawImage(green,c[block3X],r[block3Y],this);
					offG.drawImage(green,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 3){
					//lblue
					offG.drawImage(lblue,c[block1X],r[block1Y],this);
					offG.drawImage(lblue,c[block2X],r[block2Y],this);
					offG.drawImage(lblue,c[block3X],r[block3Y],this);
					offG.drawImage(lblue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 4){
					//orange
					offG.drawImage(orange,c[block1X],r[block1Y],this);
					offG.drawImage(orange,c[block2X],r[block2Y],this);
					offG.drawImage(orange,c[block3X],r[block3Y],this);
					offG.drawImage(orange,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 5){
					//purple
					offG.drawImage(purple,c[block1X],r[block1Y],this);
					offG.drawImage(purple,c[block2X],r[block2Y],this);
					offG.drawImage(purple,c[block3X],r[block3Y],this);
					offG.drawImage(purple,c[block4X],r[block4Y],this);	
					repaint();	
				}
				else if(piece == 6){
					//red
					offG.drawImage(red,c[block1X],r[block1Y],this);
					offG.drawImage(red,c[block2X],r[block2Y],this);
					offG.drawImage(red,c[block3X],r[block3Y],this);
					offG.drawImage(red,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 7){
					//yellow
					offG.drawImage(yellow,c[block1X],r[block1Y],this);
					offG.drawImage(yellow,c[block2X],r[block2Y],this);
					offG.drawImage(yellow,c[block3X],r[block3Y],this);
					offG.drawImage(yellow,c[block4X],r[block4Y],this);
					repaint();
				}
			}
		}
		//move the piece to the left one block
		else if(key == Event.LEFT){
			if(xpause != 1 && game == 0 && landing != 1){
				redraw();
				if(block1X != 0 && block2X != 0 && block3X != 0 && block4X != 0){
					if(block[block1X-1][block1Y] == 0 && block[block2X-1][block2Y] == 0 && block[block3X-1][block3Y]==0&&block[block4X-1][block4Y]==0){
						block1X--;
						block2X--;
						block3X--;
						block4X--;
					}
				}
				if(piece == 1){
					//blue
					offG.drawImage(blue,c[block1X],r[block1Y],this);
					offG.drawImage(blue,c[block2X],r[block2Y],this);
					offG.drawImage(blue,c[block3X],r[block3Y],this);
					offG.drawImage(blue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 2){
					//green
					offG.drawImage(green,c[block1X],r[block1Y],this);
					offG.drawImage(green,c[block2X],r[block2Y],this);
					offG.drawImage(green,c[block3X],r[block3Y],this);
					offG.drawImage(green,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 3){
					//lblue
					offG.drawImage(lblue,c[block1X],r[block1Y],this);
					offG.drawImage(lblue,c[block2X],r[block2Y],this);
					offG.drawImage(lblue,c[block3X],r[block3Y],this);
					offG.drawImage(lblue,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 4){
					//orange
					offG.drawImage(orange,c[block1X],r[block1Y],this);
					offG.drawImage(orange,c[block2X],r[block2Y],this);
					offG.drawImage(orange,c[block3X],r[block3Y],this);
					offG.drawImage(orange,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 5){
					//purple
					offG.drawImage(purple,c[block1X],r[block1Y],this);
					offG.drawImage(purple,c[block2X],r[block2Y],this);
					offG.drawImage(purple,c[block3X],r[block3Y],this);
					offG.drawImage(purple,c[block4X],r[block4Y],this);
					repaint();	
				}
				else if(piece == 6){
					//red
					offG.drawImage(red,c[block1X],r[block1Y],this);
					offG.drawImage(red,c[block2X],r[block2Y],this);
					offG.drawImage(red,c[block3X],r[block3Y],this);
					offG.drawImage(red,c[block4X],r[block4Y],this);
					repaint();
				}
				else if(piece == 7){
					//yellow
					offG.drawImage(yellow,c[block1X],r[block1Y],this);
					offG.drawImage(yellow,c[block2X],r[block2Y],this);
					offG.drawImage(yellow,c[block3X],r[block3Y],this);
					offG.drawImage(yellow,c[block4X],r[block4Y],this);
					repaint();
				}
			}
		}		
		return true;
	}
	
	public boolean mouseDown(Event evt, int x, int y){
		//stuff for quit, pause, mute
		//sound
		if((x > soundX && x < soundX + 61) && (y > soundY && y < soundY + 81)){
			if(sound == 0){
				sound = 1;
				offG.drawImage(soundon,soundX,soundY,this);
			}
			else if(sound == 1){
				sound = 0;
				offG.drawImage(soundon,soundX-2,soundY-2,this);
			}
		}
		
		//pause
		if((x > 403 && x < 516) && (y > 371 && y < 411)){
			pause();
		}
		
		//quit
		if((x > 536 && x < 615) && (y > 371 && y < 411)){
			quit();
		}
		return true;
	}
	
	public void pause(){
		//pauses the game
		if(game == 0){
			if(xpause == 0){
				xpause = 1;
				System.out.print("P");
				repaint();
			}
			else if(xpause == 1){
				redraw();
				System.out.print("POn");
				if(piece == 1){
					//blue
					offG.drawImage(blue,c[block1X],r[block1Y],this);
					offG.drawImage(blue,c[block2X],r[block2Y],this);
					offG.drawImage(blue,c[block3X],r[block3Y],this);
					offG.drawImage(blue,c[block4X],r[block4Y],this);
				}
				else if(piece == 2){
					//green
					offG.drawImage(green,c[block1X],r[block1Y],this);
					offG.drawImage(green,c[block2X],r[block2Y],this);
					offG.drawImage(green,c[block3X],r[block3Y],this);
					offG.drawImage(green,c[block4X],r[block4Y],this);
				}
				else if(piece == 3){
					//lblue
					offG.drawImage(lblue,c[block1X],r[block1Y],this);
					offG.drawImage(lblue,c[block2X],r[block2Y],this);
					offG.drawImage(lblue,c[block3X],r[block3Y],this);
					offG.drawImage(lblue,c[block4X],r[block4Y],this);
				}
				else if(piece == 4){
					//orange
					offG.drawImage(orange,c[block1X],r[block1Y],this);
					offG.drawImage(orange,c[block2X],r[block2Y],this);
					offG.drawImage(orange,c[block3X],r[block3Y],this);
					offG.drawImage(orange,c[block4X],r[block4Y],this);
				}
				else if(piece == 5){
					//purple
					offG.drawImage(purple,c[block1X],r[block1Y],this);
					offG.drawImage(purple,c[block2X],r[block2Y],this);
					offG.drawImage(purple,c[block3X],r[block3Y],this);
					offG.drawImage(purple,c[block4X],r[block4Y],this);	
				}
				else if(piece == 6){
					//red
					offG.drawImage(red,c[block1X],r[block1Y],this);
					offG.drawImage(red,c[block2X],r[block2Y],this);
					offG.drawImage(red,c[block3X],r[block3Y],this);
					offG.drawImage(red,c[block4X],r[block4Y],this);
				}
				else if(piece == 7){
					//yellow
					offG.drawImage(yellow,c[block1X],r[block1Y],this);
					offG.drawImage(yellow,c[block2X],r[block2Y],this);
					offG.drawImage(yellow,c[block3X],r[block3Y],this);
					offG.drawImage(yellow,c[block4X],r[block4Y],this);
				}
				repaint();
				xpause = 0;
			}
		}
	}
	
	public void sleep(){
		//interval between each time the block drops
		try{
			myThread.sleep(duration);
		}
		catch(Exception e){}
		return;
	}
	
	public void rotate(){
		//i hated this part
		//checks what piece it is, and at which rotation it is
		if(landing != 1){
			//all of these first check if the rotation will rotate into a block, then if it doesn't, it will rotate and redraw the piece
			if(piece == 1){
				//blue
				if(rotation == 0){
					if((block[block1X][block1Y+1] == 0) && (block[block2X][block2Y+1] == 0) && (block[block2X][block2Y-1] == 0)){
						redraw();
						
						block1X++;
						block3X--;
						block1Y++;
						block3Y--;
						block4Y += 2;
						
						offG.drawImage(blue,c[block1X],r[block1Y],this);
						offG.drawImage(blue,c[block2X],r[block2Y],this);
						offG.drawImage(blue,c[block3X],r[block3Y],this);
						offG.drawImage(blue,c[block4X],r[block4Y],this);
						
						rotation = 1;
						repaint();
					}
				}
				else if(rotation == 1){
					if((block[block2X-1][block2Y] == 0) && (block[block2X+1][block2Y] == 0) && (block[block2X+1][block2Y+1] == 0)){
						redraw();
						
						block1X++;
						block3X--;
						block4X += 2;
						block1Y--;
						block3Y++;
						
						offG.drawImage(blue,c[block1X],r[block1Y],this);
						offG.drawImage(blue,c[block2X],r[block2Y],this);
						offG.drawImage(blue,c[block3X],r[block3Y],this);
						offG.drawImage(blue,c[block4X],r[block4Y],this);
						
						rotation = 2;
						repaint();
					}
				}
				else if(rotation == 2){
					if((block[block2X][block2Y+1] == 0) && (block[block2X][block2Y-1] == 0) && (block[block2X+1][block2Y-1] == 0)){
						redraw();
						
						block1X--;
						block3X++;
						block1Y--;
						block3Y++;
						block4Y -= 2;
						
						offG.drawImage(blue,c[block1X],r[block1Y],this);
						offG.drawImage(blue,c[block2X],r[block2Y],this);
						offG.drawImage(blue,c[block3X],r[block3Y],this);
						offG.drawImage(blue,c[block4X],r[block4Y],this);
						
						rotation = 3;
						repaint();
					}
				}
				else if(rotation == 3){
					if((block[block2X-1][block2Y-1] == 0) && (block[block2X-1][block2Y] == 0) && (block[block2X+1][block2Y] == 0)){
						redraw();
						
						block1X--;
						block3X++;
						block4X -= 2;
						block1Y++;
						block3Y--;
						
						offG.drawImage(blue,c[block1X],r[block1Y],this);
						offG.drawImage(blue,c[block2X],r[block2Y],this);
						offG.drawImage(blue,c[block3X],r[block3Y],this);
						offG.drawImage(blue,c[block4X],r[block4Y],this);
						
						rotation = 0;
						repaint();
					}
				}
			}
			else if(piece == 2){
				//green
				if(rotation == 0){
					if((block[block2X+1][block2Y+1] == 0) && (block[block2X+1][block2Y] == 0)){
						redraw();
						
						block1X += 2;
						block2X++;
						block4X--;
						block1Y++;
						block3Y++;
						
						offG.drawImage(green,c[block1X],r[block1Y],this);
						offG.drawImage(green,c[block2X],r[block2Y],this);
						offG.drawImage(green,c[block3X],r[block3Y],this);
						offG.drawImage(green,c[block4X],r[block4Y],this);
						
						rotation = 1;
						repaint();
					}
				}
				else if(rotation == 1){
					if((block[block3X-1][block3Y] == 0) && (block[block3X+1][block3Y-1] == 0)){
						redraw();
						
						block1X -= 2;
						block2X--;
						block4X++;
						block1Y--;
						block3Y--;
						
						offG.drawImage(green,c[block1X],r[block1Y],this);
						offG.drawImage(green,c[block2X],r[block2Y],this);
						offG.drawImage(green,c[block3X],r[block3Y],this);
						offG.drawImage(green,c[block4X],r[block4Y],this);
						
						rotation = 0;
						repaint();
					}
				}
			}
			else if(piece == 3){
				//lblue
				if(rotation == 0){
					if((block[block2X+1][block2Y] == 0) && (block[block2X+1][block2Y-1] == 0)){
						redraw();
						
						block1X+=2;
						block2X++;
						block4X--;
						block2Y--;
						block4Y--;
						
						offG.drawImage(lblue,c[block1X],r[block1Y],this);
						offG.drawImage(lblue,c[block2X],r[block2Y],this);
						offG.drawImage(lblue,c[block3X],r[block3Y],this);
						offG.drawImage(lblue,c[block4X],r[block4Y],this);
						
						rotation = 1;
						repaint();
					}
				}
				else if(rotation == 1){
					if((block[block2X+1][block2Y+1] == 0) && (block[block2X-1][block2Y] == 0)){
						redraw();
						
						block1X-=2;
						block2X--;
						block4X++;
						block2Y++;
						block4Y++;
						
						offG.drawImage(lblue,c[block1X],r[block1Y],this);
						offG.drawImage(lblue,c[block2X],r[block2Y],this);
						offG.drawImage(lblue,c[block3X],r[block3Y],this);
						offG.drawImage(lblue,c[block4X],r[block4Y],this);
						
						rotation = 0;
						repaint();
					}
				}
			}
			else if(piece == 4){
				//orange
				if(rotation == 0){
					if(block1Y != 16 && block1Y != 0 && block1Y != 1 ){
						if((block[block2X][block2Y+1] == 0) && (block[block2X][block2Y-1] == 0) && (block[block2X][block2Y-2] == 0)){
							redraw();
							
							block1X++;
							block3X--;
							block4X -= 2;
							block1Y++;
							block3Y--;
							block4Y += 2;
							
							offG.drawImage(orange,c[block1X],r[block1Y],this);
							offG.drawImage(orange,c[block2X],r[block2Y],this);
							offG.drawImage(orange,c[block3X],r[block3Y],this);
							offG.drawImage(orange,c[block4X],r[block4Y],this);
							
							rotation = 1;
							repaint();
						}
					}
				}
				else if(rotation == 1){
					if((block[block2X-1][block2Y] == 0) && (block[block2X+1][block2Y] == 0) && (block[block2X+2][block2Y] == 0)){
						if(block1X != 0 && block1X != 9 && block1X != 8 ){
							redraw();
							
							block1X--;
							block3X++;
							block4X += 2;
							block1Y--;
							block3Y++;
							block4Y -= 2;
							
							offG.drawImage(orange,c[block1X],r[block1Y],this);
							offG.drawImage(orange,c[block2X],r[block2Y],this);
							offG.drawImage(orange,c[block3X],r[block3Y],this);
							offG.drawImage(orange,c[block4X],r[block4Y],this);
							
							rotation = 0;
							repaint();
						}
					}
				}
			}
			else if(piece == 5){
				//purple
				if(rotation == 0){
					if((block[block2X-1][block2Y-1] == 0) && (block[block2X][block2Y+1] == 0) && (block[block2X][block2Y-1] == 0)){
						redraw();
						
						block1X++;
						block3X--;
						block4X -= 2;
						block1Y++;
						block3Y--;
						
						offG.drawImage(purple,c[block1X],r[block1Y],this);
						offG.drawImage(purple,c[block2X],r[block2Y],this);
						offG.drawImage(purple,c[block3X],r[block3Y],this);
						offG.drawImage(purple,c[block4X],r[block4Y],this);
						
						rotation = 1;
						repaint();
					}
				}
				else if(rotation == 1){
					if((block[block2X-1][block2Y] == 0) && (block[block2X-1][block2Y+1] == 0) && (block[block2X+1][block2Y] == 0)){
						redraw();
						
						block1X++;
						block3X--;
						block1Y--;
						block3Y++;
						block4Y += 2;
						
						offG.drawImage(purple,c[block1X],r[block1Y],this);
						offG.drawImage(purple,c[block2X],r[block2Y],this);
						offG.drawImage(purple,c[block3X],r[block3Y],this);
						offG.drawImage(purple,c[block4X],r[block4Y],this);
						
						rotation = 2;
						repaint();
					}
				}
				else if(rotation == 2){
					if((block[block2X][block2Y+1] == 0) && (block[block2X+1][block2Y+1] == 0)&& (block[block2X][block2Y-1] == 0)){
						redraw();
						
						block1X--;
						block3X++;
						block4X += 2;
						block1Y--;
						block3Y++;
						
						offG.drawImage(purple,c[block1X],r[block1Y],this);
						offG.drawImage(purple,c[block2X],r[block2Y],this);
						offG.drawImage(purple,c[block3X],r[block3Y],this);
						offG.drawImage(purple,c[block4X],r[block4Y],this);
						
						rotation = 3;
						repaint();
					}
				}
				else if(rotation == 3){
					if((block[block2X+1][block2Y] == 0) && (block[block2X+1][block2Y-1] == 0)&& (block[block2X-1][block2Y] == 0)){
						redraw();
						
						block1X--;
						block3X++;
						block1Y++;
						block3Y--;
						block4Y -= 2;
						
						offG.drawImage(purple,c[block1X],r[block1Y],this);
						offG.drawImage(purple,c[block2X],r[block2Y],this);
						offG.drawImage(purple,c[block3X],r[block3Y],this);
						offG.drawImage(purple,c[block4X],r[block4Y],this);
						
						rotation = 0;
						repaint();
					}
				}
			}
			else if(piece == 6){
				//red
				//haha red
			}
			else if(piece == 7){
				//yellow
				if(rotation == 0){
					if(block[block2X][block2Y+1] == 0){
						redraw();
						
						block1X++;
						block3X--;
						block4X--;
						block1Y++;
						block3Y--;
						block4Y++;
						
						offG.drawImage(yellow,c[block1X],r[block1Y],this);
						offG.drawImage(yellow,c[block2X],r[block2Y],this);
						offG.drawImage(yellow,c[block3X],r[block3Y],this);
						offG.drawImage(yellow,c[block4X],r[block4Y],this);
						
						rotation = 1;
						repaint();
					}
				}
				else if(rotation == 1){
					if(block[block2X+1][block2Y] == 0){
						redraw();
						
						block1X++;
						block3X--;
						block4X++;
						block1Y--;
						block3Y++;
						block4Y++;
						
						offG.drawImage(yellow,c[block1X],r[block1Y],this);
						offG.drawImage(yellow,c[block2X],r[block2Y],this);
						offG.drawImage(yellow,c[block3X],r[block3Y],this);
						offG.drawImage(yellow,c[block4X],r[block4Y],this);
						
						rotation = 2;
						repaint();
					}
				}
				else if(rotation == 2){
					if(block[block2X][block2Y-1] == 0){
						redraw();
						
						block1X--;
						block3X++;
						block4X++;
						block1Y--;
						block3Y++;
						block4Y--;
						
						offG.drawImage(yellow,c[block1X],r[block1Y],this);
						offG.drawImage(yellow,c[block2X],r[block2Y],this);
						offG.drawImage(yellow,c[block3X],r[block3Y],this);
						offG.drawImage(yellow,c[block4X],r[block4Y],this);
						
						rotation = 3;
						repaint();
					}
				}
				else if(rotation == 3){
					if(block[block2X-1][block2Y] == 0){
						redraw();
						
						block1X--;
						block3X++;
						block4X--;
						block1Y++;
						block3Y--;
						block4Y--;
						
						offG.drawImage(yellow,c[block1X],r[block1Y],this);
						offG.drawImage(yellow,c[block2X],r[block2Y],this);
						offG.drawImage(yellow,c[block3X],r[block3Y],this);
						offG.drawImage(yellow,c[block4X],r[block4Y],this);
						
						rotation = 0;
						repaint();
					}
				}
			}
		}
		return;
	}
	
	public void line(){
		//check if a line is made
		lineamt = 1;
		multiple = 0;
		
		while(lineamt != 0){
			lineamt = 0;
			for(int i = 0; i < 17; i ++){
				for(int d = 0; d < 10; d++){
					if(block[d][i] != 0){
						chkline++;
					}
					if(chkline == 10){
						for(int f = i; f < 16; f++){
							for(int g = 0; g < 10; g++){
								block[g][f] = block[g][f+1];
							}
							if(sound == 1){
								line.play();
							}
							redraw();
							lineamt++;
							
						}
						multiple++;
					}
				}
				chkline = 0;
			}
			
		}
		lines += multiple;
		slines = "" + lines;
		lvlcounter += multiple;
		
		//increases the score
		if(multiple == 1){
			score += level*40 + 40;
			sscore = "" + score;
		}
		else if(multiple == 2){
			score += level*100 + 100;
			sscore = "" + score;
		}
		else if(multiple == 3){
			score += level*300 + 300;
			sscore = "" + score;
		}
		else if(multiple == 4){
			score += level*1200 + 1200;
			sscore = "" + score;
		}
		
		//the level goes up every 10 lines you complete, this resets the "counter"
		if(lvlcounter >= 10){
			level++;
			lvlcounter -= 10;
			slevel = "" + level;
			repaint();
		}
		
		return;
	}
	
	public void blue(){
		//draws the blue piece at the top
		offG.drawImage(blue,c[3],r[16],this);
		offG.drawImage(blue,c[4],r[16],this);
		offG.drawImage(blue,c[5],r[16],this);
		offG.drawImage(blue,c[3],r[15],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 5;
		block4X = 3;
		block1Y = 16;
		block2Y = 16;
		block3Y = 16;
		block4Y = 15;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		
		return;
	}
	
	public void green(){
		//draws the green piece at the top
		offG.drawImage(green,c[3],r[16],this);
		offG.drawImage(green,c[4],r[16],this);
		offG.drawImage(green,c[4],r[15],this);
		offG.drawImage(green,c[5],r[15],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 4;
		block4X = 5;
		block1Y = 16;
		block2Y = 16;
		block3Y = 15;
		block4Y = 15;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void lblue(){
		//draws the light blue piece at the top
		offG.drawImage(lblue,c[3],r[15],this);
		offG.drawImage(lblue,c[4],r[15],this);
		offG.drawImage(lblue,c[4],r[16],this);
		offG.drawImage(lblue,c[5],r[16],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 4;
		block4X = 5;
		block1Y = 15;
		block2Y = 15;
		block3Y = 16;
		block4Y = 16;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void orange(){
		//draws the orange piece at the top
		offG.drawImage(orange,c[3],r[16],this);
		offG.drawImage(orange,c[4],r[16],this);
		offG.drawImage(orange,c[5],r[16],this);
		offG.drawImage(orange,c[6],r[16],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 5;
		block4X = 6;
		block1Y = 16;
		block2Y = 16;
		block3Y = 16;
		block4Y = 16;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void purple(){
		//draws the purple piece at the top
		offG.drawImage(purple,c[3],r[16],this);
		offG.drawImage(purple,c[4],r[16],this);
		offG.drawImage(purple,c[5],r[16],this);
		offG.drawImage(purple,c[5],r[15],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 5;
		block4X = 5;
		block1Y = 16;
		block2Y = 16;
		block3Y = 16;
		block4Y = 15;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void red(){
		//draws the red piece at the top
		offG.drawImage(red,c[4],r[16],this);
		offG.drawImage(red,c[5],r[16],this);
		offG.drawImage(red,c[4],r[15],this);
		offG.drawImage(red,c[5],r[15],this);
		
		block1X = 4;
		block2X = 5;
		block3X = 4;
		block4X = 5;
		block1Y = 16;
		block2Y = 16;
		block3Y = 15;
		block4Y = 15;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void yellow(){
		//draws the yellow piece at the top
		offG.drawImage(yellow,c[3],r[16],this);
		offG.drawImage(yellow,c[4],r[16],this);
		offG.drawImage(yellow,c[5],r[16],this);
		offG.drawImage(yellow,c[4],r[15],this);
		
		block1X = 3;
		block2X = 4;
		block3X = 5;
		block4X = 4;
		block1Y = 16;
		block2Y = 16;
		block3Y = 16;
		block4Y = 15;
		repaint();
		
		if(block[block1X][block1Y] != 0){
			youlose();
		}
		else if(block[block2X][block2Y] != 0){
			youlose();
		}
		else if(block[block3X][block3Y] != 0){
			youlose();
		}
		else if(block[block4X][block4Y] != 0){
			youlose();
		}
		return;
	}
	
	public void redraw(){
		//draws the background, level, score, lines, and blocks at the bottom
		offG.drawImage(background,0,0,this);
		offG.drawImage(ilevel,levelX,levelY,this);
		offG.drawImage(iscore,scoreX,scoreY,this);
		offG.drawImage(ilines,linesX,linesY,this);
		
		offG.setFont(gameFont);
		offG.drawString(slines, 443, 331);
		offG.drawString(slevel, 442, 202);
		offG.drawString(sscore, 461, 266);
		
		if(sound == 1){
			offG.drawImage(soundon,soundX,soundY,this);
		}
		else if(sound ==0){
			offG.drawImage(soundoff,soundX,soundY,this);
		}
		
		if(random == 1){
			//blue
			offG.drawImage(nblue,nextX,nextY,this);
		}
		else if(random == 2){
			//green
			offG.drawImage(ngreen,nextX,nextY,this);
		}
		else if(random == 3){
			//lblue
			offG.drawImage(nlblue,nextX,nextY,this);
		}
		else if(random == 4){
			//orange
			offG.drawImage(norange,nextX,nextY,this);
		}
		else if(random == 5){
			//purple
			offG.drawImage(npurple,nextX,nextY,this);
		}
		else if(random == 6){
			//red
			offG.drawImage(nred,nextX,nextY,this);
		}
		else if(random == 7){
			//yellow
			offG.drawImage(nyellow,nextX,nextY,this);
		}
		
		for(int i = 0; i < 10; i++){
			for(int d = 0; d < 16; d++){
				if(block[i][d] == 1){
					//blue
					offG.drawImage(blue,c[i],r[d],this);
				}
				else if(block[i][d] == 2){
					//green
					offG.drawImage(green,c[i],r[d],this);
				}
				else if(block[i][d] == 3){
					//lblue
					offG.drawImage(lblue,c[i],r[d],this);
				}
				else if(block[i][d] == 4){
					//orange
					offG.drawImage(orange,c[i],r[d],this);
				}
				else if(block[i][d] == 5){
					//purple
					offG.drawImage(purple,c[i],r[d],this);
				}
				else if(block[i][d] == 6){
					//red
					offG.drawImage(red,c[i],r[d],this);
				}
				else if(block[i][d] == 7){
					//yellow
					offG.drawImage(yellow,c[i],r[d],this);
				}
			}
		}
		
		//if pause is in effect, draw the pause overlay
		if(xpause == 1){
			offG.drawImage(ipause,0,0,this);
		}
		repaint();
		return;
	}
	
	public void quit(){
		//quits game
		//fix please =)
		System.exit(0);
	}
	
	public void youlose(){
		//when you lose, the sound plays and you quit
		if(sound == 1){
			lose.play();
		}
		JOptionPane.showMessageDialog(null,"You lose!");
		game = 1;
		quit();
	}
}