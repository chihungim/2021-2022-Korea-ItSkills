package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

public class TimePicker extends JDialog {
	
	MatteBorder border = new MatteBorder(0, 0, 2, 0, BaseFrame.blue);
	JLabel title;
	JPanel c, s;
	Reserve r;
	CalPanel cal;
	SpinPanel spin;
	
	public TimePicker(Reserve r) {
		this.r = r;
		this.setTitle("시간 선택");
		this.setModal(true);
		this.setSize(700, 500);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(2);
		
		this.add(title = BaseFrame.lblP("탑승하실 날짜와 시간을 선택해주세요.", 2, 20), "North");
		title.setBorder(border);
		
		this.add(c = new JPanel(new GridLayout()));
		this.add(s = new JPanel(new GridLayout(0, 1)), "South");
		
		c.add(spin=new SpinPanel());
		c.add(cal=new CalPanel());
		
		s.add(BaseFrame.btn("해당 시간으로 변경하기", a->{
			if (cal.d.isBefore(LocalDate.now())) {
				BaseFrame.eMsg("이미 지난 날짜는 선택할 수 없습니다.");
				return;
			}
			if (spin.t.isBefore(LocalTime.now().plusMinutes(30))) {
				BaseFrame.eMsg("이미 지난 시간은 선택할 수 없습니다.");
				return;
			}
			
			r.time = spin.t;
			r.date = cal.d;
			r.mkTimeTable();
			r.setTime();
			dispose();
		}));
		
//		this.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				cal.chk.stop();
//			}
//		});
		
		BaseFrame.setEmpty(cal, 30, 0, 0, 0);
		BaseFrame.setEmpty((JPanel)getContentPane(), 10, 10, 10, 10);
		this.setVisible(true);
	}
	
	class SpinPanel extends JPanel {
		JLabel pr1, ne1, pr2, ne2;
		JLabel hour, min;
		int h = r.time.getHour(), m = r.time.getMinute();
		LocalTime t;
		JPanel hp = new JPanel(new BorderLayout(80, 80));
		JPanel mp = new JPanel(new BorderLayout(80, 80));
		
		public SpinPanel() {
			super(new FlowLayout(1, 50, 50));
			
			this.add(hp);
			this.add(BaseFrame.lbl(":", 0, 25));
			this.add(mp);
			
			hp.add(ne1 = BaseFrame.lbl("▲", 0, 25), "North");
			hp.add(hour = BaseFrame.lbl(String.format("%02d", h), 0, 25));
			hp.add(pr1 = BaseFrame.lbl("▼", 0, 25), "South");
			
			mp.add(ne2 = BaseFrame.lbl("▲", 0, 25), "North");
			mp.add(min = BaseFrame.lbl(String.format("%02d", m), 0, 25));
			mp.add(pr2 = BaseFrame.lbl("▼", 0, 25), "South");
			
			t = LocalTime.of(h, m, 0);
			
			ne1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (h >= 23) return;
					h++;
					hour.setText(String.format("%02d", h));
					t = LocalTime.of(h, m, 0);
				}
			});
			pr1.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (h <= 0) return;
					h--;
					hour.setText(String.format("%02d", h));
					t = LocalTime.of(h, m, 0);
				}
			});
			
			ne2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(m >= 59) return;
					m++;
					min.setText(String.format("%02d", m));
					t = LocalTime.of(h, m,0);
				}
			});
			
			pr2.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(m <= 0) return;
					m--;
					min.setText(String.format("%02d", m));
					t = LocalTime.of(h, m,0);
				}
			});
			
			
			
		}
	}
	
	class CalPanel extends JPanel {
		JPanel n, c;
		JLabel prev, next, date;
		String str[] = "Sun,Mon,Tue,Wed,Thu,Fri,Sat".split(",");
		LocalDate l = LocalDate.now(), d;
		JLabel day[] = new JLabel[42];
		
		public CalPanel() {
			super(new BorderLayout(20, 20));
			
			this.add(n = new JPanel(new BorderLayout()), "North");
			this.add(c = new JPanel(new GridLayout(0, 7)));
			
			n.add(prev = BaseFrame.lbl("<", 0, 20), "West");
			n.add(date = BaseFrame.lbl(BaseFrame.tFormat(l, "MM월 yyyy"), 0, 20));
			n.add(next = BaseFrame.lbl(">", 0, 20), "East");
			
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
			
			for (int i = 0; i < str.length; i++) {
				var jl = BaseFrame.lbl(str[i], 0, 15);
				jl.setForeground(BaseFrame.blue);
				c.add(jl);
			}
			for (int i = 0; i < day.length; i++) {
				c.add(day[i] = new JLabel("", 0));
				day[i].setBackground(Color.white);
				day[i].setOpaque(true);
			}
			
			setCal();
		}
		
		void setCal() {
			l = LocalDate.of(l.getYear(), l.getMonthValue(), 1);
			d = LocalDate.now();
			int sday = l.getDayOfWeek().getValue()%7, eday = l.lengthOfMonth();
			for (int i = 0; i < day.length; i++) {
				LocalDate tmp = l.plusDays(i-sday);
				day[i].setText("");
				day[i].setBackground(Color.white);
				if (tmp.getMonthValue() != l.getMonthValue()) {
					day[i].setVisible(false);
				} else {
					day[i].setVisible(true);
					day[i].setText(tmp.getDayOfMonth()+"");
					if (tmp.isEqual(d)) day[i].setBackground(Color.orange);

					
					day[i].addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(MouseEvent e) {
							Arrays.stream(day).forEach(a-> a.setBackground(Color.white));
							((JLabel)e.getSource()).setBackground(Color.orange);
							d = tmp;
						}
					});
					
				}
			}
		}
		
		void setMonth(int m) {
			l = l.plusMonths(m);
			date.setText(BaseFrame.tFormat(l, "MM월 yyyy"));
			setCal();
		}
	}
	
}
