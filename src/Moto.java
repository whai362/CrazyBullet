import java.awt.*;

import javax.swing.*;
public class Moto extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int N=4,X0=600,Y0=237,BANG_TIME=5*N;
	private int attack,x,y,point=0,flag,hp,id,bang_time=0;
	private Toolkit kit=Toolkit.getDefaultToolkit();
	public Image[] moto=new Image[2],
				   bang=new Image[5];
	{
		for(int i=0;i<2;++i){
			moto[i]=kit.getImage("img\\moto"+i+".png");
		}
		for(int i=0;i<5;++i){
			bang[i]=kit.getImage("img\\vehicle_bang"+i+".png");
		}
	}
	public Moto(){
		x=X0;
		y=Y0;
		flag=0;
		id=0;
	}
	public void init(){
		x=X0;
		y=Y0;
		attack=1;
		hp=3;
		flag=1;
	}
	public void init_bang(){
		flag=0;
		bang_time=BANG_TIME;
	}
	public boolean is_exist(){
		if(flag==0 || hp==0)
			return false;
		return true;
	}
	//把摩托加载入碰撞检测数组的函数
	public void draw_in_map(){
		if(is_exist()){
			int width=moto[point].getWidth(this),
				height=moto[point].getHeight(this);
			for(int i=y/3;i<=(y+height)/3;++i){
				if(i>=0 && i<134){
					for(int j=x/3;j<=(x+width)/3;++j){
						if(j>=0 && j<=201 && Game._map[i][j]==0){
							Game._map[i][j]=600+id;
						}
					}
				}
			}
		}
	}
	//画爆炸的函数
	public void draw_bang(Graphics g){		//爆炸的参数可以微调
		int moto_height=moto[point].getHeight(this),
			bang_height=bang[(BANG_TIME-bang_time)/N].getHeight(this),
			bang_x=x,bang_y=y+moto_height-bang_height;
		g.drawImage(bang[(BANG_TIME-bang_time)/N],bang_x,bang_y,this);
		if(Execute.getEsc_flag()==0)
			--bang_time;
	}
	//画摩托的函数
	public void draw(Graphics g){
		int width=moto[point].getWidth(this);
		if(x<-width){
			flag=0;
		}
		if(is_exist()){
			//System.out.println(x);
			g.drawImage(moto[point],x,y,this);
			if(Execute.getEsc_flag()==0){
				point=(point+1)%2;
				x-=6;
			}
		}
		else if(bang_time>0){
			draw_bang(g);
		}
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		if(hp>=0)
			this.hp = hp;
		else
			this.hp = 0;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getAttack() {
		return attack;
	}
	public void setAttack(int attack) {
		this.attack = attack;
	}
}
