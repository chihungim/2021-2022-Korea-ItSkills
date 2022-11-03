package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import db.DBManager;

public class Chart extends BaseFrame {
	JComboBox<String> combo;
	Color col[] = new Color[7];
	
	public Chart() {
		super("통계", 800, 770);
		setLayout(new GridLayout(1, 0));

		//더 쉬운 방법
		int r=34, g=34, b=255;
		for (int i = 6; i >= 0; i--) {
			col[i] = new Color(r, g, b);
			r+=17; g+=17;
		}
		
//		for (int i = 6, pos = 1; i >= 0; i--) {
//			if(i == 6) {
//				col[i] = new Color(34, 34, 255);
//				continue;
//			}
//			int r = col[i + 1].getRed(), b = col[i + 1].getBlue(), g = col[i+ 1].getGreen();
//			col[i] = pos == 0? new Color(r+17, g+17, b) : new Color(r+34, g+34, b);
//			pos = pos == 0 ?1:0;
//		}
		
		add(new TimePanel());
		add(new ChartPanel());
		((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		
		setVisible(true);
	}
	
	class TimePanel extends JPanel {
		JPanel c, cc, s, sc, ss;
		JLabel jl[][] = new JLabel[24][7];
		String cap[] = "12,2,4,6,8,10".split(",");
//		String cap[] = "12, ,2, ,4, ,6, ,8, ,10, ,".split(",");
		String str[] = "일,월,화,수,목,금,토".split(",");
		String aa[] = "최근 7일,최근 30일,최근 90일".split(",");
		
		public TimePanel() {
			super(new BorderLayout());
			
			add(lbl("자주 이용하는 시간대", 2, 20),"North");
			add(sz(c = new JPanel(new FlowLayout()), 400, getHeight()));
			
			c.add(sz(lbl("시간 별 이용자 수", 2), 355, 30),"North");
			c.add(cc = new JPanel(new GridLayout(0, 8, 2, 2)));
			c.add(sz(s = new JPanel(new BorderLayout(5, 10)), 350, 60), "South");
			
			for (int i = 0; i < jl.length; i++) {
//			for (int i = 0, j = 0, pos = 0; i < jl.length; i++, j++) {
				for (int k = 0; k < jl[0].length; k++) {
					jl[i][k] = new JLabel("",0);
					jl[i][k].setOpaque(true);
					jl[i][k].setBackground(Color.LIGHT_GRAY);
					jl[i][k].setForeground(Color.RED);

					cc.add(jl[i][k]);
//					cc.add(sz(jl[i][k], 400 / 10, 20));
				}
				cc.add(lbl(i%2==0 ? cap[(i/2)%6] + (i<12?" 오전":" 오후") : "", 2, 12, Color.BLACK));
//				if(j == cap.length) {
//					j = 0;
//					pos = 1;
//				}
//				cc.add(lbl(cap[j]+" "+(!cap[j].equals(" ")?pos == 0 ?"오전":"오후":""), 2, 12, Color.BLACK));
			}
			
			for (int i = 0; i < str.length; i++) {
				cc.add(lbl(str[i], 0));
			}
			s.add(sz(sc = new JPanel(new GridLayout(1, 0, 5, 5)), 1, 30));
			
			for (int i = 0; i < col.length; i++) {
				JLabel jl = new JLabel((i * 5)+"+",2);
//				JLabel jl = new JLabel("+"+(i * 5)+"",2);
				sc.add(jl);
				jl.setForeground(Color.RED);
				jl.setOpaque(true);
				jl.setBackground(col[i]);
			}
			
			s.add(ss = new JPanel(new BorderLayout()),"South");
			ss.add(combo = new JComboBox<String>());
			s.setOpaque(false);
			sc.setOpaque(false);
			ss.setOpaque(false);
			
			c.setBackground(Color.WHITE);
			cc.setBackground(Color.WHITE);
			
			for(var li: aa) {
				combo.addItem(li);
			}
			evnt();
			
			combo.setSelectedIndex(0);
			combo.addItemListener(a->evnt());
		}

		private void evnt() {
			Arrays.stream(jl).flatMap(a->Arrays.stream(a)).forEach(a->a.setText(""));
			Arrays.stream(jl).flatMap(a->Arrays.stream(a)).forEach(a->a.setBackground(Color.LIGHT_GRAY));
			
			try {
				System.out.println("select hour(`time`), sum(if(weekday(`date`) = 6, 1, 0)) as '일', sum(if(weekday(`date`) = 0, 1, 0)) as '월', sum(if(weekday(`date`) = 1, 1, 0)) as '화', sum(if(weekday(`date`) = 2, 1, 0)) as '수', sum(if(weekday(`date`) = 3, 1, 0)) as '목',sum(if(weekday(`date`) = 4, 1, 0)) as '금', sum(if(weekday(`date`) = 5, 1, 0)) as '토' from purchase where `date` >= now() and `date` <= DATE_ADD(now(), INTERVAL "+combo.getSelectedItem().toString().replaceAll("[^0-9]", "")+" DAY) group by `time` order by `time` desc");
				var rs = DBManager.rs("select hour(`time`), sum(if(weekday(`date`) = 6, 1, 0)) as '일', sum(if(weekday(`date`) = 0, 1, 0)) as '월', sum(if(weekday(`date`) = 1, 1, 0)) as '화', sum(if(weekday(`date`) = 2, 1, 0)) as '수', sum(if(weekday(`date`) = 3, 1, 0)) as '목',sum(if(weekday(`date`) = 4, 1, 0)) as '금', sum(if(weekday(`date`) = 5, 1, 0)) as '토' from purchase where `date` <= '2021-10-08' and `date` >= DATE_SUB('2021-10-08', INTERVAL "+combo.getSelectedItem().toString().replaceAll("[^0-9]", "")+" DAY) group by `time` order by `time` desc");
				while(rs.next()) {
					for (int i = 0; i < 7; i++) {
						if(rs.getInt(i + 2) == 0) continue;
						jl[rs.getInt(1)][i].setText(rei(jl[rs.getInt(1)][i].getText()) + rs.getInt(i + 2)+"");
						jl[rs.getInt(1)][i].setBackground(col[rei(jl[rs.getInt(1)][i].getText()) / 5]);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	class ChartPanel extends JPanel {
		JLabel cj;
		JPanel c, jp;
		private double tot = 0 , cur = 0;
		int sar = 0, arc = 0;
		Color col[] = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN.darker(), Color.BLUE, Color.PINK, Color.DARK_GRAY};
		ArrayList<Integer> list = new ArrayList<Integer>();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		
		ArrayList list1 = new ArrayList(), list2 = new ArrayList();
		
		public ChartPanel() {
			setLayout(new BorderLayout());
			add(lbl("자주 이용하는 회원", 2, 20), "North");

			add(c=new JPanel(new GridLayout(0, 1)));
			int rank=1;
			try {
				var rs = DBManager.rs("select u.serial, u.name, count(*) from user u, purchase p where p.`user` = u.serial group by u.`name` order by count(*) desc limit 0,8");
				while(rs.next()) {
					tot+=rs.getInt(3);
					list1.add(rs.getInt(3));
					list2.add((rank++) + "등 " + rs.getString(2) + " (" + rs.getInt(3) + "회)");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			//원형 차트 생성
			sar = 85;
			c.add(cj = new JLabel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					Graphics2D g2 = (Graphics2D) g;
					
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					for (int i = 0; i < 7; i++) {
						g2.setColor(col[i]);
						arc = (int)((int)list1.get(i)/tot * 360) + 1;
						g2.fillArc(60, 10, 300, 300, sar, -arc);
						sar = sar-arc;
						cur += (int)list1.get(i);
					}
				}
			});
			
			//범례 생성
			JPanel tmpjp1, tmpjp2;
			JLabel tmpjl;
			c.add(jp = new JPanel(new GridLayout(0, 1)));
			for(int i=0; i<list2.size(); i++) {
				jp.add(tmpjp1 = new JPanel());
				tmpjp1.add(tmpjp2 = new JPanel());
				tmpjp2.setPreferredSize(new Dimension(20, 20));
				tmpjp2.setBackground(col[i]);
				tmpjp1.add(tmpjl = new JLabel(list2.get(i).toString(), JLabel.CENTER));
			}
	
//			try {
//				var rs = DBManager.rs("select u.serial, u.name, count(*) from user u, purchase p where p.`user` = u.serial group by u.`name` order by count(*) desc limit 0,8");
//				while(rs.next()) {
//					list.add(rs.getInt(3));
//					map.put(rs.getInt(1), rs.getString(2));
//				}
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
			
//			setChart();
			
//			c.add(jp = new JPanel(new GridLayout(0, 1)));
//			for(Integer n : map.keySet()) {
//				JPanel temp = new JPanel(new FlowLayout(1));
//				JPanel rec = new JPanel(null);
//				rec.setBackground(col[n - 1]);
//				temp.add(BaseFrame.sz(rec, 20, 20));
//				temp.add(BaseFrame.lbl(n+"등 "+map.get(n)+" ("+list.get(7 - n)+"회)", 2));
//				jp.add(temp);
//			}
		}

//		private void setChart() {
//			for(var v : list) {
//				tot += v;
//			}
//			
//			Collections.reverse(list);
//			
//			c.add(cj = new JLabel() {
//				@Override
//				public void paint(Graphics g) {
//					super.paint(g);
//					Graphics2D g2 = (Graphics2D) g;
//					
//					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//					for (int i = 0; i < list.size(); i++) {
//						sar = (int)(cur * 360 / tot) + 80;
//						int arc = (int)(list.get(i) * 360 / tot) + 1;
//						
//						g2.setColor(col[6 - i]);
//						g2.fillArc(60, 10, 300, 300, sar, arc);
//						cur += list.get(i);
//					}
//				}
//			});
//		}
	}

	public static void main(String[] args) {
		new Chart();
	}

}
