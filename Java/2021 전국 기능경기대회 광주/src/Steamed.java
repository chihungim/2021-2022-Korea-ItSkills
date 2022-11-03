

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Steamed extends Basedialog {
	JScrollPane scr;
	JPanel c = new JPanel(new FlowLayout(1));
	int hei = 0;
	
	public Steamed() {
		super("찜한 음식점", 380, 600);
		
		add(scr = new JScrollPane(c));
		
		try {
			ResultSet rs1 = stmt.executeQuery("select f.seller, s.name, c.name, format(s.DELIVERYFEE,'#,##0'), format(avg(minute(cooktime) + hour(cooktime) * 60),'#') as tim from favorite f, seller s, category c, menu m where f.seller = s.no and s.category = c.no and m.seller = s.no and user = "+NO+" group by s.no");
			while(rs1.next()) {
				JPanel temp = new JPanel(new BorderLayout()), in = new JPanel(new GridLayout(0, 1));
				JLabel img = new JLabel(img("프로필/"+rs1.getString(1)+".png",100,100));
				
				temp.add(size(img, 100, 100),"West");
				temp.add(in);
				
				in.add(label(rs1.getString(2), 2));
				in.add(labelP(rs1.getString(3), 2, 12));
				in.add(labelP(rs1.getString(4)+"원 배달 수수료 / 조리 평균 "+rs1.getString(5)+"분", 2, 12));
				
				line(img);
				in.setOpaque(false);
				temp.setOpaque(false);
				temp.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5)));
				emp(in, 0, 5, 0, 0);
				size(temp, 350, 110);
				c.add(temp);
				String name = rs1.getString(2);
				int sno = rs1.getInt(1);
//				String info = getone("SELECT concat(format(DELIVERYFEE,'#,##0'), '원 배달 수수료 / ', concat(minute(min(cooktime)),' ~ ', minute(max(cooktime)),'분'), ' / 평점 ',format(ifnull(avg(rate),0.0),1) ) FROM menu m, seller s left join review r on r.seller = s.no where m.seller = s.no and s.no = "+sno+" group by s.name order by s.no asc");
				temp.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if(e.getClickCount() == 2) {
							new Shop(name, sno).addWindowListener(new be(Steamed.this));
						}
					}
				});
				hei += 110;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		size(c, 350, hei);
		
		scr.setBackground(Color.WHITE);
		c.setBackground(Color.WHITE);
		scr.setBorder(null);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		NO = 1;
		new Steamed();
	}

}
