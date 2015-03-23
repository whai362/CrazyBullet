import java.awt.*;
import javax.swing.*;

//游戏主类
public class Game{
	//用于碰撞检测的矩阵
	public static int[][] _map=new int[134][251];	//0:无 1:障碍/地 2:英雄 3:敌人 4:地雷 5:宝贝 6:摩托兵 7:坦克
	//判断是否在矩阵内，复制越界
	public static boolean is_in_map(int x,int y){
		if(x>=0 && x<134 && y>=0 && y<251)
			return true;
		return false;
	}
	private static Keyboard keyboard=null;
	public static void main(String[] args){
		GameFrame frame=new GameFrame();
		keyboard=new Keyboard(frame);	//键盘监听
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	//返回键盘信息
	public static Keyboard command(){
		return keyboard;
	}
}

//游戏框架类
class GameFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_WIDTH=600,DEFAULT_HEIGHT=439;
	public GameFrame(){
		setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);
		setLocationByPlatform(true);
		setTitle("CrazyBullet");
		add(new Execute());
	}
}
//游戏运行类
class Execute extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;
	private static final int DELAY=8,LOGO_TIME=6*8;	//载入延迟常量
	//不用初始化变量
	private static int logo_time=LOGO_TIME,gameover_time=0,win_time=0;
	private Logo logo=new Logo();
	private Gameover gameover=new Gameover();
	private Win win=new Win();
	//要初始化变量
	private static int delay,clk,select,esc_select,esc_flag,openning_flag,distance;		//delay载入延迟，clk时钟延迟(防止按键过快)
	private Map map;
	private Vehicle vehicle;
	private Hero hero;
	private Tank tank;
	private Menu menu;
	private Status status;
	private Barrier[] barrier=new Barrier[Barrier.MAX_NUM];
	private Enemy[] enemy=new Enemy[Enemy.MAX_NUM];
	private Mine[] mine=new Mine[Mine.MAX_NUM];
	public Execute(){
		init();		//初始化游戏
		Thread thr=new Thread(this);
		thr.start();
	}
	//初始化函数
	private void init(){
		delay=0;
		clk=0;
		select=0;
		esc_select=0;
		esc_flag=0;
		openning_flag=1;
		distance=0;
		map=new Map();
		vehicle=new Vehicle();
		hero=new Hero();
		tank=new Tank();
		menu=new Menu();
		status=new Status();
		Barrier.setNum(0);
		Enemy.setNum(0);
		Mine.setNum(0);
		{
			for(int i=0;i<Barrier.MAX_NUM;++i){
				barrier[i]=new Barrier();
			}
			for(int i=0;i<Enemy.MAX_NUM;++i){
				enemy[i]=new Enemy();
			}
			for(int i=0;i<Mine.MAX_NUM;++i){
				mine[i]=new Mine();
			}
		}
	}
	Toolkit kit=Toolkit.getDefaultToolkit();	//加载图片所需要的变量
	private Image iBuffer=null;		//双缓冲
	private Graphics gBuffer=null;
	public void run(){
		while(true){
			try{
				Thread.sleep(25);
				if(clk>0)
					--clk;
				if(select==1){
					if(esc_flag==0){	
						if(openning_flag==1)
							openning_action();
						else{
							init_map();
							control();
							load_game_element();
							collision_detection();
						}
					}
					else{
						loadEsc_select();
					}
				}
				repaint();
			}
			catch(InterruptedException e){}
		}
	}
	public void update(Graphics g){
		if(iBuffer==null){
			iBuffer=createImage(this.getSize().width,this.getSize().height);
			gBuffer=iBuffer.getGraphics();
		}
		if(logo_time>0){
			logo.draw(gBuffer);
			--logo_time;
		}
		else if(gameover_time>0){
			gameover.draw(gBuffer);
			--gameover_time;
			if(gameover_time==0){
				select=0;
				init();
				Keyboard.clearKeyboard();
			}
		}
		else if(win_time>0){
			win.draw(hero,gBuffer);
			--win_time;
			if(win_time==0){
				select=0;
				init();
				Keyboard.clearKeyboard();
			}
		}
		else if(select!=1){
			loadSelect();
			switch(select){
				case 0:
					menu.draw(gBuffer);
					break;
				case 1:
					break;
				case 2:
					menu.drawShow_operate(gBuffer);
					break;
				case 3:
					menu.drawShow_maker(gBuffer);
					break;
			}
		}
		else if(select==1){
			map.draw_map(gBuffer);
			if(delay>DELAY){
				for(int i=0;i<Barrier.MAX_NUM;++i){
					barrier[i].draw(gBuffer);
				}
				vehicle.draw(hero,gBuffer);
				hero.draw(gBuffer);
				
				for(int i=0;i<Enemy.MAX_NUM;++i){
					enemy[i].draw_enemy(gBuffer);
				}
				for(int i=0;i<Mine.MAX_NUM;++i){
					mine[i].draw(gBuffer);
				}
				tank.draw(gBuffer);
				for(int i=0;i<Tank.MOTO_MAX_NUM;++i){
					tank.moto[i].draw(gBuffer);
				}
				
				if(vehicle.is_exist()){
					for(int i=0;i<Vehicle.BULLET_MAX_NUM;++i){
						vehicle.bullet[i].draw(gBuffer);
					}
				}
				else{
					for(int i=0;i<Hero.BULLET_MAX_NUM;++i){
						hero.bullet[i].draw(gBuffer);
					}
				}
				
				for(int i=0;i<Enemy.MAX_NUM;++i){
					for(int j=0;j<Enemy.BULLET_MAX_NUM;++j){
						enemy[i].bullet[j].draw(gBuffer);
					}
				}
				
				for(int i=0;i<Tank.BULLET_MAX_NUM;++i){
					tank.bullet[i].draw(gBuffer);
				}
				
				status.draw(hero,gBuffer);
			}
			else ++delay;
			if(esc_flag==1){
				menu.drawEsc_select(gBuffer);
			}
		}
		g.drawImage(iBuffer,0,0,this);
	}
	public void paint(Graphics g){
		update(g);
	}
	
	//加载开场动作函数
	private void openning_action(){
		//车的开场动作
		if(vehicle.is_exist()) vehicle.setStop(1);
		//英雄的开场动作
		if(hero.getJump_time()==0){
			int door_x=vehicle.get_vehicle_pos().x-154;
			if(hero.getIn_vehicle()==0 && hero.getX()<=door_x){
				hero.run();
			}
			else if(hero.getIn_vehicle()==0 && hero.getX()>door_x)
				hero.init_jump();
		}
		else{
			if(hero.getJump_time()==1){
				hero.setIn_vehicle(1);
				hero.setJump_time(0);
				openning_flag=0;
			}
			hero.jump();
		}
	}
	
	//整体往前移函数
	public void move_forward(){
		distance+=3;
		map.setX(map.getX()-3);
		for(int i=0;i<Barrier.MAX_NUM;++i){
			barrier[i].setX(barrier[i].getX()-3);
		}
		for(int i=0;i<Enemy.MAX_NUM;++i){
			enemy[i].setX(enemy[i].getX()-3);
		}
		for(int i=0;i<Mine.MAX_NUM;++i){
			mine[i].setX(mine[i].getX()-3);
		}
		for(int i=0;i<Tank.MOTO_MAX_NUM;++i){
			if(tank.moto[i].is_exist())
				tank.moto[i].setX(tank.moto[i].getX()-3);
		}
		tank.setX(tank.getX()-3);
	}
	
	//把游戏元素加载入碰撞检测矩阵的函数
	private void load_game_element(){
		//加载障碍
		for(int i=0;i<Barrier.MAX_NUM;++i){
			barrier[i].draw_in_map();
		}
		//控制地雷
		for(int i=0;i<Mine.MAX_NUM;++i){
			mine[i].draw_in_map();
		}
		//加载敌人
		for(int i=0;i<Enemy.MAX_NUM;++i){
			enemy[i].move();
			enemy[i].draw_in_map();
		}
		//加载摩托
		for(int i=0;i<Tank.MOTO_MAX_NUM;++i){
			tank.moto[i].draw_in_map();
		}
		//加载坦克
		tank.move();
		tank.draw_in_map();
		//加载英雄
		if(hero.is_in_air()){	//由于碰撞检测需要，把fall放这
			hero.init_fall();
		}
		if(hero.is_fall()){
			hero.fall();
		}
		if(hero.is_alive())
			hero.draw_in_map();
	}
	
	//加载退出询问菜单的函数
	private void loadEsc_select(){
		if(Game.command()!=null){
			String[] command=Game.command().get_keyboard();
			for(int i=0;i<Keyboard.MAX_NUM;++i){
				if(clk==0){
					if(menu.shiftEsc_select(command[i]))
						break;
				}
				if(command[i].equals("enter")){
					if(menu.getEsc_select()==0){
						select=0;
						init();
						Keyboard.clearKeyboard();
					}
					break;
				}
			}
			if(clk==0)
				clk=5;
		}
	}
	
	//加载主菜单的函数
	private void loadSelect(){
		if(Game.command()!=null){
			String[] command=Game.command().get_keyboard();
			for(int i=0;i<Keyboard.MAX_NUM;++i){
				if(clk==0)
					menu.shift(command[i]);
				if(command[i].equals("enter")){
					select=menu.getSelect();
				}
				else if(command[i].equals("esc")){
					select=0;
				}
			}
			if(clk==0)
				clk=5;
		}
	}
	private void control(){
		if(vehicle.is_exist())
			vehicle.setStop(0);
		if(Game.command()!=null){
			String[] command=Game.command().get_keyboard();
			//退出询问菜单控制
			for(int i=0;i<Keyboard.MAX_NUM;++i){
				if(command[i].equals("esc")){
					esc_flag=1;
					menu.setEsc_select(esc_select);
					clk=5;
					return ;
				}
			}
			//控制车
			if(vehicle.is_exist()){
				if(distance<2300){
					move_forward();
					for(int i=0;i<Keyboard.MAX_NUM;++i){
						if(command[i].equals("w"))
							vehicle.setY(vehicle.getY()-3);
						else if(command[i].equals("s"))
							vehicle.setY(vehicle.getY()+3);
						else if(command[i].equals("j")){
							vehicle.shoot(command[i]);
						}
						else if(command[i].equals("k")){
							vehicle.shoot(command[i]);
						}
					}
				}
				else{
					hero.init_fall();
					vehicle.setBroken(1);
				}
			}
			//控制英雄
			else if(hero.is_alive()){
				int squat=0;
				for(int i=0;i<Keyboard.MAX_NUM;++i){
					if(command[i].equals("w")){}
					else if(command[i].equals("s")){
						squat=1;
					}
					else if(command[i].equals("a")){
						hero.setDirection(0);
						hero.run();
					}
					else if(command[i].equals("d")){
						hero.setDirection(1);
						hero.run();
						if(distance<6300 && hero.getBlocked("front")==0 && hero.getX()>GameFrame.DEFAULT_WIDTH*2/5){		//可以改漂亮点
							move_forward();
						}
					}
					else if(command[i].equals("j")){
						if(hero.getAttack_time()<=8){
							//hero.attack();
							hero.shoot();
						}
					}
					else if(command[i].equals("k")){
						if(hero.getAttack_time()<=8){
							hero.shift_gun();
						}
					}
					else if(command[i].equals("l") && hero.is_on_land()){
						hero.init_jump();
					}
				}
				if(squat==1)
					hero.setSquat(1);
				else
					hero.setSquat(0);
			}
			if(hero.getJump_time()>0)
				hero.jump();
		}
	}
	
	//检测是否碰撞的函数
	public static int is_collided(Point p){
		int x=p.x/3,y=p.y/3;
		if(x>=0 && x<201 && y>=0 && y<134){
			return Game._map[y][x]/100;
		}
		return 0;
	}
	
	//游戏碰撞检测函数
	private void collision_detection(){
		//车子弹碰撞检测
		for(int i=0;i<Vehicle.BULLET_MAX_NUM;++i){
			Point bullet_pos=vehicle.bullet[i].get_pos();
			if(vehicle.bullet[i].is_exist()){
				int id,obj=is_collided(bullet_pos);		//这里可以改得好看点
				switch(obj){
					case 0:
						break;
					case 1:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						barrier[id].setTough(barrier[id].getTough()-1);
						vehicle.bullet[i].setFlag(0);
						break;
					case 3:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						hero.setScore(hero.getScore()+enemy[id].getType()*1000);
						enemy[id].setHp(enemy[id].getHp()-1);
						vehicle.bullet[i].setFlag(0);
						break;
				}
			}
		}
		//英雄的子弹碰撞检测
		for(int i=0;i<Hero.BULLET_MAX_NUM;++i){
			if(hero.bullet[i].is_exist()){
				Point bullet_pos=hero.bullet[i].get_pos();
				int id,obj=is_collided(bullet_pos),bullet_type=hero.bullet[i].getType();		//这里可以改得好看点
				switch(obj){
					case 0:
						break;
					case 1:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						if(bullet_type==0)
							barrier[id].setTough(barrier[id].getTough()-1);
						else
							barrier[id].setTough(0);
						hero.bullet[i].setFlag(0);
						break;
					case 3:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						if(bullet_type==0){
							if(enemy[id].getHp()==1)
								hero.setScore(hero.getScore()+enemy[id].getType()*1000);
							enemy[id].setHp(enemy[id].getHp()-1);
						}
						else{
							hero.setScore(hero.getScore()+enemy[id].getType()*1000);
							enemy[id].setHp(0);
						}
						hero.bullet[i].setFlag(0);
						break;
					case 6:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						if(bullet_type==0){
							if(tank.moto[id].getHp()==1)
								hero.setScore(hero.getScore()+3000);
							tank.moto[id].setHp(tank.moto[id].getHp()-1);
						}
						else{
							hero.setScore(hero.getScore()+3000);
							tank.moto[id].setHp(0);
						}
						if(tank.moto[id].getHp()==0)
							tank.moto[id].init_bang();
						hero.bullet[i].setFlag(0);
						break;
					case 7:
						if(bullet_type==0)
							tank.setTough(tank.getTough()-1);
						else
							tank.setTough(tank.getTough()-5);
						if(tank.getTough()==0)
							tank.init_bang();
						hero.bullet[i].setFlag(0);
						break;
				}
			}
		}
		//敌人子弹碰撞检测
		for(int i=0;i<Enemy.MAX_NUM;++i){
			for(int j=0;j<Enemy.BULLET_MAX_NUM;++j){
				if(enemy[i].bullet[j].is_exist()){
					Point bullet_pos=enemy[i].bullet[j].get_pos();
					int id,obj=is_collided(bullet_pos);		//这里可以改得好看点
					switch(obj){
						case 0:
							break;
						case 1:
							id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
							if(barrier[id].getTough()>0){
								switch(enemy[i].getType()){
									case 1:
									case 2:
										barrier[id].setTough(barrier[id].getTough()-1);
										break;
									case 3:
										barrier[id].setTough(barrier[id].getTough()-2);
										break;
								}
								enemy[i].bullet[j].setFlag(0);
							}
							break;
						case 2:
							if(hero.getHp()>0){
								switch(enemy[i].getType()){
									case 1:
									case 2:
										hero.setHp(hero.getHp()-1);
										break;
									case 3:
										hero.setHp(hero.getHp()-2);
										break;
								}
								if(hero.getHp()==0)
									hero.init_die();
								enemy[i].bullet[j].setFlag(0);
							}
							break;
					}
				}
			}
		}
		//坦克子弹碰撞检测
		for(int i=0;i<Tank.BULLET_MAX_NUM;++i){
			if(tank.bullet[i].is_exist()){
				Point bullet_pos=tank.bullet[i].get_pos();
				int id,obj=is_collided(bullet_pos);		//这里可以改得好看点
				switch(obj){
					case 0:
						break;
					case 1:
						id=Game._map[bullet_pos.y/3][bullet_pos.x/3]%100;	//这里可以改得好看点
						if(barrier[id].getTough()>0){
							barrier[id].setTough(barrier[id].getTough()-3);
							tank.bullet[i].setFlag(0);
						}
						break;
					case 2:
						if(hero.getHp()>0){
							hero.setHp(hero.getHp()-3);
							if(hero.getHp()==0)
								hero.init_die();
							tank.bullet[i].setFlag(0);
						}
						break;
				}
			}
		}
		//车碰撞检测
		if(hero.getIn_vehicle()==1){
			Point vehicle_pos=vehicle.get_vehicle_pos();
			int id,obj=is_collided(vehicle_pos);
			switch(obj){
				case 0:
					break;
				case 1:
					id=Game._map[vehicle_pos.y/3][vehicle_pos.x/3]%100;	//这里可以改得好看点
					barrier[id].setTough(0);
					break;
				case 3:
					id=Game._map[vehicle_pos.y/3][vehicle_pos.x/3]%100;	//这里可以改得好看点
					enemy[id].setHp(0);
					break;
			}
		}
		
		//英雄碰撞检测
		if(hero.getIn_vehicle()==0){
			//前面碰撞检测
			int id,obj,blocked=0,hight=78;
			Point hero_pos=hero.getFront_pos();
			for(int i=hero_pos.y/3;i<(hero_pos.y+hight)/3;++i){
				if(Game.is_in_map(i,hero_pos.x/3)){
					id=Game._map[i][hero_pos.x/3]%100;
					obj=is_collided(new Point(hero_pos.x,i*3));
					switch(obj){
						case 1:
							blocked=1;
							break;
						case 4:
							if(mine[id].is_exist()){
								hero.setHp(hero.getHp()-2);
								if(hero.getHp()==0)
									hero.init_die();
								mine[id].init_bang();
							}
							break;
						case 5:
							hero.setScore(hero.getScore()+barrier[id].getGift()*5000);
							switch(barrier[id].getGift()){
								case 1:
									hero.setHp(hero.getHp()+1);
									break;
								case 2:
									hero.setHp(hero.getHp()+2);
									break;
								case 3:
									hero.setShell_num(hero.getShell_num()+1);
									break;
								case 4:
									hero.setLife(hero.getLife()+1);
							}
							barrier[id].setGift(0);
							break;
						case 6:
							if(hero.is_alive() && tank.moto[id].getAttack()==1){
								hero.setHp(hero.getHp()-3);
								if(hero.getHp()==0)
									hero.init_die();
							}
							tank.moto[id].setAttack(0);
							break;
					}
				}
			}
			hero.setBlocked("front",blocked);
			//后面碰撞检测
			blocked=0;
			hero_pos=hero.getBack_pos();
			for(int i=hero_pos.y/3;i<(hero_pos.y+hight)/3;++i){
				if(Game.is_in_map(i,hero_pos.x/3)){
					id=Game._map[i][hero_pos.x/3]%100;
					obj=is_collided(new Point(hero_pos.x,i*3));
					switch(obj){
						case 1:
							blocked=1;
							break;
						case 4:
							if(mine[id].is_exist()){
								hero.setHp(hero.getHp()-2);
								if(hero.getHp()==0)
									hero.init_die();
								//System.out.println(hero.getHp());
								mine[id].init_bang();
							}
							break;
						case 5:
							hero.setScore(hero.getScore()+barrier[id].getGift()*5000);
							switch(barrier[id].getGift()){
								case 1:
									hero.setHp(hero.getHp()+1);
									break;
								case 2:
									hero.setHp(hero.getHp()+2);
									break;
								case 3:
									hero.setShell_num(hero.getShell_num()+1);
									break;
								case 4:
									hero.setLife(hero.getLife()+1);
							}
							barrier[id].setGift(0);
							break;
						case 6:
							if(hero.is_alive() && tank.moto[id].getAttack()==1){
								hero.setHp(hero.getHp()-3);
								if(hero.getHp()==0)
									hero.init_die();
							}
							tank.moto[id].setAttack(0);
							break;
					}
				}
			}
			hero.setBlocked("back",blocked);
			//下面碰撞检测
			blocked=0;
			hero_pos=hero.getDown_pos();
			obj=is_collided(hero_pos);
			if(obj==1 || hero_pos.y>324)
				blocked=1;
			hero.setBlocked("down",blocked);
		}
	}
	
	//初始化_map数组
	private void init_map(){
		for(int i=0;i<=108;++i){
			for(int j=0;j<Game._map[i].length;++j){
				Game._map[i][j]=0;
			}
		}
		for(int i=109;i<Game._map.length;++i){
			for(int j=0;j<Game._map[i].length;++j){
				Game._map[i][j]=100;
			}
		}
	}
	
	public static int getClk(){
		return clk;
	}
	public static void setClk(int _clk){
		clk=_clk;
	}
	public static int getDistance() {
		return distance;
	}
	public static void setDistance(int distance) {
		Execute.distance = distance;
	}
	public static int getEsc_flag() {
		return esc_flag;
	}
	public static void setEsc_flag(int esc_flag) {
		Execute.esc_flag = esc_flag;
	}
	public static int getGameover_time() {
		return gameover_time;
	}
	public static void setGameover_time(int gameover_time) {
		Execute.gameover_time = gameover_time;
	}
	public static int getWin_time() {
		return win_time;
	}
	public static void setWin_time(int win_time) {
		Execute.win_time = win_time;
	}
}
//碰撞检测位置不对	改变碰撞检测方法，把他打到一个数组里
//数组无效 原因：使用前初始化了，在repaint阶段才填数 解决方法： 添加一个draw_in_map函数
//改架构 控制和绘图分开 ok
//可以写一个jump函数 代替 set_jump_time()
//跳上障碍物后下不来 ok
//子弹向后碰撞检测有误差 ok
//车子弹的 碰撞检测不太好
//右边子弹 x有bug ok += 写成 =+ 了
//可以考虑一下向上打
//踩雷应该掉两滴血，但却空血了 ok 重复检测到一个雷了