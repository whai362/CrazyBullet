import java.awt.*;

import javax.swing.JPanel;
public class Gameover extends JPanel{
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image gameover=kit.getImage("img\\gameover.jpg");
	public void draw(Graphics g){
		g.drawImage(gameover,0,0,this);
	}
}
