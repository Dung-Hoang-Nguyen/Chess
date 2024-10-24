package simpleGames;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import javax.swing.Timer;
public class Chess{
public static void main( String args[] ){
	new Chess(90,90); //button size, time(m)
}//GODDAMN WHY DIDN'T I LABEL ANYTHING
//aha if there are bugs i blame past me FOR NOT LABELING
//ok after some reading I have determined: TO-DO: Checks, mates, king restrictions, time running out, prolly redo the pieces into an interface, if ambitious make a shitty engine
private Piece[][] pieces;
JFrame window;
JLabel label,timew,timeb;
JButton restart, undo;
JButton[][] buttons;
boolean[][] boo;
int[] current;
Font mono,medium,big;
JLabel endScreen;
int p=1;
boolean choose;
int butsize;
double timinute;
Timer whitetime,blacktime;
ArrayList<int[]> possible;
Scanner scan;
boolean ended;
//Icon icon = new ImageIcon("E:\editicon.PNG");
public Chess(int but,double time){
	ended=false;
	butsize=but;timinute=time;
	possible=new ArrayList<int[]>();
	scan=new Scanner(System.in);
	pieces=new Piece[8][8];
	buttons=new JButton[8][8];
	boo =new boolean[8][8];
	mono = new Font(Font.MONOSPACED,Font.PLAIN,butsize/4);
	big = new Font(Font.MONOSPACED,Font.PLAIN,5+8*3*(butsize/30));
	medium=new Font(Font.MONOSPACED,Font.PLAIN,(int)((double)(5+8*3*(butsize/30)+butsize/3)/2));
	window = new JFrame("Chess");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setBounds(965,250,(int)((10+1*(double)2/(butsize/15))*butsize-14),(int)((10+1*(double)2/(butsize/15))*butsize+9));//-14,+9
	window.setLayout(null);
	label=new JLabel("a  b  c  d  e  f  g  h");
	label.setFont(medium);
	window.add(label);
	label.setBounds(butsize,0,butsize*8,butsize);
	label=new JLabel("a  b  c  d  e  f  g  h");
	label.setBounds(butsize,butsize*9,butsize*8,butsize);
	label.setFont(medium);
	window.add(label);
	for(int i=1;i<=8;i++) {
		label=new JLabel(i+"");
		label.setFont(medium);
		label.setBounds(butsize/2,butsize*(9-i),butsize,butsize);
		window.add(label);
		label=new JLabel(i+"");
		label.setFont(medium);
		label.setBounds(butsize*9,butsize*(9-i),butsize,butsize);
		window.add(label);
	}
	timew=new JLabel();
	window.add(timew);
	timew.setBounds(butsize/10,butsize*9,butsize,butsize);
	timew.setFont(mono);
	Instant start;
	start = Instant.now();
	whitetime = new Timer(1, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int theTime = (int) (timinute*60-(int)Duration.between(start, Instant.now()).getSeconds());
			timew.setText(String.format("%02d",theTime/60)+":"+String.format("%02d",theTime%60));
		}
	});
	timeb=new JLabel();
	window.add(timeb);
	timeb.setBounds(butsize/10,0,butsize,butsize);
	timeb.setFont(mono);
	Instant start1;
	start1 = Instant.now();
	timeb.setText(String.format("%02d",90)+":"+String.format("%02d",0));
	blacktime = new Timer(1, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int theTime = (int) (timinute*60-(int)Duration.between(start1, Instant.now()).getSeconds());
			timeb.setText(String.format("%02d",theTime/60)+":"+String.format("%02d",theTime%60));
		}
	});
	whitetime.start();

	current=new int[2];
	undo=new JButton("Undo");
	undo.setBounds(butsize*9,0,butsize,butsize);
	undo.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
		}
	});
	for (int i=0;i<pieces.length;i++){
		for(int j=0;j<pieces.length;j++){
			initiatePlay(i,j);
			buttons[i][j]=initiateButton(i,j);
		}
	}
	//buttons[0][0]=
	window.setVisible(true);
}
public JButton initiateButton(int x,int y) {
	JButton n = new JButton(pieces[x][y].type.equals("none")? "":pieces[x][y].toString());
	n.setBackground(Color.lightGray);
	switch(pieces[x][y].color){
	case "White":n.setBackground(Color.white);n.setForeground(Color.black);break;
	case "Black":n.setBackground(Color.black);n.setForeground(Color.white);break;
	default:n.setBackground(Color.lightGray);
	}
	n.setBounds(butsize+x*butsize,butsize+y*butsize, butsize, butsize);
	window.add(n);
	n.addActionListener( new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			if(((pieces[x][y].color.equals("White")&&p%2==1)||(pieces[x][y].color.equals("Black")&&p%2==0)||choose)&&!ended) {
			if((x==current[0]&&y==current[1])) {
				for(int i=0;i<possible.size();i++) {
					buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);
					switch(pieces[possible.get(i)[0]][possible.get(i)[1]].color){
					case "White":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.white);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);break;
					case "Black":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.black);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.white);break;
					default:buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);
					}
					boo[possible.get(i)[0]][possible.get(i)[1]]=false;
				}
				current=new int[] {8,8};choose=false;
				if(p%2==0) {whitetime.stop();blacktime.start();}else {whitetime.start();blacktime.stop();}
			}
			else if(boo[x][y]) {
				if(pieces[x][y].type.equals("King")) {end();}
				if(pieces[current[0]][current[1]].firstMove&&pieces[current[0]][current[1]].type.equals("King")) {
					if(x==1) {
						pieces[2][y]=pieces[0][y];
						buttons[2][y].setText(pieces[2][y].toString());
						pieces[0][y]=new Piece();
						buttons[0][y].setBackground(Color.lightGray);buttons[0][y].setForeground(Color.black);
						pieces[2][y].firstMove=false;
						buttons[0][y].setText("");
					}
					if(x==5) {
						pieces[4][y]=pieces[7][y];
						buttons[4][y].setText(pieces[4][y].toString());
						pieces[7][y]=new Piece();
						buttons[7][y].setBackground(Color.lightGray);buttons[7][y].setForeground(Color.black);
						pieces[4][y].firstMove=false;
						buttons[7][y].setText("");
					}
				}
				pieces[x][y]=pieces[current[0]][current[1]];
				buttons[x][y].setText(pieces[x][y].toString());
				//System.out.print(pieces[x][y].type+x+y);
				pieces[current[0]][current[1]]=new Piece();
				pieces[x][y].firstMove=false;
				buttons[current[0]][current[1]].setText("");
				for(int i=0;i<possible.size();i++) {
					buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);
					switch(pieces[possible.get(i)[0]][possible.get(i)[1]].color){
					case "White":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.white);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);break;
					case "Black":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.black);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.white);break;
					default:buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);
					}
					boo[possible.get(i)[0]][possible.get(i)[1]]=false;
				}
				current=new int[] {8,8};choose=false;
				p++; if(p%2==0) {whitetime.stop();blacktime.start();}else {whitetime.start();blacktime.stop();}
			}
			else if (pieces[x][y].type.equals("none")){}
			else if (!boo[x][y]&&((pieces[x][y].color.equals("White")&&p%2==1)||(pieces[x][y].color.equals("Black")&&p%2==0))) {
				for(int i=0;i<possible.size();i++) {
					buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);
					switch(pieces[possible.get(i)[0]][possible.get(i)[1]].color){
					case "White":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.white);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);break;
					case "Black":buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.black);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.white);break;
					default:buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.lightGray);buttons[possible.get(i)[0]][possible.get(i)[1]].setForeground(Color.black);
					}
					boo[possible.get(i)[0]][possible.get(i)[1]]=false;
				}
				possible=pieces[x][y].possibleMoves(pieces,x,y);
				current=new int[] {x,y};choose=true;
				for(int i =0;i<possible.size();i++) {
					buttons[possible.get(i)[0]][possible.get(i)[1]].setBackground(Color.darkGray);
					boo[possible.get(i)[0]][possible.get(i)[1]]=true;
				}
			}
			if(pieces[x][y].type.equals("Pawn")&&(y==0||y==7)){System.out.println("Pawn succession detected! Type what it should be!");
			while(pieces[x][y].type.equals("Pawn")) {
				String choice=scan.nextLine();
				switch (choice.toLowerCase()) {
				case "rook":
					pieces[x][y]=new Piece("Rook");
					pieces[x][y].setSide(y==0? "White":"Black");
					buttons[x][y].setText(pieces[x][y].toString());
					break;
				case "bishop":
					pieces[x][y]=new Piece("Bishop");
					pieces[x][y].setSide(y==0? "White":"Black");
					buttons[x][y].setText(pieces[x][y].toString());
					break;
				case "knight":
					pieces[x][y]=new Piece("Knight");
					pieces[x][y].setSide(y==0? "White":"Black");
					buttons[x][y].setText(pieces[x][y].toString());
					break;
				case "queen":
					pieces[x][y]=new Piece("Queen");
					pieces[x][y].setSide(y==0? "White":"Black");
					buttons[x][y].setText(pieces[x][y].toString());
					break;
				}
			}
			}
			//System.out.print(current[0]+""+current[1]);
		}}
	});
	return n;
}
private void initiatePlay(int x,int y){
	pieces[x][y]=new Piece();
	if (y==1||y==6){pieces[x][y]=new Piece("Pawn");}
	else if((x==0||x==7)&&(y==0||y==7)){pieces[x][y]=new Piece("Rook");}
	else if((x==1||x==6)&&(y==0||y==7)){pieces[x][y]=new Piece("Knight");}
	else if((x==2||x==5)&&(y==0||y==7)){pieces[x][y]=new Piece("Bishop");}
	else if(x==4&&(y==0||y==7)){pieces[x][y]=new Piece("Queen");}
	else if(x==3&&(y==0||y==7)){pieces[x][y]=new Piece("King");}
	if(y<2){pieces[x][y].setSide("Black");}
	else if(y>5){pieces[x][y].setSide("White");}
	
}
public boolean exists(int x,int y){
	if(x>=0&&x<8&&y>=0&&y<8){
		return true;
	}
	return false;
}
public void end(){
	ended=true; //System.out.print("a");
	window.getContentPane().removeAll();
	window.repaint();
	for(int i=0;i<8;i++) {
		for(int j=0;j<8;j++) {
			//JTextField a=initiateText(i,j);
		}
	}
	restart=new JButton("Again?");
	endScreen=new JLabel();
	endScreen.setBounds(0,0,(11)*butsize-14,(int)(11.0*butsize+9)/5*3);
	endScreen.setFont(big);
	endScreen.setText("     "+(pieces[current[0]][current[1]].color.equals("Black")?"Black":"White")+" wins!");
	restart.setBounds((int)(((double)11/3)*butsize-14),(int)(((11.0*butsize+9)/5*3)+((double)11/5*2/3)*butsize+9),(int)(((double)11/3)*butsize-14),(int)(((double)11/5*2/3)*butsize+9));
	restart.setFont(medium);
	window.add(endScreen);
	window.add(restart);
	restart.addActionListener( new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e) {
			window.dispose();
			new Chess(butsize,timinute);
		}
	});
}
}
class Piece{
String type,color;
boolean firstMove;
public Piece(){
	type="none";
	color="none";
	firstMove=false;
}
public Piece(String t){
	type=t;
	firstMove=true;
}
public void setSide(String s){
	color=s;
}
public String toString(){
	return type+color.substring(0,1);
}
public boolean exists(int x,int y){
	if(x>=0&&x<8&&y>=0&&y<8){
		return true;
	}
	return false;
}
public ArrayList<int[]> possibleMoves(Piece[][] pieces,int x,int y) {
	ArrayList<int[]> pos=new ArrayList<int[]>();
	if(!exists(x,y)) {return pos;}
	boolean boobreak=false;
	int i=0;
	switch(type){
	case "Pawn":
		switch(color) {
		case "White":
			if(exists(x,y-1)&&pieces[x][y-1].type.equals("none")){pos.add(new int[]{x,y-1});
			if(exists(x,y-2)&&firstMove&&pieces[x][y-2].type.equals("none")){pos.add(new int[]{x,y-2});}}
			if(exists(x+1,y-1)&&!pieces[x+1][y-1].type.equals("none")&&pieces[x+1][y-1].color.equals("Black")){pos.add(new int[]{x+1,y-1});}
			if(exists(x-1,y-1)&&!pieces[x-1][y-1].type.equals("none")&&pieces[x-1][y-1].color.equals("Black")){pos.add(new int[]{x-1,y-1});}
			break;
		case "Black":
			if(exists(x,y+1)&&pieces[x][y+1].type.equals("none")){pos.add(new int[]{x,y+1});
			if(exists(x,y+2)&&firstMove&&pieces[x][y+2].type.equals("none")){pos.add(new int[]{x,y+2});}}
			if(exists(x+1,y+1)&&!pieces[x+1][y+1].type.equals("none")&&pieces[x+1][y+1].color.equals("White")){pos.add(new int[]{x+1,y+1});}
			if(exists(x-1,y+1)&&!pieces[x-1][y+1].type.equals("none")&&pieces[x-1][y+1].color.equals("White")){pos.add(new int[]{x-1,y+1});}
			break;
		}
		break;
	case "Rook":
		boobreak=false;
		i=0;
		while(true){
		if(exists(x+i,y)&&pieces[x+i][y].type.equals("none")){pos.add(new int[]{x+i,y});}
		else if(boobreak){
			if(exists(x+i,y)&&!pieces[x+i][y].color.equals(color)){pos.add(new int[]{x+i,y});}
			boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y)&&pieces[x-i][y].type.equals("none")){pos.add(new int[]{x-i,y});}
		else if(boobreak){if(exists(x-i,y)&&!pieces[x-i][y].color.equals(color)){pos.add(new int[]{x-i,y});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x,y+i)&&pieces[x][y+i].type.equals("none")){pos.add(new int[]{x,y+i});}
		else if(boobreak){if(exists(x,y+i)&&!pieces[x][y+i].color.equals(color)){pos.add(new int[]{x,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x,y-i)&&pieces[x][y-i].type.equals("none")){pos.add(new int[]{x,y-i});}
		else if(boobreak){if(exists(x,y-i)&&!pieces[x][y-i].color.equals(color)){pos.add(new int[]{x,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		break;
	case "Bishop":
		boobreak=false;
		i=0;
		while(true){
		if(exists(x+i,y+i)&&pieces[x+i][y+i].type.equals("none")){pos.add(new int[]{x+i,y+i});}
		else if(boobreak){if(exists(x+i,y+i)&&!pieces[x+i][y+i].color.equals(color)){pos.add(new int[]{x+i,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y-i)&&pieces[x-i][y-i].type.equals("none")){pos.add(new int[]{x-i,y-i});}
		else if(boobreak){if(exists(x-i,y-i)&&!pieces[x-i][y-i].color.equals(color)){pos.add(new int[]{x-i,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y+i)&&pieces[x-i][y+i].type.equals("none")){pos.add(new int[]{x-i,y+i});}
		else if(boobreak){if(exists(x-i,y+i)&&!pieces[x-i][y+i].color.equals(color)){pos.add(new int[]{x-i,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x+i,y-i)&&pieces[x+i][y-i].type.equals("none")){pos.add(new int[]{x+i,y-i});}
		else if(boobreak){if(exists(x+i,y-i)&&!pieces[x+i][y-i].color.equals(color)){pos.add(new int[]{x+i,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		break;
	case "Knight":
		if(exists(x+1,y-2)&&!pieces[x+1][y-2].color.equals(color)){pos.add(new int[]{x+1,y-2});}
		if(exists(x+2,y-1)&&!pieces[x+2][y-1].color.equals(color)){pos.add(new int[]{x+2,y-1});}
		if(exists(x-1,y-2)&&!pieces[x-1][y-2].color.equals(color)){pos.add(new int[]{x-1,y-2});}
		if(exists(x-2,y-1)&&!pieces[x-2][y-1].color.equals(color)){pos.add(new int[]{x-2,y-1});}
		if(exists(x+1,y+2)&&!pieces[x+1][y+2].color.equals(color)){pos.add(new int[]{x+1,y+2});}
		if(exists(x+2,y+1)&&!pieces[x+2][y+1].color.equals(color)){pos.add(new int[]{x+2,y+1});}
		if(exists(x-1,y+2)&&!pieces[x-1][y+2].color.equals(color)){pos.add(new int[]{x-1,y+2});}
		if(exists(x-2,y+1)&&!pieces[x-2][y+1].color.equals(color)){pos.add(new int[]{x-2,y+1});}
		break;
	case "Queen":
		boobreak=false;
		i=0;
		while(true){
		if(exists(x+i,y)&&pieces[x+i][y].type.equals("none")){pos.add(new int[]{x+i,y});}
		else if(boobreak){if(exists(x+i,y)&&!pieces[x+i][y].color.equals(color)){pos.add(new int[]{x+i,y});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y)&&pieces[x-i][y].type.equals("none")){pos.add(new int[]{x-i,y});}
		else if(boobreak){if(exists(x-i,y)&&!pieces[x-i][y].color.equals(color)){pos.add(new int[]{x-i,y});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x,y+i)&&pieces[x][y+i].type.equals("none")){pos.add(new int[]{x,y+i});}
		else if(boobreak){if(exists(x,y+i)&&!pieces[x][y+i].color.equals(color)){pos.add(new int[]{x,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x,y-i)&&pieces[x][y-i].type.equals("none")){pos.add(new int[]{x,y-i});}
		else if(boobreak){if(exists(x,y-i)&&!pieces[x][y-i].color.equals(color)){pos.add(new int[]{x,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x+i,y+i)&&pieces[x+i][y+i].type.equals("none")){pos.add(new int[]{x+i,y+i});}
		else if(boobreak){if(exists(x+i,y+i)&&!pieces[x+i][y+i].color.equals(color)){pos.add(new int[]{x+i,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y-i)&&pieces[x-i][y-i].type.equals("none")){pos.add(new int[]{x-i,y-i});}
		else if(boobreak){if(exists(x-i,y-i)&&!pieces[x-i][y-i].color.equals(color)){pos.add(new int[]{x-i,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x-i,y+i)&&pieces[x-i][y+i].type.equals("none")){pos.add(new int[]{x-i,y+i});}
		else if(boobreak){if(exists(x-i,y+i)&&!pieces[x-i][y+i].color.equals(color)){pos.add(new int[]{x-i,y+i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		i=0;
		while(true){
		if(exists(x+i,y-i)&&pieces[x+i][y-i].type.equals("none")){pos.add(new int[]{x+i,y-i});}
		else if(boobreak){if(exists(x+i,y-i)&&!pieces[x+i][y-i].color.equals(color)){pos.add(new int[]{x+i,y-i});}boobreak=false;break;}
		else{boobreak=true;}
		i++;
		}
		break;
	case "King":
		if(firstMove) {
			//System.out.print(pieces[x-3][y].firstMove+""+pieces[x-1][y].type.equals("none")+""+pieces[x-2][y].type.equals("none"));
			if(pieces[x-3][y].firstMove&&pieces[x-1][y].type.equals("none")&&pieces[x-2][y].type.equals("none")) {
				pos.add(new int[] {x-2,y});
			}
			if(pieces[x+4][y].firstMove&&pieces[x+1][y].type.equals("none")&&pieces[x+2][y].type.equals("none")&&pieces[x+2][y].type.equals("none")) {
				pos.add(new int[] {x+2,y});
			}
		}
		for(int j=-1;j<2;j++){
			if(exists(x+j,y-1)&&!pieces[x+j][y-1].color.equals(color)){pos.add(new int[]{x+j,y-1});}
			if(exists(x+j,y)&&!pieces[x+j][y].color.equals(color)){pos.add(new int[]{x+j,y});}
			if(exists(x+j,y+1)&&!pieces[x+j][y+1].color.equals(color)){pos.add(new int[]{x+j,y+1});}
		}
		break;
	}
	pos.add(new int[] {x,y});
	return pos;
	}
}


