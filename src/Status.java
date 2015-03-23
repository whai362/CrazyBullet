import java.awt.*;

import javax.swing.JPanel;
public class Status extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int  STATUS_X=0,STATUS_Y=363,HP_X=10,HP_Y=10;
	private int init=0;
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image status=kit.getImage("img\\status.png"),
				  hp=kit.getImage("img\\hp.png"),
				  inf=kit.getImage("img\\inf.png");
	private Image[] num=new Image[10];
	{
		for(int i=0;i<num.length;++i){
			num[i]=kit.getImage("img\\"+i+".png");
		}
	}
	public Status(){
		init=0;
	}
	//把图片事先加载一遍，防止闪烁
	private void load_image(Graphics g){
		g.drawImage(status,-100,-100,this);
		g.drawImage(hp,-100,-100,this);
		g.drawImage(inf,-100,-100,this);
		for(int i=0;i<num.length;++i){
			g.drawImage(num[i],-100,-100,this);
		}
	}
	//画状态栏的函数
	private void drawStatus(Hero hero,Graphics g){
		Integer life=hero.getLife(),shell_num=hero.getShell_num();
		char[] number=new char[5];
		g.drawImage(status,STATUS_X,STATUS_Y,this);
		number=life.toString().toCharArray();
		for(int i=0;i<number.length;++i){
			g.drawImage(num[number[i]-'0'],STATUS_X+60+10*i,STATUS_Y+20,this);
		}
		g.drawImage(inf,STATUS_X+158,STATUS_Y+20,this);
		number=shell_num.toString().toCharArray();
		for(int i=0;i<number.length;++i){
			g.drawImage(num[number[i]-'0'],STATUS_X+215+10*i,STATUS_Y+20,this);
		}
	}
	//画血条的函数
	private void drawHp(Hero hero,Graphics g){
		for(int i=0;i<hero.getHp();++i){
			g.drawImage(hp,HP_X+15*i,HP_Y,this);
		}
	}
	//画分数的函数
	private void drawScore(Hero hero,Graphics g){
		Integer score=hero.getScore();
		char[] number=new char[6];
		number=score.toString().toCharArray();
		int i;
		for(i=0;i<6-number.length;++i){
			g.drawImage(num[0],150+12*i,13,this);
		}
		for(;i<6;++i){
			g.drawImage(num[number[i-6+number.length]-'0'],150+12*i,13,this);
		}
	}
	//画状态的函数
	public void draw(Hero hero,Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		drawStatus(hero,g);
		drawHp(hero,g);
		drawScore(hero,g);
		drawScore(hero,g);
	}
}
