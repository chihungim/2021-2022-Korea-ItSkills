package view;

import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DetailMap extends BaseFrame {
	public DetailMap(String cno, String cname) {
		super("技何瘤档", 600, 600);
		add(lbl(cname + " 技何 瘤档", JLabel.CENTER, 20));
		add(c = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				var g2 = tog2d(g);
				try {
					var img = ImageIO.read(new File("./datafiles/瘤开/" +cno + ".png"));
					g2.drawImage(img, 0, 0, getWidth(), getHeight(), this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		setVisible(true);
	}
}
