import java.awt.*;
import javax.swing.JPanel;
public class Menu extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final int N=20,MOD=16;
	private int action_time=0,select=0,
				menu_x=0,name_x=130,
				begin_x=200+24,maker_x=200,
				operate_x=200,
				menu_y=0,name_y=50,
				begin_y=156,maker_y=280,
				operate_y=215,
				point=0,esc_select=0;
	private int[] x={100,150,300,400},
				  y={250,100,150,200};
	private Toolkit kit=Toolkit.getDefaultToolkit();
	private Image menu=kit.getImage("img\\menu.jpg"),
				  name=kit.getImage("img\\name.png"),
				  begin=kit.getImage("img\\begin.png"),
				  maker=kit.getImage("img\\maker.png"),
				  operate=kit.getImage("img\\operate.png"),
				  embellish=kit.getImage("img\\embellish.png"),
				  show_op=kit.getImage("img\\show_op.png");
	private Image[] show_maker=new Image[2],
					esc_selectect=new Image[2];
	{
		for(int i=0;i<2;++i){
			show_maker[i]=kit.getImage("img\\show_maker"+i+".png");
		}
		for(int i=0;i<2;++i){
			esc_selectect[i]=kit.getImage("img\\esc"+i+".png");
		}
	}
	//切换主菜单的选项的函数
	public boolean shift(String command){
		if(command.equals("w") || command.equals("s")){
			switch(select){
				case 0:
					begin_x-=24;
					//begin_y-=12;
					break;
				case 1:
					operate_x-=24;
					//operate_y-=12;
					break;
				case 2:
					maker_x-=24;
					//maker_y-=12;
					break;
			}
			if(command.equals("w")){
				select=(select-1+3)%3;
			}
			else if(command.equals("s")){
				select=(select+1)%3;
			}
			switch(select){
				case 0:
					begin_x+=24;
					//begin_y+=12;
					break;
				case 1:
					operate_x+=24;
					//operate_y+=12;
					break;
				case 2:
					maker_x+=24;
					//maker_y+=12;
					break;
			}
			return true;
		}
		return false;
	}
	//切换退出询问框选项的函数
	public boolean shiftEsc_select(String command){
		if(command.equals("a")){
			esc_select-=1;
			esc_select=(esc_select+2)%2;
			return true;
		}
		else if(command.equals("d")){
			esc_select+=1;
			esc_select=(esc_select+2)%2;
			return true;
		}
		return false;
	}
	//画操作说明的函数
	public void drawShow_operate(Graphics g){
		g.drawImage(show_op,0,0,this);
	}
	//画制作人的函数
	public void drawShow_maker(Graphics g){
		g.drawImage(show_maker[point/(MOD/2)],0,0,this);
		point=(point+1)%MOD;
	}
	//画退出询问框的函数
	public void drawEsc_select(Graphics g){
		g.drawImage(esc_selectect[esc_select],100,100,this);
	}
	//画主菜单的函数
	public void draw(Graphics g){
		if(action_time%N<3){
			g.drawImage(menu,menu_x,menu_y+3,this);
			g.drawImage(name,name_x,name_y+3,this);
		}
		else{
			g.drawImage(menu,menu_x,menu_y,this);
			g.drawImage(name,name_x,name_y,this);
		}
		g.drawImage(begin,begin_x,begin_y,this);
		g.drawImage(operate,operate_x,operate_y,this);
		g.drawImage(maker,maker_x,maker_y,this);
		for(int i=0;i<action_time/N;++i){
			g.drawImage(embellish,x[i],y[i],this);
		}
		if(action_time<5*N-1 && Execute.getEsc_flag()==0)
			++action_time;
	}
	public int getEsc_select() {
		return esc_select;
	}
	public void setEsc_select(int esc_select) {
		this.esc_select = esc_select;
	}
	public int getSelect(){		//这样写不太美观
		return select+1;
	}
}
