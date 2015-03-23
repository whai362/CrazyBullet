import java.awt.*;

import javax.swing.*;
import java.util.*;
public class Enemy extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public static final int MAX_NUM=48,BULLET_MAX_NUM=3;
	public Bullet[] bullet=new Bullet[BULLET_MAX_NUM];
	
	private static final int Y0=252,N=4,STAND_TIME=4*N,RUN_TIME=5*N,DIE_TIME=5*N,ATTACK_TIME=5*N; 
	private static int num=0;
	private int action,action_time=0,id,hp,die_time,init=0,x,y,type,direction;
	private int[] t={0,0,0,0, 0,1,0,2, 1,0,0,1, 2,2,0,0, 0,1,0,2, 0,2,0,1,
					 0,3,0,3, 0,2,0,3, 0,2,0,1, 0,3,0,1, 0,0,0,3, 0,2,0,0};
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Random random=new Random();
	private Image[][][] stand=new Image[3][2][2],
					  	run=new Image[3][5][2],
					  	attack=new Image[3][5][2],
						die=new Image[3][5][2];
	//初始化块
	{
		for(int i=0;i<BULLET_MAX_NUM;++i){
			bullet[i]=new Bullet();
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<2;++j){
				for(int k=0;k<2;++k){
					stand[i][j][k]=kit.getImage("img\\enemy_stand"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					run[i][j][k]=kit.getImage("img\\enemy_run"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					attack[i][j][k]=kit.getImage("img\\enemy_attack"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					die[i][j][k]=kit.getImage("img\\enemy_die"+i+j+k+".png");
				}
			}
		}
	}
	public Enemy(){
		if(num<MAX_NUM){
			init=0;
			id=num;
			x=39+num*150;	//可能错
			y=Y0;
			type=hp=t[num];
			action_time=die_time=direction=0;
			++num;
		}
	}
	public void load_image(Graphics g){
		for(int i=0;i<3;++i){
			for(int j=0;j<2;++j){
				for(int k=0;k<2;++k){
					g.drawImage(stand[i][j][k],-100,100,this);
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					g.drawImage(run[i][j][k],-100,100,this);
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					g.drawImage(attack[i][j][k],-100,100,this);
				}
			}
		}
		for(int i=0;i<3;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					g.drawImage(die[i][j][k],-100,100,this);
				}
			}
		}
	}
	
	public void run(){
		int hight=stand[type-1][0][0].getHeight(this),
			width=run[type-1][(RUN_TIME-action_time)/N][0].getWidth(this);
		if(direction==0){
			//int run[]
			x-=3;
			if(x/3>=0 && x/3<251 && Game.is_in_map((y+hight/2)/3,x/3) && Game._map[(y+hight/2)/3][x/3]/100==1)
				x+=3;
		}
		else{
			x+=3;
			if((x+width)/3>=0 && (x+width)/3<251 && Game.is_in_map((y+hight/2)/3,(x+width)/3) && Game._map[(y+hight/2)/3][(x+width)/3]/100==1)	//可以改漂亮点
				x-=3;
		}
	}
	public void turn(){
		if(direction==0)
			direction=1;
		else
			direction=0;
	}
	public void draw_attack(Graphics g){
		int attack_width=attack[type-1][(ATTACK_TIME-action_time)/N][0].getWidth(this),
			attack_height=attack[type-1][(ATTACK_TIME-action_time)/N][0].getHeight(this),
			stand_width=stand[type-1][0][0].getWidth(this),
			stand_height=stand[type-1][0][0].getHeight(this),
			attack_x=x,attack_y=y+stand_height-attack_height;
		if(direction==0)
			attack_x+=stand_width-attack_width;
		g.drawImage(attack[type-1][(ATTACK_TIME-action_time)/N][direction],attack_x,attack_y,this);
	}
	public void draw_stand(Graphics g){
		g.drawImage(stand[type-1][(STAND_TIME-action_time)/2/N][direction],x,y,this);	//
	}
	public void draw_die(Graphics g){
		int die_x=x,die_y=y,
			h=die[type-1][(DIE_TIME-die_time)/N][0].getHeight(this),
			h0=stand[type-1][0][0].getHeight(this);
		g.drawImage(die[type-1][(DIE_TIME-die_time)/N][direction],die_x,die_y+h0-h,this);
		if(Execute.getEsc_flag()==0)
			--die_time;
		if(die_time==0) type=0;
	}
	public void draw_run(Graphics g){
		g.drawImage(run[type-1][(RUN_TIME-action_time)/N][direction],x,y,this);	//加一句if>0
	}
	public void move(){
		if(is_exist()){
			if(hp>0){
				if(action_time==0){
					action=(random.nextInt()%4+4)%4;
					switch(action){
						case 0:
							action_time=1;
							break;
						case 1:
							action_time=STAND_TIME;
							break;
						case 2:
							action_time=RUN_TIME;
							break;
						case 3:
							action_time=ATTACK_TIME;
							break;
					}
				}
				if(action_time>0){	//1处
					switch(action){
						case 0:
							turn();
							break;
						case 1:
									//stand不会产生位移，所以没有加上
							break;
						case 2:
							run();
							break;
						case 3:
							attack();
							break;
					}
					//--action_time;	//这里在draw的时候会有问题
				}
			}
			else{
				die_time=DIE_TIME;
			}
		}
		else
			action_time=0;
	}
	
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x=x;
	}
	
	boolean is_exist(){
		int width=0;
		if(type>0)
			width=stand[type-1][0][0].getWidth(this);
		if(type==0 || x<-width || x>=600 || die_time>0) return false;
		return true;
	}
	
	public void draw_in_map(){
		if(is_exist()){
			int width=stand[type-1][0][0].getWidth(this),hight=stand[type-1][0][0].getHeight(this);
			for(int i=y/3;i<=(y+hight)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+width)/3;++j){
						if(j>=0 && j<251 && Game._map[i][j]==0)
						Game._map[i][j]=3*100+id;
					}
				}
			}
		}
	}
	public void draw_enemy(Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		if(is_exist()){
			//System.out.println("!!!");
			if(hp>0 && action_time>0){
				switch(action){
					case 0:
									//turn不会调用绘图动作所以没有加上
						break;
					case 1:
						draw_stand(g);
						break;
					case 2:
						draw_run(g);
						break;
					case 3:
						draw_attack(g);
						break;
				}
				if(action_time>0 && Execute.getEsc_flag()==0)
					--action_time;
			}
		}
		else if(die_time>0){
			draw_die(g);
		}
	}
	
	public Point get_gun_pos(){
		int gun_x=x,gun_y=y,
			attack_width=attack[type-1][(ATTACK_TIME-action_time)/N][0].getWidth(this),
			stand_width=stand[type-1][0][0].getWidth(this);
		if(direction==1)
			gun_x+=attack_width;
		else
			gun_x+=stand_width-attack_width;
		if(type==2)
			gun_y+=18;
		else if(type==3)
			gun_y+=12;
		Point p=new Point(gun_x,gun_y);
		return p;
	}
	public void attack(){
		if(action_time==3*N){	//时钟延迟
			if(type>1){
				int i;
				for(i=0;i<BULLET_MAX_NUM;++i){
					if(!bullet[i].is_exist()){
						break;
					}
				}
				if(i<BULLET_MAX_NUM){
					Point gun_pos=get_gun_pos();
					if(type==2)
						bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,0,direction);
					else
						bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,2,direction);
				}
			}
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public static int getNum() {
		return num;
	}

	public static void setNum(int num) {
		Enemy.num = num;
	}
	
}
//人物移动得很快，时钟控制得不对 。 1处 的if写成while了 ok
//人物图片闪烁
//敌人会穿过障碍物  ok 用_map[201]解决 ok
//敌人在半空中死掉，像素点控制得不对  ok
//敌人偶尔会走进障碍物里 边界情况控制不对 还没ok
//敌人会穿过英雄 木有做碰撞检测
//251 用 单词代替
//boss子弹能飞全场 把get_flag 改为is_exist()