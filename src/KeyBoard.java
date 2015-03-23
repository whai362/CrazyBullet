import java.awt.event.*;
import javax.swing.*;
class Keyboard implements KeyListener{
	private static final long serialVersionUID = 1L;
	public static final int MAX_NUM=5;
	private static String[] key_text=new String[MAX_NUM];
	{
		for(int i=0;i<MAX_NUM;++i){
			key_text[i]="null";
		}
	}
	public Keyboard(JFrame frame){
		frame.addKeyListener(this);
	}
	public String[] get_keyboard(){
		return key_text;
	}
	public void keyPressed(KeyEvent e){
		int j=MAX_NUM;
		String press=KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
		for(int i=0;i<MAX_NUM;++i){
			if(j==MAX_NUM && key_text[i].equals("null")){
				j=i;
			}
			if(press.equals(key_text[i])){
				j=-1;
			}
		}
		if(j!=-1 && j<MAX_NUM){
			key_text[j]=press;
		}
	}
	public static void clearKeyboard(){
		for(int i=0;i<MAX_NUM;++i){
			key_text[i]="null";
		}
	}
	public void keyReleased(KeyEvent e){
		int i;
		String release=KeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
		for(i=0;i<MAX_NUM;++i){
			if(release.equals(key_text[i]))
				break;
		}
		if(i<MAX_NUM)
			key_text[i]="null";
	}
	public void keyTyped(KeyEvent e){}
}