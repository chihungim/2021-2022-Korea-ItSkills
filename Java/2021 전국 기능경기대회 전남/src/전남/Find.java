package 전남;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class Find extends Baseframe {
	
	String loc[] = "지역선택,서울,광주,충남,대구,대전,경기,부산,인천,세종,전남,경남,강원,제주,충북,경북".split(",");
	JComboBox combo[] = {
		new JComboBox(loc),	
		new JComboBox()
	};
	ArrayList<String> jumpo = new ArrayList<String>();
	HashMap<String, ArrayList<String>> list = new HashMap<String, ArrayList<String>>();
	JPanel n, c;
	
	public Find() {
		super("지역별 지점", 950, 860);
		
		this.add(n = new JPanel(new FlowLayout(1)), "North");
		this.add(new JScrollPane(c = new JPanel(new GridLayout(0, 4, 10, 10))));
		
		for (var com : combo)
			n.add(size(com, 150, 30));
		
		for (int i = 0; i < loc.length; i++) {
			list.put(loc[i], new ArrayList<String>());
		}
		
		for (int i = 0; i < loc.length; i++) {
			try {
				ResultSet rs = stmt.executeQuery("SELECT distinct SUBSTRING_INDEX(c_name, ' ', -1) FROM cafe where c_address like '%"+loc[i]+"%'");
				while (rs.next()) {
					list.get(loc[i]).add(rs.getString(1));
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		combo[1].addItem("지점선택");
		combo[0].addItemListener(a->{
			if (combo[0].getSelectedIndex()==0) return;
			combo[1].removeAllItems();
			combo[1].addItem("지점선택");
			for (int i = 0; i < list.get(combo[0].getSelectedItem()).size(); i++) {
				combo[1].addItem(list.get(combo[0].getSelectedItem()).get(i));
			}
		});
		
		
		combo[1].addItemListener(a->{
			if (combo[1].getSelectedIndex()==-1) return;
			search();
		});
		
		
		this.setVisible(true);
	}
	
	void search() {
		c.removeAll();
		try {
			ResultSet rs= stmt.executeQuery("SELECT * FROM cafe where c_name like '%"+combo[1].getSelectedItem()+"%' and c_address like '%"+combo[0].getSelectedItem()+"%'");
			while (rs.next()) {
				c.add(new ItemPanel(rs.getString(2)));
				
			}
			for (int i = c.getComponentCount(); i < 12; i++) {
				c.add(size(new JPanel(), 210, 210));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();
		revalidate();
	}
	
	class ItemPanel extends JPanel {
		
		JLabel lbl;
		
		public ItemPanel(String tit) {
			super(new BorderLayout());
			
			
			this.add(new JLabel("", 0) {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					Graphics2D g2 = (Graphics2D)g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
					g.setClip(new Ellipse2D.Float(20f, 0, 180, 180));
					g.drawImage(Toolkit.getDefaultToolkit().getImage("./Datafiles/지점/"+tit.split(" ")[0]+".jpg"), 20, 0, 180, 180, null);
					g.drawOval(20, 0, 180, 180);
					
					repaint();
				}
			});
			
			var s = new JPanel(new GridLayout(0, 1));
			var s_l = new JPanel();
			s.add(label(tit, 0, 15));
			s.add(s_l);
			this.add(s, "South");
			
			s_l.add(btn("지점소개", a->{
				cNAME = tit;
				cfNAME = tit.split(" ")[0];
				new intro().addWindowListener(new be(Find.this));
			}));
			s_l.add(btn("예약하기", a->{
				new Reserve(combo[0].getSelectedItem()+"", tit).addWindowListener(new be(Find.this));
			}));
		
		}
	}
	
	public static void main(String[] args) {
		new Find();
	}
	
}
