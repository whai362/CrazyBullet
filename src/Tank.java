import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
public class Tank extends JPanel{
	private static final long serialVersionUID = 1L;
	public static final int BULLET_MAX_NUM=5,MOTO_MAX_NUM=3;
	public Bullet[] bullet=new Bullet[BULLET_MAX_NUM];
	public Moto[] moto=new Moto[MOTO_MAX_NUM];
	private static final int N=4,X0=6630,Y0=127,TOUGH=50,BOSS_ATTACK_TIME=5*N,ATTACK_TIME=2*N,STAND_TIME=8*N,BANG_TIME=5*N;
	private int x,y,tough,action,action_time,bang_time;
	private Random random=new Random();
	Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] stand=new Image[2],
					attack=new Image[2],
					boss_attack=new Image[5],
					bang=new Image[5];
	{
		for(int i=0;i<BULLET_MAX_NUM;++i){
			bullet[i]=new Bullet();
		}
		for(int i=0;i<MOTO_MAX_NUM;++i){
			moto[i]=new Moto();
			moto[i].setId(i);
		}
		for(int i=0;i<2;++i){
			stand[i]=kit.getImage("img\\tank"+i+".png");
		}
		for(int i=0;i<2;++i){
			attack[i]=kit.getImage("img\\tank_attack"+i+".png");
		}
		for(int i=0;i<5;++i){
			boss_attack[i]=kit.getImage("img\\boss_attack"+i+".png");
		}
		for(int i=0;i<5;++i){
			bang[i]=kit.getImage("img\\vehicle_bang"+i+".png");
		}
	}
	public Tank(){
		x=X0;
		y=Y0;
		tough=TOUGH;
		action_time=0;
		bang_time=0;
	}
	//把图片事先加载一遍，防止闪烁
	public void load_image(Graphics g){
		for(int i=0;i<2;++i){
			g.drawImage(stand[i],-100,-100,this);
		}
		for(int i=0;i<2;++i){
			g.drawImage(attack[i],-100,-100,this);
		}
		for(int i=0;i<5;++i){
			g.drawImage(boss_attack[i],-100,-100,this);
		}
	}
	//获取坦克的炮口位置的函数
	private Point get_gun_pos(){
		Point p=new Point(x,y+105);
		return p;
	}
	//坦克发炮的函数
	public void attack(){
		int i;
		for(i=0;i<BULLET_MAX_NUM;++i){
			if(!bullet[i].is_exist()){
				break;
			}
		}
		//System.out.println(i);
		if(i<BULLET_MAX_NUM){
			Point gun_pos=get_gun_pos();
			//System.out.println(gun_pos.x+" "+gun_pos.y);
			bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,5,0);
		}
	}
	//boss召唤摩托的函数
	public void boss_attack(){
		int i;
		for(i=0;i<MOTO_MAX_NUM;++i){
			if(!moto[i].is_exist()){
				break;
			}
		}
		if(i<MOTO_MAX_NUM){
			moto[i].init();
		}
	}
	//坦克行动ai
	public void move(){
		if(is_exist()){
			if(action_time==0){
				action=(random.nextInt()%3+3)%3;
				switch(action){
					case 0:
						action_time=STAND_TIME;
						break;
					case 1:
						attack();
						action_time=ATTACK_TIME;
						break;
					case 2:
						action_time=BOSS_ATTACK_TIME;
						break;
				}
			}
		}
	}
	//初始化坦克爆炸参数的函数
	public void init_bang(){
		bang_time=BANG_TIME;
	}
	//判断坦克是否存在的函数
	public boolean is_exist(){
		int width=stand[0].getWidth(this);
		if(x<-width || x>GameFrame.DEFAULT_WIDTH || tough==0)
			return false;
		return true;
	}
	//把坦克加载入碰撞检测数组的函数
	public void draw_in_map(){
		if(is_exist()){
			int width=stand[0].getWidth(this),
				height=stand[0].getHeight(this);
			for(int i=y/3;i<=(y+height)/3;++i){
				if(i>=0 && i<134){
					for(int j=(x+24)/3;j<(x+width)/3;++j){
						if(j>=0 && j<201 && Game._map[i][j]==0){
							Game._map[i][j]=701;
						}
					}
				}
			}
		}
	}
	//画坦克攻击的函数
	public void draw_attack(Graphics g){
		int attack_width=attack[(ATTACK_TIME-action_time)/N].getWidth(this),
			stand_width=stand[0].getWidth(this),
			attack_x=x+stand_width-attack_width,attack_y=y;
		g.drawImage(attack[(ATTACK_TIME-action_time)/N],attack_x,attack_y,this);
	}
	//画boss攻击的函数
	public void draw_boss_attack(Graphics g){
		int attack_width=boss_attack[(BOSS_ATTACK_TIME-action_time)/N].getWidth(this),
			attack_height=boss_attack[(BOSS_ATTACK_TIME-action_time)/N].getHeight(this),
			stand_width=stand[0].getWidth(this),
			stand_height=stand[0].getHeight(this),
			attack_x=x+stand_width-attack_width,
			attack_y=y+stand_height-attack_height;
		g.drawImage(boss_attack[(BOSS_ATTACK_TIME-action_time)/N],attack_x,attack_y,this);
	}
	//画爆炸的函数
	public void draw_bang(Graphics g){
		int tank_height=stand[action_time%2].getHeight(this),
		bang_height=bang[(BANG_TIME-bang_time)/N].getHeight(this),
		bang_x=x,bang_y=y+tank_height-bang_height;
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x,bang_y,this);
		if(Execute.getEsc_flag()==0){
			--bang_time;
			if(bang_time==0){
				Execute.setWin_time(200);
			}
		}
	}
	//画坦克正常情况下的函数
	public void draw_stand(Graphics g){
		int h0=stand[0].getHeight(this),
			h1=stand[1].getHeight(this),
			stand_x=x,stand_y=y;
		if(action_time%2==1)
			stand_y=y+h0-h1;
		g.drawImage(stand[action_time%2],stand_x,stand_y,this);
	}
	//画坦克的函数
	public void draw(Graphics g){
		if(is_exist()){
			if(action_time>0){
				switch(action){
					case 0:
						draw_stand(g);
						break;
					case 1:
						draw_attack(g);
						break;
					case 2:
						draw_boss_attack(g);
						if(action_time==1)
							boss_attack();
						break;
				}
				if(Execute.getEsc_flag()==0)
					--action_time;
			}
		}
		else if(bang_time>0){
			draw_bang(g);
		}
	}
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x=x;
	}
	public int getTough() {
		return tough;
	}
	public void setTough(int tough) {
		if(tough>=0){
			this.tough = tough;
		}
		else{
			this.tough = 0;
		}
	}
}
