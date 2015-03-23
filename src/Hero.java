import java.awt.*;
import javax.swing.JPanel;
public class Hero extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public static final int BULLET_MAX_NUM=5;
	public Bullet[] bullet=new Bullet[BULLET_MAX_NUM];
	
	private static final int N=4,MOD1=3*N,MOD2=9*N,X0=-30,Y0=247,ATTACK_TIME=5*N,DIE_TIME=5*N,V=30,HP=6,LIFE=99,SHELL_NUM=10;
	private int x,y,init,
				body_point,run_point,
				jump_time,fall_time,attack_time,die_time,
				v,direction,action,
				front_blocked,back_blocked,down_blocked,
				squat,on_land,in_vehicle,
				hp,life,shell_num,score,gun_type;
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] stand=new Image[2],
					squat_stand=new Image[2];
	private Image[][] run=new Image[9][2],
					  die=new Image[5][2],
					  jump_leg=new Image[7][2];
	private Image[][][] jump_body=new Image[2][7][2],
						attack=new Image[2][5][2],
						body=new Image[2][3][2];
	
	{
		for(int i=0;i<BULLET_MAX_NUM;++i){
			bullet[i]=new Bullet();
		}
		for(int i=0;i<2;++i){ 
			stand[i]=kit.getImage("img\\stand"+i+".png");
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<3;++j){
				for(int k=0;k<2;++k){
					body[i][j][k]=kit.getImage("img\\body"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<9;++i){
			for(int j=0;j<2;++j){
				run[i][j]=kit.getImage("img\\run"+i+j+".png");
			}
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<7;++j){
				for(int k=0;k<2;++k){
					jump_body[i][j][k]=kit.getImage("img\\jump_body"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					attack[i][j][k]=kit.getImage("img\\attack"+i+j+k+".png");
				}
			}
		}
		for(int i=0;i<2;++i){
			squat_stand[i]=kit.getImage("img\\squat_stand"+i+".png");
		}
		for(int i=0;i<5;++i){
			for(int j=0;j<2;++j){
				die[i][j]=kit.getImage("img\\die"+i+j+".png");
			}
		}
		for(int i=0;i<7;++i){
			for(int j=0;j<2;++j){
				jump_leg[i][j]=kit.getImage("img\\jump_leg"+i+j+".png");
			}
		}
	}
	public Hero(){
		x=X0;
		y=Y0;
		init=0;
		body_point=0;
		run_point=0;
		jump_time=0;
		fall_time=0;
		attack_time=0;
		die_time=0;
		v=V;
		direction=1;
		action=0;
		front_blocked=0;
		back_blocked=0;
		down_blocked=0;
		squat=0;
		on_land=1;
		in_vehicle=0;
		hp=HP;
		life=LIFE;
		shell_num=SHELL_NUM;
		score=0;
		gun_type=0;
	}
	private void load_image(Graphics g){
		for(int i=0;i<2;++i){
			g.drawImage(stand[i],-100,-100,this);
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<3;++j){
				for(int k=0;k<2;++k){
					g.drawImage(body[i][j][k],-100,-100,this);
				}
			}
		}
		for(int i=0;i<9;++i){
			for(int j=0;j<2;++j){
				g.drawImage(run[i][j],-100,-100,this);
			}
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					g.drawImage(jump_body[i][j][k],-100,-100,this);
				}
			}
		}
		for(int i=0;i<2;++i){
			for(int j=0;j<5;++j){
				for(int k=0;k<2;++k){
					g.drawImage(attack[i][j][k],-100,-100,this);
				}
			}
		}
		for(int i=0;i<2;++i){
			g.drawImage(squat_stand[i],-100,-100,this);
		}
		for(int i=0;i<5;++i){
			for(int j=0;j<2;++j){
				g.drawImage(die[i][j],-100,-100,this);
			}
		}
		for(int i=0;i<5;++i){
			for(int j=0;j<2;++j){
				g.drawImage(jump_leg[i][j],-100,-100,this);
			}
		}
	}
	//初始化跳跃的函数
	public void init_jump(){
		action=2;
		jump_time=24;
		v=30;
		on_land=0;
	}
	//跳跃控制函数
	public void jump(){
		action=2;	//保证在空中可以移动
		if((jump_time+1)%4==0){
			y-=v;
			v-=5;
		}
	}
	//初始化下落的函数
	public void init_fall(){
		action=3;
		fall_time=24;
		v=0;
		on_land=0;
	}
	//下落控制函数
	public void fall(){
		action=3;	//保证在空中可以移动
		if((fall_time+1)%4==0){
			if(v<=30)
				v+=5;
			if(down_blocked==0)
				y+=v;
			else{
				int i,hight=stand[0].getHeight(this);
				Point hero_pos=getDown_pos();
				for(i=hero_pos.y/3;i>=0;--i){
					if(Game.is_in_map(i,hero_pos.x/3) && Game._map[i][hero_pos.x/3]/100!=1)		//改
						break;
				}
				y=3*i-45-hight;
				action=0;
				fall_time=0;
				on_land=1;
			}
		}
		if(fall_time==0){
			if(down_blocked==0)
				y+=v;
			else{
				int i,height=stand[0].getHeight(this),
				width=body[gun_type][body_point/N][0].getWidth(this);
				Point hero_pos=getDown_pos();
				for(i=hero_pos.y/3;i>=0;--i){
					if(Game.is_in_map(i,(x+width/2)/3) && Game._map[i][(x+width/2)/3]/100!=1)		//改
						break;
				}
				y=3*i-45-height;
				action=0;
				fall_time=0;
				on_land=1;
			}
		}
	}
	//控制移动的函数
	public void run(){
		action=1;
		if(direction==1){
			if(front_blocked==0 && (Execute.getDistance()>=6300 && 
			   x<GameFrame.DEFAULT_WIDTH-305 || x<=GameFrame.DEFAULT_WIDTH*2/5))	//可以改漂亮点
				x+=3;
		}
		else if(back_blocked==0 && x>0)
			x-=3;
	}
	//获取枪位置的函数
	public Point get_gun_pos(){
		int attack_width=attack[gun_type][(ATTACK_TIME-attack_time)/N][0].getWidth(this),
			body_width=body[gun_type][body_point/N][0].getWidth(this),
			squat_hight=squat_stand[0].getHeight(this),
			gun_x=x,gun_y=y+15;
		if(direction==1)
			gun_x+=attack_width;
		else
			gun_x+=body_width-attack_width;
		if(jump_time>0 || fall_time>0)
			gun_y-=10;
		if(action==0 && squat==1)
			gun_y+=squat_hight/2;
		Point p=new Point(gun_x,gun_y);
		return p;
	}
	//射击函数
	public void shoot(){
		attack_time=ATTACK_TIME;
		if(Execute.getClk()==0){	//时钟延迟
			int i;
			for(i=0;i<BULLET_MAX_NUM;++i){
				if(!bullet[i].is_exist()){
					break;
				}
			}
			if(i<BULLET_MAX_NUM){
				Point gun_pos;
				if(gun_type==0){
					gun_pos=get_gun_pos();
					//System.out.println(gun_pos.x+" "+gun_pos.y);
					bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,0,direction);
				}
				else if(gun_type==1){
					//System.out.println(gun_pos.x+" "+gun_pos.y);
					if(shell_num>0){
						gun_pos=get_gun_pos();
						bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,4,direction);
						--shell_num;
					}
				}
			}
			Execute.setClk(5); //时钟延迟
		}
	}
	//切换枪的函数
	public void shift_gun(){
		if(Execute.getClk()==0){	//时钟延迟
			if(gun_type==0)
				gun_type=1;
			else
				gun_type=0;
			Execute.setClk(5); //时钟延迟
		}
	}
	//重生的函数
	public void reborn(){
		--life;
		hp=6;
		shell_num=shell_num>10? shell_num:10;
		on_land=1;
		y=jump_time=attack_time=0;
	}
	//初始化英雄死亡的函数
	public void init_die(){
		die_time=DIE_TIME;
	}
	//检测英雄是否活着的函数
	public boolean is_alive(){
		if(hp>0)
			return true;
		return false;
	}
	//检测是否在空中的函数
	public boolean is_in_air(){		//这个名字不好
		//int width=body[0][0].getWidth(this);
		Point hero_pos=getDown_pos();
		if(on_land==1 && Game.is_in_map(hero_pos.y/3+1,hero_pos.x/3) && Game._map[hero_pos.y/3+1][hero_pos.x/3]/100!=1)
			return true;
		return false;
	}
	//检测是否在地上的函数
	public boolean is_on_land(){
		if(on_land==0) return false;
		return true;
	}
	//检测是否在下落的函数
	public boolean is_fall(){
		if(jump_time==0 && on_land==0)
			return true;
		return false;
	}
	//把英雄加载入碰撞检测数组的函数
	public void draw_in_map(){
		int width=stand[0].getWidth(this),
			height=78,squat_height=60;
		if(squat==0){
			for(int i=y/3;i<=(y+height)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+width)/3;++j){
						if(j>=0 && j<201){
							Game._map[i][j]=201;
						}
					}
				}
			}
		}
		else{
			for(int i=(y+height-squat_height)/3;i<=(y+height)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+width)/3;++j){
						if(j>=0 && j<201){
							Game._map[i][j]=201;
						}
					}
				}
			}
		}
	}
	//画下落的函数
	public void draw_fall(Graphics g){
		int tmp=fall_time/4;
		g.drawImage(jump_leg[tmp][direction],x+32-jump_leg[tmp][0].getWidth(null)/2,y+45+tmp-13,this);	//越界
	 	if(attack_time==0)
	 		g.drawImage(jump_body[gun_type][tmp][direction],x,y-13,this);
	 	else{
	 		int attack_x=x,attack_y=y-8,
	 			jump_width=jump_body[gun_type][tmp][0].getWidth(this),
	 			attack_width=attack[gun_type][(ATTACK_TIME-attack_time)/N][0].getWidth(this);
	 		if(direction==0)
	 			attack_x=x+jump_width-attack_width;
			draw_attack(attack_x,attack_y,g);
		}
	 	if(fall_time>0 && Execute.getEsc_flag()==0)		//防止越界
	 		--fall_time;
	}
	//画跳跃的函数
	public void draw_jump(Graphics g){
		int tmp=6-jump_time/4;
		//System.out.println(tmp+" "+jump_time);
		g.drawImage(jump_leg[tmp][direction],x+32-jump_leg[tmp][0].getWidth(null)/2,y+45+tmp-13,this);	//////可以改
		if(attack_time==0)
			g.drawImage(jump_body[gun_type][tmp][direction],x,y-13,this);
		else{
			int attack_x=x,attack_y=y-8,
				jump_width=jump_body[gun_type][tmp][0].getWidth(this),
				attack_width=attack[gun_type][(ATTACK_TIME-attack_time)/N][0].getWidth(this);
			if(direction==0)
				attack_x=x+jump_width-attack_width;
			draw_attack(attack_x,attack_y,g);
		}
		if(Execute.getEsc_flag()==0)
			--jump_time;
		if(jump_time==0){
			init_fall();
		}
	}
	//画移动的函数
	public void draw_run(Graphics g){
		int body_width=body[gun_type][body_point/N][0].getWidth(this),
			leg_width=run[run_point/N][0].getWidth(this);
		if(direction==1) g.drawImage(run[run_point/N][direction],x+(body_width-leg_width-16)/2,y+45,this);	//优化位置
		else g.drawImage(run[run_point/N][direction],x+(body_width-leg_width+16)/2,y+45,this);
		if(Execute.getEsc_flag()==0)
			run_point=(run_point+1)%MOD2;
		if(attack_time==0){
			g.drawImage(body[gun_type][body_point/N][direction],x,y,this);
			if(Execute.getEsc_flag()==0)
				body_point=(body_point+1)%MOD1;
		}
		else{
			int attack_x=x,attack_y=y,
				attack_width=attack[gun_type][(ATTACK_TIME-attack_time)/N][0].getWidth(this);
			if(direction==0)
				attack_x=x+body_width-attack_width;
			draw_attack(attack_x,attack_y,g);
		}
		action=0;
	}
	//画站的函数
	public void draw_stand(Graphics g){
		int body_width=body[gun_type][body_point/N][0].getWidth(this),
		leg_width=stand[0].getWidth(this);
		if(direction==1){
			if(squat==0)
				g.drawImage(stand[direction],x+(body_width-leg_width-16)/2,y+45,this);
			else
				g.drawImage(squat_stand[direction],x+(body_width-leg_width-16)/2,y+45,this);
		}
		else{
			if(squat==0)
				g.drawImage(stand[direction],x+(body_width-leg_width+16)/2,y+45,this);
			else
				g.drawImage(squat_stand[direction],x+(body_width-leg_width+16)/2,y+45,this);
		}
		if(attack_time==0){
			int stand_x=x,stand_y=y,
				squat_hight=squat_stand[0].getHeight(this);;
			if(squat==1)
				stand_y+=squat_hight/2;
			g.drawImage(body[gun_type][body_point/N][direction],stand_x,stand_y,this);
			if(Execute.getEsc_flag()==0)
				body_point=(body_point+1)%MOD1;
		}
		else{
			int attack_x=x,attack_y=y,
				attack_width=attack[gun_type][(ATTACK_TIME-attack_time)/N][0].getWidth(this),
				squat_hight=squat_stand[0].getHeight(this);
			if(direction==0)
				attack_x=x+body_width-attack_width;
			if(squat==1)
				attack_y+=squat_hight/2;
			draw_attack(attack_x,attack_y,g);
		}
	}
	//画英雄死的函数
	public void draw_die(Graphics g){
		int die_height=die[(DIE_TIME-die_time)/N][0].getHeight(this),
			stand_height=78,
			die_x=x,die_y=y+stand_height-die_height;
		g.drawImage(die[(DIE_TIME-die_time)/N][direction],die_x,die_y,this);
		if(Execute.getEsc_flag()==0)
			--die_time;
	}
	//画攻击的函数
	public void draw_attack(int x,int y,Graphics g){
		g.drawImage(attack[gun_type][(ATTACK_TIME-attack_time)/N][direction],x,y,this);
		if(Execute.getEsc_flag()==0)
			--attack_time;
	}
	//画英雄的函数
	public void draw(Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		if(in_vehicle==0){
			if(is_alive()){
				switch(action){
					case 0:
						draw_stand(g);
						break;
					case 1:
						draw_run(g);
						break;
					case 2:
						draw_jump(g);
						break;
					case 3:
						draw_fall(g);
						break;
				}
			}
			else if(die_time>0){
				draw_die(g);
			}
			else if(life>0){
				reborn();
			}
			else{
				Execute.setGameover_time(60);
			}
				
		}
	}
	public int getJump_time(){
		return jump_time;
	}
	public void setJump_time(int jump_time){
		this.jump_time=jump_time;
	}
	public int getX(){
		return x;
	}
	public void setX(int x){
		this.x=x;
	}
	public void setDirection(int direction){
		this.direction=direction;
	}
	public int getDirection(){
		return direction;
	}
	public void setSquat(int squat){
		this.squat=squat;
	}
	public int getBlocked(String dir){
		if(dir.equals("front"))
			return front_blocked;
		else if(dir.equals("back"))
			return back_blocked;
		else
			return down_blocked;
	}
	public void setBlocked(String dir,int blocked){
		if(dir.equals("front"))
			front_blocked=blocked;
		else if(dir.equals("back"))
			back_blocked=blocked;
		else
			down_blocked=blocked;
	}
	public int getY(){
		return y;
	}
	public Point getFront_pos(){
		int width=body[gun_type][body_point/N][0].getWidth(this);
		Point p=new Point(x+width,y);
		return p;
	}
	public Point getBack_pos(){
		Point p=new Point(x-3,y);
		return p;
	}
	public Point getDown_pos(){
		int width=body[gun_type][body_point/N][0].getWidth(this),
		hight=stand[0].getHeight(this);
		Point p;
		if(on_land==1) 
			p=new Point(x+width/2,y+45+hight+3);	//45可以改下
		else
			p=new Point(x+width/2,y+45+hight+v+3);
		return p;
	}
	public int getAttack_time(){
		return attack_time;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		if(hp>=0 && hp<=6)
			this.hp = hp;
		else if(hp>6)
			this.hp = 6;
		else
			this.hp = 0;
	}
	public int getLife() {
		return life;
	}
	public void setLife(int life) {
		this.life = life;
	}
	public int getShell_num() {
		return shell_num;
	}
	public void setShell_num(int shell_num) {
		this.shell_num = shell_num;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public int getIn_vehicle() {
		return in_vehicle;
	}
	public void setIn_vehicle(int in_vehicle) {
		this.in_vehicle = in_vehicle;
	}
}
//jump的姿势不太对 ok 解决方法 ()
//往左走，英雄腿部和身体不对齐 ok
//跳跃会穿过障碍物 木有碰撞检测 ok
//跳到敌人头上会有bug ok
//英雄碰到敌人的y坐标会有bug
//从障碍物下来后 gun的位置不太准 fall_time 木有及时清零 ok
// _map使用前先判下是否越界