package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import db.DBManager;

public class PurchaseList extends BaseFrame {
	
	String week[] = "일,월,화,수,목,금,토".split(",");
	JPanel cc;
	JLabel lblDate;
	LocalDate date = LocalDate.now();
	DayBox day[] = new DayBox[42];
	
	public PurchaseList() {
		super("결제내역", 1000, 700);
		
		this.add(n = new JPanel(new FlowLayout()), "North");
		this.add(c = new JPanel(new BorderLayout()));
		
		var cn = new JPanel(new GridLayout(0, 7));
		c.add(cn, "North");
		for (int i = 0; i < week.length; i++) {
			var l = new JLabel(week[i], 0);
			cn.add(l);
			if (i==0) {
				l.setForeground(Color.red);
			} else if (i == 6) l.setForeground(Color.blue);
		}
		c.add(cc = new JPanel(new GridLayout(0, 7)));
		
		n.add(btn("◀", a->{
			setMonth(-1);
		}));
		n.add(lblDate = lblP(tFormat(date, "yyyy년 MM월"), 0, 13));
		n.add(btn("▶", a->{
			setMonth(1);
		}));
		
		for (int i = 0; i < day.length; i++) {
			cc.add(day[i] = new DayBox());
			if (i%7==0) day[i].d.setForeground(Color.red);
			if (i%7==6) day[i].d.setForeground(Color.red);
		}
		
		setCal();
		
		this.setVisible(true);
	}
	
	void setCal() {
		date = date.of(date.getYear(), date.getMonthValue(), 1);
		int sday = date.getDayOfWeek().getValue()%7, eday = date.lengthOfMonth();
		
		for (int i = 0; i < day.length; i++) {
			var tmp = date.plusDays(i-sday);
			Color col = (i % 7 == 0 ? Color.red : (i % 7 == 6 ? Color.blue : Color.black));
			day[i].d.setText(tmp.getDayOfMonth()+"");
			System.out.println(tmp.getDayOfMonth());
			day[i].cont.setText("");
			if (tmp.getMonthValue() != date.getMonthValue()) {
				day[i].d.setForeground(new Color(col.getRed(), col.getGreen(), col.getBlue(), 127));
			} else {
				day[i].d.setForeground(col);
				try {
					var rs = DBManager.rs("SELECT s1.name, s2.name FROM purchase pu, station s1, station s2 where user = "+uno+" and date= '"+tmp.toString()+"' and s1.serial=pu.departure and s2.serial=pu.arrive order by date, pu.serial");
					String station = "<html><font face = '맑은 고딕'>";
					while (rs.next()) {
						station += rs.getString(1)+"→"+rs.getString(2)+"<br>";
					}
					day[i].cont.setText(station);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		repaint();
		revalidate();
	}
	
	void setMonth(int m) {
		date = date.plusMonths(m);
		lblDate.setText(tFormat(date, "yyyy년 MM월"));
		setCal();
	}
	
	class DayBox extends JPanel {
		
		JLabel d, cont;
		JScrollPane scr;
		
		public DayBox() {
			this.setLayout(new BorderLayout());
			this.add(d = lbl("", 4), "North");
			this.add(scr=new JScrollPane(cont = new JLabel("", 2)));
			cont.setVerticalAlignment(JLabel.TOP);
			
			scr.setBorder(new LineBorder(Color.LIGHT_GRAY));
			setLine(this);
		}
	}
	
}
