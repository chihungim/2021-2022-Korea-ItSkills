package view;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.SQLException;

import javax.swing.JPanel;

public class TOP5 extends BaseFrame {

	Color col[] = { Color.black, Color.blue, Color.red, Color.green, Color.yellow };

	public TOP5() {
		super("놀이기구 인기순위 TOP5", 850, 450);
		ui();
		this.setVisible(true);
	}

	void ui() {
		var c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				try {
					int i = 0, max = 0, h = 50;
					double p = 250;
					var rs = stmt.executeQuery(
							"SELECT r.r_name, count(r.r_no) from ticket t, ride r where t.r_no=r.r_no group by r.r_no order by count(r.r_no) desc limit 5");
					while (rs.next()) {
						if (i == 0)
							max = rs.getInt(2);

						g2.setColor(col[i]);
						g2.fillRect(80 + (100 * i), 280 - (int) (p * rs.getInt(2) / max), 40,
								(int) (p * rs.getInt(2) / max));
						g2.fillRect(600, 150 + (25 * i), 10, 10);
						FontMetrics m = g2.getFontMetrics(g2.getFont());
						var width = m.stringWidth(rs.getString(1));

						g2.setColor(Color.black);
						g2.drawRect(80 + (100 * i), 280 - (int) (p * rs.getInt(2) / max), 40,
								(int) (p * rs.getInt(2) / max));

						g2.drawString(rs.getString(1), 100 + (100 * i) - width / 2, 300);
						g2.drawString(rs.getString(1) + " : " + rs.getString(2) + "개", 620, 160 + (25 * i));

						i++;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		this.add(lbl("놀이기구 인기순위 TOP5", 0, 25), "North");
		this.add(c);
		c.setOpaque(false);
	}

	public static void main(String[] args) {
		new TOP5();
	}

}
