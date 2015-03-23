import java.awt.*;

import javax.swing.JPanel;
public class Mine extends JPanel{
	private static final long serialVersionUID = 1L;
	public static final int MAX_NUM=5;
	private static final int N=5,BANG_TIME=5*N,MOD=4*N,X0=3000,Y0=305;
	private static int num=0;
	private int init,id,x,y,exist,bang_time,point;
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] mine=new Image[2],
					bang=new Image[5];
	{
		for(int i=0;i<2;++i){
			mine[i]=kit.getImage("img\\mine"+i+".png");
		}
		for(int i=0;i<5;++i){
			bang[i]=kit.getImage("img\\mine_bang"+i+".png");
		}
	}
	Mine(){
		init=1;
		x=X0+120*num;
		y=Y0;
		exist=1;
		bang_time=0;
		point=0;
		id=num;
		++num;	
	}
	//把图片事先加载一遍，防止闪烁
	public void load_image(Graphics g){
		for(int i=0;i<2;++i){
			g.drawImage(mine[i],-100,-100,this);
		}
		for(int i=0;i<5;++i){
			g.drawImage(bang[i],-100,-100,this);
		}
	}
	//检测子弹是否存在的函数
	public boolean is_exist(){
		int width=mine[0].getWidth(this);
		if(x<-width || x>GameFrame.DEFAULT_WIDTH || exist==0)
			return false;
		return true;
	}
	//初始化爆炸参数的函数
	public void init_bang(){
		exist=0;
		bang_time=BANG_TIME;
	}
	//把地雷加载入碰撞检测数组的函数
	public void draw_in_map(){
		if(is_exist()){
			int width=mine[0].getWidth(this),
				height=mine[0].getHeight(this);
			for(int i=y/3;i<=(y+height)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+width)/3;++j){
						if(j>=0 && j<201){
							Game._map[i][j]=400+id;
						}
					}
				}
			}
		}
	}
	//画爆炸的函数
	public void draw_bang(Graphics g){
		int mine_width=mine[0].getWidth(this),
			mine_height=mine[0].getHeight(this),
			bang_width=bang[(BANG_TIME-bang_time)/N].getWidth(this),
			bang_height=bang[(BANG_TIME-bang_time)/N].getHeight(this),
			bang_x=x+(mine_width-bang_width)/2,
			bang_y=y+mine_height-bang_height;
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x,bang_y,this);
		if(bang_time>0 && Execute.getEsc_flag()==0)
			--bang_time;
	}
	//画地雷的函数
	public void draw(Graphics g){
		if(init==1){
			load_image(g);
			init=0;
		}
		if(is_exist()){
			g.drawImage(mine[point/2/N],x,y,this);
			if(Execute.getEsc_flag()==0)
				point=(point+1)%MOD;
		}
		else if(bang_time>0){
			draw_bang(g);
		}
	}
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x = x;
	}
	public static int getNum() {
		return num;
	}
	public static void setNum(int num) {
		Mine.num = num;
	}
	
}
