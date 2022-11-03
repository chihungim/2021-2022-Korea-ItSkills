package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Route extends BaseFrame {
	
	JComboBox<String> combo = new JComboBox<String>();
	RoutePanel rp = new RoutePanel();
	
	ArrayList<Integer> x = new ArrayList<Integer>(), y = new ArrayList<Integer>(), px = new ArrayList<Integer>(), py = new ArrayList<Integer>();
	
	boolean enable = false;
	
	public Route() {
		super("노선도", 950, 750);
		this.setLayout(new BorderLayout(5, 5));
		
		for (int i = 1; i < metroNames.size(); i++) {
			combo.addItem(metroNames.get(i));
		}
		
		this.add(combo, "North");
		this.add(c = new JPanel(new BorderLayout()));
		c.add(new JScrollPane(rp = new RoutePanel()));
		
		combo.addItemListener(a->{
			rp.removeAll();
			draw();
		});
		
		draw();
		setEmpty((JPanel)getContentPane(), 10, 10, 10, 10);
		this.setVisible(true);
	}
	
	void draw() {
		int lineIdx = combo.getSelectedIndex()+1;
		var lineSt = (ArrayList) metroStInfo[lineIdx][0];
		
		int sx = 130, sy = 100, x1=sx, x2=x1, y1=sy, y2=y1;
		
		x.clear(); y.clear(); px.clear(); py.clear();
		x.add(x1); y.add(y1); px.add(x1); py.add(y1);
		for (int i = 1; i < lineSt.size(); i++) {
			if ((i-1) / 6 % 2 == 0) x2 = x1 + 100;
			else x2 = x1 - 100;
			
			x.add(x2); y.add(y2);
			px.add(x2); py.add(y2);
			
			if (i % 6 == 0) {
				y2 = y1 + 100;
				px.add(x2); py.add(y2);
			}
			
			JLabel jl = new JLabel(lineSt.get(i)+"", 0);
			rp.add(jl);
			jl.setBounds((x1+x2)/2-50, y1+10, 100, 20);
			jl.addMouseListener(new RoutePanel(lineIdx, lineSt, i));
			
			if (i != lineSt.size()-1) {
				int cost = adjDim[stNames.indexOf(lineSt.get(i))][stNames.indexOf(lineSt.get(i+1))]/5;
				JLabel jl2 = new JLabel((cost/10.0)+"km ("+(cost*5)+"초)", 0);
				rp.add(jl2);
				
				if (i%6 != 0) {
					jl2.setBounds(x2-50, y1-20, 100, 10);
				} else if (i/6 % 2 == 1) {
					jl2.setBounds(x2, (y1+y2)/2, 100, 10);
				} else jl2.setBounds(x2-100, (y1+y2)/2, 100, 10);
			}
			x1=x2; y1=y2;
			
		}
		
		sz(rp, getWidth()-100, 100+(lineSt.size()+5)/6*100);
		
		enable = true;
		repaint();
		revalidate();
		
	}
	
	class RoutePanel extends JPanel implements MouseListener {
		int lineIdx, innerIdx;
		ArrayList lineSt;
		
		public RoutePanel() {
			this.setLayout(null);
		}
		
		public RoutePanel(int lineIdx, ArrayList lineSt, int innerIdx) {
			this.lineIdx = lineIdx;
			this.lineSt = lineSt;
			this.innerIdx = innerIdx;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (enable == false) return;
			
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.black);
			g2.setFont(new Font("맑은 고딕", Font.BOLD, 20));
			g2.drawString("노선도 - "+combo.getSelectedItem()+"", 130, 40);

			int px2[] = new int[px.size()], py2[] = new int[py.size()];
			for (int i = 0; i < py2.length; i++) {
				px2[i] = px.get(i).intValue();
				py2[i] = py.get(i).intValue();
			}
			
			g2.setColor(blue);
			g2.drawPolyline(px2, py2, px2.length);
			
			g2.setColor(new Color(255, 50, 100));
			for (int i = 0; i < x.size()-1; i++) {
				g2.fillOval((x.get(i)+x.get(i+1))/2, y.get(i+1)-5, 10, 10);
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			String str = "<html>";
			str += lineSt.get(innerIdx)+"에서 갈 수 있는 역<br>";
			str += innerIdx==1 ? lineSt.get(innerIdx+1) : (innerIdx == lineSt.size()-1 ? lineSt.get(innerIdx-1) : lineSt.get(innerIdx-1) +","+lineSt.get(innerIdx+1));
			str += "<br>"+lineSt.get(innerIdx)+"를 지나가는 노선<br>";
			for (int i = 1; i < metroNames.size(); i++) {
				if (((ArrayList)metroStInfo[i][0]).contains(lineSt.get(innerIdx))) {
					str += metroNames.get(i)+"<br>";
				}
			}
			
			iMsg(str);
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
	
}
