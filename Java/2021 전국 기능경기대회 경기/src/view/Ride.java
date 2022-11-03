package view;

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import view.BaseFrame.Before;
import view.BaseFrame.IconLabel;

public class Ride extends BaseFrame {
	String floor[] = "1F,2F,3F,4F,외부".split(",");
	JComboBox<String> combo = new JComboBox<String>();
	JLabel lbl_img[] = new JLabel[5];
	CardLayout card = new CardLayout();
	HashMap<String, ArrayList<String>> ride = new HashMap<String, ArrayList<String>>();
	int point[][][];
	IconLabel iconlbl[][];
	JPanel c = new JPanel(card);

	public Ride() {
		super("놀이기구 등록/수정", 1250, 650);
		data();
		ui();
		events();
		setVisible(true);
	}

	void ui() {
		var e = new JPanel();

		add(c);
		add(e, "East");

		e.add(combo, "North");

		for (int i = 0; i < floor.length; i++) {
			int j = i;
			lbl_img[i] = new JLabel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					Graphics2D g2 = (Graphics2D) g;
					Image img = new ImageIcon(
							Toolkit.getDefaultToolkit().getImage("./datafiles/지도/" + floor[j] + ".jpg")).getImage();

					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
					g.drawImage(img, 0, 0, 950, 600, null);
				}

			};
			c.add(floor[i], lbl_img[i]);
			lbl_img[i].setOpaque(true);
			lbl_img[i].setName(floor[i]);
			lbl_img[i].setBackground(Color.white);
			lbl_img[i].setLayout(null);
			for (int k = 0; k < point[i].length; k++) {
				lbl_img[i].add(iconlbl[i][k]);
			}
		}

		sz(combo, 180, 25);
		c.setOpaque(false);
		e.setOpaque(false);
	}

	void clear() {
		data();

		for (var img : lbl_img) {
			img.removeAll();
		}

		for (int i = 0; i < lbl_img.length; i++) {
			for (int j = 0; j < iconlbl[i].length; j++) {
				lbl_img[i].add(iconlbl[i][j]);
			}
		}

		revalidate();
		repaint();
	}

	void events() {
		combo.addItemListener(a -> {
			card.show(c, combo.getSelectedItem().toString());
		});

		for (int i = 0; i < floor.length; i++) {
			lbl_img[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var lbl = (JLabel) e.getSource();
					var floor = lbl.getName();
					var rae = new Ride_apply_edit(Ride_apply_edit.APPLY, "", floor, Ride.this);
					rae.x = e.getX();
					rae.y = e.getY();
					rae.addWindowListener(new Before(Ride.this));
					super.mousePressed(e);
				}
			});
		}

	}

	void data() {
		combo.setModel(new DefaultComboBoxModel<>(floor));
		point = new int[floor.length][][];
		iconlbl = new IconLabel[point.length][];

		for (int i = 0; i < floor.length; i++) {
			ride.put(floor[i], new ArrayList<String>());
		}

		try {
			var rs = stmt.executeQuery("select * from ride");
			while (rs.next()) {
				ride.get(rs.getString(3)).add(rs.getString(2));
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < floor.length; i++) {
			point[i] = new int[ride.get(floor[i]).size()][2];
			try {
				var rs = stmt.executeQuery("SELECT r_explation from ride where r_floor = '" + floor[i] + "'");
				int idx = 0;
				while (rs.next()) {
					point[i][idx][0] = toInt(rs.getString(1).split("#")[1]);
					point[i][idx][1] = toInt(rs.getString(1).split("#")[2]);
					idx++;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < point.length; i++) {
			iconlbl[i] = new IconLabel[point[i].length];
			for (int j = 0; j < point[i].length; j++) {
				iconlbl[i][j] = new IconLabel(ride.get(floor[i]).get(j), floor[i]);
				iconlbl[i][j].setBounds(point[i][j][0], point[i][j][1], 50, 50);
			}
		}
	}

	public static void main(String[] args) {
		new Ride();
	}
}
