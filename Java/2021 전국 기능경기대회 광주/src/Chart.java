

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Chart extends Basedialog {
	JLabel chart;
	
	public Chart() {
		super("≈Î∞Ë", 450, 400);
		
		add(labelB("≈Î∞Ë", 2, 25),"North");
		
		drawChart();
		((JPanel)getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		
		setVisible(true);
	}

	private void drawChart() {
		
		chart = new JLabel() {
			int size, vgap = 20, height = 30, y = 0;
			double max = 0;
			String name;
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2 = (Graphics2D)g;
				
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				try {
					ResultSet rs = stmt.executeQuery("select m.name, sum(rd.count) from receipt r , receipt_detail rd, menu m where r.no = rd.receipt and m.no = rd.menu and r.seller = "+NO+" group by m.name order by sum(rd.count) desc, r.no asc limit 5");
					int i = 0;
					while(rs.next()) {
						if(i==0)max = rs.getInt(2);
						size = (int)((rs.getInt(2) / max)*100) * 4;
						name = rs.getString(1) +"("+rs.getInt(2)+"∞≥ ∆«∏≈µ )";
						
						g2.setFont(new Font("∏º¿∫ ∞ÌµÒ", Font.BOLD, 15));
						FontMetrics f = g2.getFontMetrics();
						int h = f.getHeight();
						
						g2.setColor(Color.BLACK);
						
						y += height + vgap;
						
						g2.drawString(name, 10, y - 5);
						g2.setColor(Color.BLUE);
						
						GradientPaint gp = new GradientPaint(10, y + h, Color.WHITE, size, y, Color.BLUE);
						g2.setPaint(gp);
						
						g2.fillRect(10, y, size, height);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		
		add(chart);
	}
	
	public static void main(String[] args) {
		NO = 1;
		new Chart();
	}

}
