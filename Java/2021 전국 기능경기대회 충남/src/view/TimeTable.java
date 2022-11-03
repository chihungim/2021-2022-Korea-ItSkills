package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class TimeTable extends BaseFrame {
	
	JScrollPane scr;
	JComboBox<String> mcombo = new JComboBox<String>(), tcombo = new JComboBox<String>();
	JLabel info;
	int totTime, lineIdx;
	ArrayList arr;
	
	public TimeTable() {
		super("열차 시간표", 1100, 750);
		
		this.add(w = new JPanel(new FlowLayout(0)), "West");
		this.add(scr = new JScrollPane(c = new JPanel(new FlowLayout(0))));
		this.add(e = new JPanel(new BorderLayout()), "East");
		
		scr.setBorder(BorderFactory.createEmptyBorder());
		setWest();
		setCenter();
		sz(w, 280, 150);
		sz(e, 400, 1);
		setEmpty(c, 10, 10, 10, 10);
		
		this.setVisible(true);
	}
	
	void setCenter() {
		
		if (arr == null) return;
		
		c.removeAll();
		e.removeAll();
		int height = 0;
		int second = 0;
		
		LocalTime start = LocalTime.parse(tcombo.getSelectedItem()+"");
		for (int i = 1; i < arr.size(); i++) {
//			System.out.println(arr.get(i));
			second += adjDim[stNames.indexOf(arr.get(i-1))][stNames.indexOf(arr.get(i))];
			ItemPanel p = new ItemPanel(i, stNames.indexOf(arr.get(i)), arr.get(i)+"", LocalTime.parse(tcombo.getSelectedItem()+"").plusSeconds(second));
			c.add(sz(p, 350, 50));
			p.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent ev) {
					Arrays.stream(c.getComponents()).forEach(a->{
						((ItemPanel)a).onClicked(false);
					});
					ItemPanel p =((ItemPanel)ev.getSource());
					p.onClicked(true);
					
					e.add(lbl("<html><b>"+p.name+"에서<br>환승 가능한 노선", 2, 20), "North");
					var ec = new JPanel(new FlowLayout(0));
					e.add(ec);
					
					for (int j = 1; j <= 30; j++) {
						if (((ArrayList)metroStInfo[j][0]).contains(p.name)) {
 							if (!metroNames.get(j).equals(mcombo.getSelectedItem().toString())) {
 								System.out.println(metroNames.get(j));
 								ItemPanel mt = new ItemPanel();
 								mt.add(lbl(metroNames.get(j), 2, 13), "West");
 								ec.add(sz(mt, 380, 50));
 							}
						}
					}
					
					repaint();
					revalidate();
					
				}
			});
			height += 55;
		}
		
		sz(c, 350, height);
	
		repaint();
		revalidate();
		
	}
	
	class ItemPanel extends JPanel {
		boolean chk;
		int serial;
		String name;
		
		public ItemPanel() {
			this.setLayout(new BorderLayout());
			setEmpty(this, 0, 5, 0, 5);
		}
		
		public ItemPanel(int idx,int serial, String name, LocalTime t) {
			this.serial =serial;
			this.name = name;
			this.setLayout(new BorderLayout());
			setEmpty(this, 0, 5, 0, 5);
			
			this.add(lbl(idx+". "+name, 0, 13), "West");
			this.add(lbl(tFormat(t, "HH:mm:ss")+"도착", 0, 13), "East");
		}
		
		void onClicked(boolean chk) {
			this.chk = chk;
			repaint();
			revalidate();
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(chk ? Color.orange : Color.white);
			RoundRectangle2D rec = new RoundRectangle2D.Float(1.5f, 1.5f, getWidth()-3, getHeight()-3, 15, 15);
			g2.fill(rec);
		}
	}
	
	void setWest() {
		w.add(sz(mcombo, 270, 25));
		w.add(sz(tcombo, 270, 25));
		w.add(info = sz(lbl("<html><b>1호선 - 경원선 (소요산역 - 청량리역)</b><br>소요 시간 : 00:35:45", 2, 12), 280, 35));
		
		for (int i = 1; i < metroNames.size(); i++) {
			mcombo.addItem(metroNames.get(i));
		}
		
		mcombo.addItemListener(a->{
			totTime = 0;
			lineIdx = mcombo.getSelectedIndex()+1;
			
			tcombo.removeAllItems();
			for (int j = 0; j < 300; j++) {
				if (metroTime[lineIdx][1][j] == 0) break;
				int time = metroTime[lineIdx][1][j];
				tcombo.addItem(tFormat(LocalTime.ofSecondOfDay(time), "HH:mm:ss"));
			}
			
			arr = ((ArrayList)metroStInfo[lineIdx][0]);
//			for (int i = 1; i < arr.size()-1; i++) {
//				totTime += adjDim[stNames.indexOf(arr.get(i))][stNames.indexOf(arr.get(i+1))];
//			}
			info.setText("<html><b>"+mcombo.getSelectedItem()+"</b><br>소요 시간 : "+tFormat(LocalTime.ofSecondOfDay(metroTime[lineIdx][arr.size()-1][0]-metroTime[lineIdx][1][0]), "HH:mm:ss"));
			
			setCenter();
		});
		
		tcombo.addActionListener(a->{
			if (tcombo.getSelectedIndex()==-1) return;
			setCenter();
		});
		
		mcombo.setSelectedIndex(1);
		mcombo.setSelectedIndex(0);
		
	}
	
	public static void main(String[] args) {
		 new TimeTable();
	}
	
}
