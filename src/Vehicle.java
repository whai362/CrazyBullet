import java.awt.*;
import javax.swing.JPanel;
public class Vehicle extends JPanel{
	private static final long serialVersionUID = 1L;
	public static final int BULLET_MAX_NUM=5;
	public Bullet[] bullet=new Bullet[BULLET_MAX_NUM];
	private static final int N=4,X0=60,Y0=134,MOD1=3*N,MOD2=4*N,BANG_TIME=5*N;
	private int init,x,y,p1,p2,broken,stop,bang_time;	//可以考虑一下构造函数
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image[] v=new Image[20],
					bang=new Image[5];
	{
		for(int i=0;i<BULLET_MAX_NUM;++i){
			bullet[i]=new Bullet();
		}
		for(int i=0;i<15;++i){
			v[i]=kit.getImage("img\\v"+i+".png");	//改图片包命名规则
		}
		for(int i=0;i<5;++i){
			bang[i]=kit.getImage("img\\vehicle_bang"+i+".png");
		}
	}
	public Vehicle(){
		init=0;
		x=X0;
		y=Y0;
		p1=0;
		p2=0;
		broken=0;
		stop=1;
		bang_time=0;
	}
	private void load_image(Graphics g){
		for(int i=0;i<15;++i){
			g.drawImage(v[i],-100,-100,this);
		}
	}
	//获取车的位置
	public Point get_vehicle_pos(){
		Point p=new Point(x+336,y+160);		//可能位置还需要微调
		return p;
	}
	//获取第一个枪的位置
	public Point get_gun1_pos(){
		Point p=new Point(x+245+91,y+93);
		return p;
	}
	//获取第二个枪的位置
	public Point get_gun2_pos(){
		Point p=new Point(x+67+122,y-60);
		return p;
	}
	//枪攻击的函数
	public void shoot(String command){
		if(Execute.getClk()==0){	//时钟延迟
			int i;
			for(i=0;i<BULLET_MAX_NUM;++i){
				if(!bullet[i].is_exist()){
					break;
				}
			}
			if(i<BULLET_MAX_NUM){
				Point gun_pos;
				if(command.equals("j")){
					gun_pos=get_gun1_pos();
					bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,1,1);
				}
				else if(command.equals("k")){
					gun_pos=get_gun2_pos();
					bullet[i].set_bullet(gun_pos.x,gun_pos.y,1,3,1);
				}
			}
			Execute.setClk(5); //时钟延迟
		}
	}
	//检测车是否损坏
	public boolean is_exist(){
		if(broken==0) return true;
		return false;
	}
	//画爆炸的函数
	public void draw_bang(Graphics g){		//爆炸的参数可以微调
		int bang_x=x,bang_y=y,width=bang[0].getWidth(this);
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x,bang_y,this);
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x+width/2,bang_y,this);
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x+width,bang_y,this);
		if(Execute.getEsc_flag()==0)
			--bang_time;
	}
	//画车
	public void draw(Hero hero,Graphics g){
		if(init==0){
			load_image(g);
			init=1;
		}
		if(is_exist()){
			if(Execute.getEsc_flag()==0){
				p1=(p1+1)%MOD1;
				if(stop==0) p2=(p2+1)%MOD2;
			}
			g.drawImage(v[0],x,y,this);
			if(hero.getIn_vehicle()==0)
				g.drawImage(v[1],x+183,y+32,this);
			else
				g.drawImage(v[14],x+183,y+32,this);
			g.drawImage(v[2],x+305,y+130,this);
			g.drawImage(v[3+p1/N],x,y+52,this);
			g.drawImage(v[3+p1/N],x+60,y+52,this);
			g.drawImage(v[3+p1/N],x+120,y+52,this);
			
			g.drawImage(v[6+p2/N],x+12,y+125,this);
			g.drawImage(v[6+p2/N],x+235,y+125,this);
			
			g.drawImage(v[10],x,y-25,this);
			g.drawImage(v[11],x+63,y-33,this);
			g.drawImage(v[12],x+67,y-65,this);
			g.drawImage(v[13],x+245,y+90,this);
		}
		else if(hero.getIn_vehicle()==1 && bang_time==0){
			hero.setIn_vehicle(0);
			bang_time=BANG_TIME;
		}
		if(bang_time>0){
			draw_bang(g);
		}
	}
	public int getStop(){
		return stop;
	}
	public void setStop(int stop){
		this.stop=stop;
	}
	public int getY(){
		return y;
	}
	public void setY(int y){
		if(y>=124 && y<=164)
			this.y=y;
	}
	public void setBroken(int broken){
		this.broken=broken;
	}
}
//车子动的方式可以修改下