import java.awt.*;
import javax.swing.JPanel;
public class Barrier extends JPanel{
	private static final long serialVersionUID = 1L;
	public static final int MAX_NUM=24;
	private static final int N=4,TOUGH=5,Y0=252,BANG_TIME=4*N;
	private static int num=0;
	private int id,init,x,y,type,gift,tough,bang_time;
	private int[] t={0,0,0,1,0,2,0,1,1,2,0,0,3,2,2,1,2,3,1,3,1,2,0,0},
				  g={0,0,0,0,0,0,0,0,0,1,0,0,1,4,0,3,0,1,0,2,0,2,0,0};
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] barrier=new Image[3],
					_gift=new Image[4];
	private Image[][] bang=new Image[3][5];
	{
		for(int i=0;i<3;++i){
			barrier[i]=kit.getImage("img\\barrier"+i+".png");
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				bang[i][j]=kit.getImage("img\\barrier_bang"+i+j+".png");
			}
		}
		for(int i=0;i<4;++i){
			_gift[i]=kit.getImage("img\\gift"+i+".png");
		}
	}
	public Barrier(){
		if(num<MAX_NUM){
			init=0;
			id=num;
			x=30+num*300;
			y=Y0;
			type=t[num];
			gift=g[num];
			tough=TOUGH;
			bang_time=0;
			++num;
		}
	}
	private void load_image(Graphics g){
		for(int i=0;i<3;++i){
			g.drawImage(barrier[i],-100,-100,this);
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				g.drawImage(bang[i][j],-100,-100,this);
			}
		}
		for(int i=0;i<4;++i){
			g.drawImage(_gift[i],-100,-100,this);
		}
	}
	//获取障碍物位置
	public Point get_pos(){
		Point p=new Point(x,y);	
		if(type==1) p.y+=5;
		return p;
	}
	//判断障碍物是否存在
	public boolean is_exist(){
		if(x<-120 || x>600 || type==0 || bang_time>0) return false;
		else return true;
	}
	//把障碍物加载入碰撞检测数组的函数
	public void draw_in_map(){
		if(is_exist()){
			int width=barrier[type-1].getWidth(this),hight=barrier[type-1].getHeight(this);
			if(type!=1){
				for(int i=(y+6)/3;i<=(y+hight+100)/3;++i){
					if(i>=0 && i<134){
						for(int j=x/3;j<(x+width)/3;++j){
							if(j>=0 && j<251)
								Game._map[i][j]=1*100+id;
						}
					}
				}
			}
			else{
				for(int i=(y+9)/3;i<=(y+hight/2)/3;++i){
					if(i>=0 && i<134){
						for(int j=(x+width/4)/3;j<(x+width*3/4)/3;++j){
							if(j>=0 && j<251)
								Game._map[i][j]=1*100+id;
						}
					}
				}
				for(int i=(y+hight/2)/3;i<=(y+hight+100)/3;++i){
					if(i>=0 && i<134){
						for(int j=x/3;j<(x+width)/3;++j){
							if(j>=0 && j<251)
								Game._map[i][j]=1*100+id;
						}
					}
				}
			}
		}
		if(gift>0){
			int gift_width=_gift[gift-1].getWidth(this),
				gift_height=_gift[gift-1].getHeight(this),
				barrier_height=barrier[1].getHeight(this);
			for(int i=(y+barrier_height-gift_height)/3;i<=(y+barrier_height)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+gift_width)/3;++j){
						if(j>=0 && j<201)
							Game._map[i][j]=5*100+id;
					}
				}
			}
		}
	}
	//画障碍物的函数
	public void draw(Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		if(is_exist()){
			if(tough>0){
				if(type==1) g.drawImage(barrier[type-1],x,y+5,this);
				else g.drawImage(barrier[type-1],x,y,this);
			}
			else{
				tough=TOUGH;
				bang_time=BANG_TIME;
			}
		}
		else if(bang_time>0){
			draw_bang(g);
		}
		else if(type==0 && gift>0){
			int barrier_hight=barrier[1].getHeight(this),
				gift_hight=_gift[gift-1].getHeight(this);
			g.drawImage(_gift[gift-1],x+20,y-6+barrier_hight-gift_hight,this);
		}
	}
	//画爆炸的函数
	public void draw_bang(Graphics g){
		int bang_x=x,bang_y=y;
		if(type==1) bang_x+=18;
		g.drawImage(bang[type-1][(BANG_TIME-bang_time)/N],bang_x,bang_y,this);
		if(Execute.getEsc_flag()==0)
			--bang_time;
		if(bang_time==0)
			type=0;
	}
	public int getGift() {
		return gift;
	}

	public void setGift(int gift) {
		this.gift = gift;
	}
	public static int getNum() {
		return num;
	}
	public static void setNum(int num) {
		Barrier.num = num;
	}
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x=x;
	}
	public int getTough(){
		return tough;
	}
	public void setTough(int tough){
		if(tough>=0)
			this.tough=tough;
		else
			this.tough=0;
	}
}

//障碍物在车行驶时有bug ok
//还没写掉宝
//下降的姿势不太对 ok
//第一个障碍物低一阶可以走 碰撞检测应该设为脚部 ok 向左走还有问题
//敌人死掉后子弹就没了