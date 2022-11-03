package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.MatteBorder;

public class Station extends BaseFrame {
	
	JComboBox<String> combo = new JComboBox<String>();
	JLabel next, prev, cur;
	JPanel nc, cc, ccc, cn;
	JScrollPane scr;
	int mnum, inner, hei;
	String st, selst;
	
	int sno;
	
	public Station() {
		super("역 정보", 600, 700);
		this.sno = rei(BaseFrame.sno);
		selst = st = stNames.get(sno);
		System.err.println(st);
		
		this.add(n = new JPanel(new FlowLayout(1)), "North");
		this.add(c = new JPanel(new BorderLayout(10, 10)));
		
		c.add(cn = new JPanel(new BorderLayout()), "North");
		c.add(scr = new JScrollPane(cc = new JPanel(new BorderLayout())));
		cc.add(ccc = new JPanel(new FlowLayout()));
		
		n.add(prev = lbl("", 0, 25));
		n.add(cur = lbl("", 0, 25));
		n.add(next = lbl("", 0, 25));
		
		cur.setForeground(blue);
		
		cn.add(combo);
		cc.add(lbl("열차 시간표", 0, 20), "North");
		
		prev.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (prev.getText().contentEquals("기점")) {
					eMsg("해당 노선의 마지막 위치입니다.");
					return;
				}
				change(-1);
			}
		});
		
		next.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (next.getText().contentEquals("종점")) {
					eMsg("해당 노선의 마지막 위치입니다.");
					return;
				}
				change(1);
			}
		});
		
		combo.addItemListener(a->cevnt());
		
		setCombo();
		cevnt();
		
		change(0);
		
		this.setVisible(true);
	}
	
	void cevnt() {
		mnum = metroNames.indexOf(combo.getSelectedItem().toString());
		st = selst;
		inner = ((ArrayList)metroStInfo[mnum][0]).indexOf(st);
		change(0);
	}
	
	void setCombo() {
		combo.removeAllItems();
		
		for (int i = 1; i < metroStInfo.length; i++) {
			if (((ArrayList)metroStInfo[i][0]).contains(st)) {
				combo.addItem(metroNames.get(i));
			}
		}
	}
	
	class TimeLine extends JPanel {
		JLabel tl;
		public TimeLine(String time, int hour) {
			super(new BorderLayout());
			
			this.add(sz(lbl(hour+"시", 2, 18), 50, 50), "West");
			this.add(sz(tl = lbl("<html>"+time, 0), 470, time.split("<br>").length*18));
			
			hei += time.split("<br>").length*18;
			
			tl.setVerticalAlignment(JLabel.TOP);
			tl.setBorder(new MatteBorder(0,1,0,0,blue));
		}
	}
	
	void change(int idx) {
		inner += idx;
		
		cur.setText("←"+((ArrayList)metroStInfo[mnum][0]).get(inner)+"→");
		selst = ((ArrayList)metroStInfo[mnum][0]).get(inner).toString();
		
		if (inner-1==0) {
			prev.setText("기점");
		} else prev.setText((((ArrayList)metroStInfo[mnum][0]).get(inner-1).toString()));
		
		if (inner+1==((ArrayList)metroStInfo[mnum][0]).size()) {
			next.setText("종점");
		} else next.setText((((ArrayList)metroStInfo[mnum][0]).get(inner+1).toString()));
		
		update();
	}
	
	void update() {
		int hour0=0, hour1=0;
		String timeStr = "";
		
		ccc.removeAll();
		for (int i = 0; i < 300; i++) {
			int sec = metroTime[mnum][inner][i];
			if (sec==0) break;
			
			hour1=sec/3600;
			if ((hour0!=0 && hour0!=hour1) || hour1==0) {
				var tmp = new JPanel(new FlowLayout());
				tmp.add(new TimeLine(timeStr, hour0));
				ccc.add(tmp);
				sz(ccc, ccc.getWidth(), hei);
				
				timeStr = String.format("%02d:%02d:%02d", sec/3600, (sec%3600)/60, sec%60) + "<br>";
			} else {
				timeStr += String.format("%02d:%02d:%02d", sec/3600, (sec%3600)/60, sec%60) + "<br>";
			}
			hour0=hour1;
		}
		
		ccc.repaint();
		ccc.revalidate();
	}
	
}
