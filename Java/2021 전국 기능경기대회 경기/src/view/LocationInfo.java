package view;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;

import view.BaseFrame.IconLabel;

public class LocationInfo extends BaseFrame {

	JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);
	String floor[] = "1F,2F,3F,4F,외부".split(",");
	String ch[] = "로리,로티".split(",");
	JLabel chImg;
	JLabel img[] = new JLabel[floor.length];
	JComboBox<String> box = new JComboBox<>();
	HashMap<String, ArrayList<String>> ride = new HashMap<>();
	Random r = new Random();
	int point[][][];
	IconLabel iconlbl[][];
	ArrayList<String> pos = new ArrayList<>();
	Timer blink;
	IconLabel prev;

	public LocationInfo() {
		super("위치정보", 1250, 650);
		data();
		ui();
		events();
		setVisible(true);
	}

	void ui() {
		var e = new JPanel(new BorderLayout());
		add(tab);
		add(sz(e, 250, 20), "East");

		e.add(chImg = new JLabel(getIcon("datafiles/캐릭터/" + ch[r.nextInt(2)] + (r.nextInt(3) + 1) + ".jpg", 200, 200)),
				"South");
		e.add(box, "North");
		for (int i = 0; i < floor.length; i++) {
			final int j = i;
			img[i] = new JLabel() {
				@Override
				public void paint(Graphics g) {
					super.paint(g);
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
					Image img = new ImageIcon(
							Toolkit.getDefaultToolkit().getImage("./datafiles/지도/" + floor[j] + ".jpg")).getImage();
					g2.drawImage(img, 0, 0, 950, 600, this);
				}
			};

			img[i].setOpaque(true);
			img[i].setLayout(null);
			tab.add(floor[i], img[i]);
			for (int k = 0; k < iconlbl[i].length; k++) {
				img[i].add(iconlbl[i][k]);
			}
		}
	}

	void data() {
		point = new int[floor.length][][];
		iconlbl = new IconLabel[floor.length][];

		for (int i = 0; i < floor.length; i++) {
			ride.put(floor[i], new ArrayList<String>());
		}

		try {
			var rs = stmt.executeQuery("select * from ride");
			while (rs.next()) {
				ride.get(rs.getString(3)).add(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < floor.length; i++) {
			point[i] = new int[ride.get(floor[i]).size()][2];
			try {
				var rs = stmt.executeQuery("select r_explation from ride where r_floor = '" + floor[i] + "'");
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
				boolean enb = (uheight >= toInt(
						getOne("select r_height from ride where r_name ='" + ride.get(floor[i]).get(j) + "'"))
						&& uold >= toInt(
								getOne("select r_old from ride where r_name ='" + ride.get(floor[i]).get(j) + "'"))
						&& !(toInt(getOne(
								"select r_disable from ride where r_name ='" + ride.get(floor[i]).get(j) + "'")) == 1
								&& udisable == 1));
				iconlbl[i][j] = new IconLabel(ride.get(floor[i]).get(j), enb);
				System.out.println(ride.get(floor[i]).get(j));
				iconlbl[i][j].setBounds(point[i][j][0], point[i][j][1], 30, 30);
			}
		}

	}

	void events() {
		for (var v : ride.get(floor[tab.getSelectedIndex()])) {
			box.addItem(v);
		}
		box.setSelectedIndex(-1);
		tab.addChangeListener(a -> {
			box.removeAllItems();
			for (var v : ride.get(floor[tab.getSelectedIndex()])) {
				box.addItem(v);
			}

			box.setSelectedIndex(-1);

			try {
				blink.stop();
				prev.setVisible(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			chImg.setIcon(getIcon("datafiles/캐릭터/" + ch[r.nextInt(2)] + (r.nextInt(3) + 1) + ".jpg", 200, 200));
		});

		box.addItemListener(i -> {
			if (box.getSelectedIndex() == -1)
				return;
			final int row = tab.getSelectedIndex();
			final int col = box.getSelectedIndex();

			try {
				blink.stop();
				prev.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			prev = iconlbl[row][col];

			blink = new Timer(1000, a -> {
				iconlbl[row][col].setVisible(iconlbl[row][col].isVisible() ? false : true);
			});

			blink.start();
		});
	}

	public static void main(String[] args) {
		new LocationInfo();
	}

}
