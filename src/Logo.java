import java.awt.*;

import javax.swing.JPanel;
public class Logo extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int N=4;
	private static int p=0;
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] logo=new Image[7];
	{
		for(int i=0;i<7;++i){
			logo[i]=kit.getImage("img\\logo"+i+".jpg");
		}
	}
	public void draw(Graphics g){
		g.drawImage(logo[p/N],0,0,this);
		if(p<6*N)
			p=p+1;
	}
}
