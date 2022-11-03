package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Resume extends BaseFrame {
	JComboBox<String> combo = new JComboBox<String>();
	JScrollPane scr;
	JPanel list;
	ItemPanel item;

	public Resume() {
		super("인재정보", 800, 550);

		this.getContentPane().setBackground(Color.white);

		this.add(n = new JPanel(new BorderLayout()), "North");
		this.add(scr = new JScrollPane(list = new JPanel(new FlowLayout(0, 0, 0))));

		var n_w = new JPanel(new FlowLayout(0));
		n_w.add(sz(new JLabel("직종"), 40, 25));
		n_w.add(sz(combo, 120, 25));
		n.add(n_w, "West");

		combo.addItemListener(a -> search());

		JButton btn;
		n.add(btn = sz(btn("알바제의신청", a -> {
			if (item == null) {
				eMsg("인재를 선택해주세요.");
				return;
			}
			// 메일쓰기 탭으로 이동하고 받는사람 입력란에 선택한 인재의 이메일을 자동으로 나타내시오.
		}), 200, 25), "East");
		btn.setVisible(mno != 0);

		n.setOpaque(false);
		n_w.setOpaque(false);
		scr.setOpaque(false);
		list.setBackground(Color.white);

		combo.addItem("전체");
		try {
			var rs = stmt.executeQuery("select name from details");
			while (rs.next()) {
				combo.addItem(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		search();

		setEmpty(n, 10, 0, 10, 0);
		setEmpty((JPanel) getContentPane(), 10, 10, 10, 10);

		this.setVisible(true);
	}

	void search() {
		list.removeAll();
		String sql = "SELECT i.title, u.u_no, u.name, u.email, i.level, d.name, i.period FROM information i, user u, details d, employment e where i.u_no=u.u_no and d.d_no=i.d_no and i.e_no=e.e_no and i.anonymous = 1";
		if (combo.getSelectedIndex() != 0) {
			sql += " and d.d_no ='" + (combo.getSelectedIndex() + 1) + "'";
		}
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				list.add(new ItemPanel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getString(7)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.setPreferredSize(new Dimension(10, 150 * list.getComponentCount()));
		repaint();
		revalidate();
	}

	class ItemPanel extends JPanel {

		String email;
		JLabel lbl, img;

		public ItemPanel(String title, String uno, String uname, String email, String level, String dname,
				String period) {
			this.email = email;

			this.setLayout(new BorderLayout());
			this.setBackground(Color.white);
			this.setPreferredSize(new Dimension(745, 150));
			this.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));

			String path = "";
			if (!Files.exists(Paths.get("./지급자료/이력서/" + uno + ".jpg"))) {
				path = "./지급자료/이미지없음.png";
			} else
				path = "./지급자료/이력서/" + uno + ".jpg";
			this.add(img = new JLabel(img(path, 150, 150)), "West");

			JPanel c = new JPanel(new GridLayout(0, 1));
			this.add(c);

			JPanel c_c[] = new JPanel[4];
			for (int i = 0; i < c_c.length; i++) {
				c.add(c_c[i] = new JPanel(new FlowLayout(0)));
				c_c[i].setOpaque(false);
			}
			c_c[0].add(new JLabel("제목 : " + title));
			c_c[1].add(new JLabel("이름 : " + uname));
			c_c[2].add(sz(new JLabel("이메일 : " + email), 220, 25));
			c_c[2].add(sz(new JLabel("최종학력 : " + level), 250, 25));
			c_c[3].add(sz(new JLabel("직종 : " + dname), 220, 25));
			c_c[3].add(sz(new JLabel("근무기간 : " + period), 250, 25));

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (var p : list.getComponents()) {
						var item = (ItemPanel) p;
						item.setBackground(Color.white);
					}
					item = ((ItemPanel) e.getSource());
					((ItemPanel) e.getSource()).setBackground(Color.pink);
				}
			});

			c.setOpaque(false);
			setLine(img);
			setEmpty(c, 5, 10, 5, 0);

		}

	}

	public static void main(String[] args) {
		new Resume();
	}
}
