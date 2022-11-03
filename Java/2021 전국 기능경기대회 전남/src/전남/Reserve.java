package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Reserve extends Baseframe {
	
	item p[] = new item[5];
	String cap[]= "달력,지역,매장,테마,시간".split(",");
	LocalDate selDate = LocalDate.now();
	String loc[] = "전국,서울,광주,충남,대구,대전,경기,부산,인천,세종,전남,경남,강원,제주,충북,경북".split(",");
	String selLoc, selCafe, selThm, selTime;
	JPanel pDate, pLoc, pCafe, pThm, pTime;
	JPanel n, c;
	
	public Reserve() {
		super("예약", 1110, 550);
		
		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(c = new JPanel(new FlowLayout(0)));
		
		n.add(label("방탈출 예약", 0, 30));
		n.add(label("Room Escape Reservation", 0, 20));
		
		int width[] = { 340, 80, 280, 280, 80 };
		for (int i = 0; i < p.length; i++) {
			c.add(size(p[i] = new item(), width[i], 420));
			p[i].title.setText(cap[i]);
		}
		
		c.setBackground(Color.WHITE);
		
		p[0].add(pDate = new JPanel(new GridLayout(0, 7, 5, 5)));
		p[1].add(pLoc = new JPanel(new GridLayout(0, 1, 5, 5)));
		p[2].add(new JScrollPane(pCafe = new JPanel(new GridLayout(0, 1, 5, 5))));
		p[3].add(new JScrollPane(pThm = new JPanel(new GridLayout(0, 1, 5, 5))));
		p[4].add(pTime = new JPanel(new GridLayout(0, 1, 5, 5)));
		
		setCal();
		setLoc();
		setCaf();
		setTime();
		
		setVisible(true);
	}
	
	public Reserve(String loc, String caf) {
		this();
		
		selLoc = loc;
		selCafe = caf;
		
		for(var i : pLoc.getComponents()) {
			iteml item = ((iteml)i);
			item.setBackground(null);
			
			if(item.getText().equals(loc)) {
				item.setBackground(Color.ORANGE);
				setCaf();
			}
		}
		
		for(var i : pCafe.getComponents()) {
			iteml item = ((iteml)i);
			item.setBackground(null);
			
			if(item.getText().equals(caf)) {
				item.setBackground(Color.ORANGE);
				setThm();
			}
		}
	}
	
	private void setThm() {
		pThm.removeAll();
		
		String div[] = getone("select c_division from cafe where c_name = '"+selCafe+"'").split(",");
		
		iteml item[] = new iteml[div.length];
		
		
		for (int i = 0; i < item.length; i++) {
			pThm.add(item[i] = new iteml(getone("select t_name from theme where t_no = "+div[i]), 0));
			
			item[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < item.length; j++) {
						item[j].setBackground(null);
					}
					iteml j = (iteml)e.getSource();
					j.setBackground(Color.ORANGE);
					selThm = j.getText();
					setTime();
				}
			});
		}
		
		for(int i = 0; i< 16 - item.length;i++) {
			pThm.add(new JLabel());
		}
		repaint();
		revalidate();
	}
	private void setTime() {
		pTime.removeAll();
		ArrayList<iteml> item = new ArrayList<iteml>();
		
		LocalTime e = LocalTime.of(10, 30);
		for (int i = 0; i < 15; i++) {
			iteml jl;
			if(i==14) {
				item.add(jl = new iteml("24:30", 0));
			}else
				item.add(jl = new iteml(e.toString(), 0));
			
			String caf = getone("select c_no from cafe where c_name = '"+selCafe+"'");
			String theme = getone("select t_no from theme where t_name = '"+selThm+"'");
			
			String res = getone("select r_time from reservation where c_no = '"+caf+"' and r_date = '"+selDate+"' and t_no = '"+theme+"'");
			
			if(res.equals(e.toString())) jl.setForeground(Color.LIGHT_GRAY);
			
			if(e.toSecondOfDay() < LocalTime.now().toSecondOfDay()) jl.setForeground(Color.LIGHT_GRAY);
			
			pTime.add(item.get(i));
			e=e.plusHours(1);
			
			item.get(i).addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < item.size(); j++) {
						item.get(j).setBackground(null);
					}
					var l = ((iteml)e.getSource());
					if(l.getForeground().equals(Color.LIGHT_GRAY)) return;
					l.setBackground(Color.orange);
					selTime = l.getText();
					new Purchase(selDate, selCafe, selThm, selTime).addWindowListener(new be(Reserve.this));
				}
			});
		}
		
		try {
			ResultSet rs = stmt.executeQuery("select * from reservation r, cafe c, theme t where r.c_no = c.c_no and r.t_no = t.t_no and c.c_name = '"+selCafe+"' and t.t_name = '"+selThm+"'");
			while(rs.next()) {
				for (int i = 0; i < item.size(); i++) {
					if(item.get(i).getText().equals(rs.getString("r_time")))
						item.get(i).setEnabled(true);
					
				}
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		repaint();
		revalidate();
	}

	private void setLoc() {
		p[1].add(pLoc);
		iteml item[] = new iteml[loc.length];
		for (int i = 0; i < loc.length; i++) {
			pLoc.add(item[i] = new iteml(loc[i], 0));
			
			item[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < item.length; j++) {
						item[j].setBackground(null);
					}
					iteml j = (iteml)e.getSource();
					j.setBackground(Color.ORANGE);
					selLoc = j.getText();
					setCaf();
				}
			});
		}
		item[0].setBackground(Color.ORANGE);
		selLoc = "전국";
	}
	
	private void setCal() {
		JLabel prev = new JLabel("<html><font color = \"white\"◀</html>"), next = new JLabel("<html><font color = \"white\"▶</html>");
		p[0].n.add(prev, "West");
		p[0].n.add(next, "East");
		
		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setMonth(-1);
			}
		});
		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setMonth(1);
			}

		});
		
		drawCal();
	}
	
	private void setMonth(int i) {
		selDate = selDate.plusMonths(i);
		drawCal();
	}
	
	private void drawCal() {
		pDate.removeAll();
		LocalDate date = LocalDate.of(selDate.getYear(), selDate.getMonthValue(), 1);
		int sday = date.getDayOfWeek().getValue()%7, lday = date.lengthOfMonth();
		
		p[0].title.setText(date.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(Locale.forLanguageTag("en"))));
		String week[] = "SUN,MON,TUE,WED,THU,FRI,SAT".split(",");
		for (int i = 0; i < week.length; i++) {
			pDate.add(label(week[i], 0, 12));
		}
		
		iteml item[] = new iteml[42];
		for (int i = 0; i < 42; i++) {
			int d = i - sday + 1;
			if (d<1) {
				pDate.add(item[i] = new iteml("", 0));
			} else if (d<=lday) {
				pDate.add(item[i] = new iteml(d+"", 0));
				if(d<LocalDate.now().getDayOfMonth()) {
					item[i].setEnabled(false);
				}
			} else {
				pDate.add(item[i] = new iteml("", 0));
			}
			
			item[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < item.length; j++) {
						item[j].setBackground(null);
					}
					var l = ((iteml)e.getSource());
					l.setBackground(Color.orange);
					
					selDate = LocalDate.of(selDate.getYear(), selDate.getMonthValue(), d);
				}
			});
		}
		
		for (int i = 0; i < item.length; i++) {
			if (item[i].getText().equals(selDate.getDayOfMonth()+"") && date.getMonth().equals(LocalDate.now().getMonth())) {
				item[i].setBackground(Color.orange);
			}
		}
	}

	private void setCaf() {
		pCafe.removeAll();
		ArrayList<iteml> item = new ArrayList<iteml>();
		
		try {
			String sql = "select c_name from cafe";
			if (!selLoc.equals("전국")) {
				sql += " where left(c_address, 2) = '"+selLoc+"'";
			}
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				item.add(new iteml(rs.getString(1), 0));
				pCafe.add(item.get(item.size()-1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < item.size(); i++) {
			item.get(i).addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < item.size(); j++) {
						item.get(j).setBackground(null);
					}
					var l = ((iteml)e.getSource());
					l.setBackground(Color.orange);
					selCafe = l.getText();
					setThm();
				}
			});
		}
		repaint();
		revalidate();
	}

	class item extends JPanel {
		JPanel n,c;
		JLabel title;
		
		public item() {
			super(new BorderLayout());
			this.add(Baseframe.size(n = new JPanel(new BorderLayout()), 1, 40), "North");
			n.setBackground(Color.black);
			n.setOpaque(true);
			n.add(title = new JLabel("", 0));
			title.setForeground(Color.white);
			this.add(c = new JPanel(new BorderLayout()));
		}
	}
	
	class iteml extends JLabel {
		public iteml(String tit, int alig) {
			super(tit, alig);
			this.setOpaque(true);
		}
		
		public boolean isBackground() {
			return this.getBackground().equals(Color.orange);
		}
	}
	

}
