import java.awt.*;
import javax.swing.*;
public class Bullet extends JPanel{
	private static final long serialVersionUID = 1L;
	private int x,y,flag,type,direction;
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[][] b=new Image[6][2];
	{
		for(int i=0;i<6;++i){
			for(int j=0;j<2;++j){
				b[i][j]=kit.getImage("img\\bullet"+i+j+".png");
			}
		}
	}
	public Bullet(){
		flag=0;
	}
	//子弹飞函数
	public void fly(Graphics g){
		if(flag==1){
			int width=b[type][0].getWidth(this);
			if(direction==1){
				g.drawImage(b[type][direction],x,y,this);
				if(x<GameFrame.DEFAULT_WIDTH && Execute.getEsc_flag()==0)
					x+=9;
				else if(x>=GameFrame.DEFAULT_WIDTH)
					flag=0;
			}
			else{
				g.drawImage(b[type][direction],x-width,y,this);
				if(x>-width && Execute.getEsc_flag()==0)
					x-=9;
				else if(x<=-width)
					flag=0;
			}
		}
	}
	//检测子弹是否存在函数
	public boolean is_exist(){
		int width=b[type][0].getWidth(this);
		if(x<-width || x>600 || flag==0) return false;
		return true;
	}
	//设置子弹参数函数
	public void set_bullet(int x,int y,int flag,int type,int dir){
		this.x=x;
		this.y=y;
		this.flag=flag;
		this.type=type;
		direction=dir;
	}
	//画子弹的函数
	public void draw(Graphics g){
		if(is_exist())
			fly(g);
	}
	//获取子弹位置的函数
	public Point get_pos(){
		int width=b[type][0].getWidth(this),
			height=b[type][0].getHeight(this),
			bullet_x=x,bullet_y=y+height;
		if(direction==0)
			bullet_x-=width;
		switch(type){
			case 0:
				if(direction==1)
					bullet_x+=width;
				break;
			case 1:
				if(direction==1)
					bullet_x+=width/3;
				else
					bullet_x+=width*2/3;
				break;
			case 2:
			case 3:
			case 4:
				if(direction==1)
					bullet_x+=width*3/4;
				else
					bullet_x+=width/4;
				break;
			case 5:
				bullet_y-=height/2;
				break;
		}
		Point p=new Point(bullet_x,bullet_y);
		return p;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getFlag(){
		return flag;
	}
	public void setFlag(int flag){
		this.flag=flag;
	}
}
//type可以统一一下
//不同子弹打在物体上的位置不同，于1处改进 ok