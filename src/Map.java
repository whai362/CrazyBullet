import java.awt.*;
import javax.swing.JPanel;
public class Map extends JPanel{
	private static final long serialVersionUID = 1L;
	private int x,init;
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image map=kit.getImage("img\\map.jpg");
	private void load_image(Graphics g){
		g.drawImage(map,-100,-100,this);
	}
	public Map(){
		x=0;
		init=0;
	}
	public void draw_map(Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		g.drawImage(map,x,0,this);
	}
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x=x;
	}
}