import java.awt.*;

import javax.swing.JPanel;
public class Win extends JPanel{
	private int p=0;
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] win=new Image[2],
					num=new Image[10];
	{
		for(int i=0;i<2;++i){
			win[i]=kit.getImage("img\\win"+i+".jpg");
		}
		for(int i=0;i<10;++i){
			num[i]=kit.getImage("img\\l"+i+".png");
		}
	}
	public void draw(Hero hero,Graphics g){
		Integer score=hero.getScore();
		char[] number=new char[6];
		g.drawImage(win[p/8],0,0,this);
		number=score.toString().toCharArray();
		for(int i=0;i<number.length;++i){
			g.drawImage(num[number[i]-'0'],60+20*i,280,this);
		}
		p=(p+1)%16;
	}
}
