import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Recommend extends JPanel {
	JPopupMenu pop = new JPopupMenu();
	JPanel in = new JPanel(new GridLayout(0, 1));
	JScrollPane sc = new JScrollPane(in);
	JButton loc[] = new JButton[16];
	JPanel c = new JPanel(new BorderLayout()), s = new JPanel(new BorderLayout()),
			cc = new JPanel(new GridLayout(1, 0)), ss = new JPanel(new FlowLayout(0));
	JPanel fl = new JPanel(new FlowLayout(0));
	JPanel jp[] = new JPanel[5];
	JButton jb;
	JPopupMenu pop2 = new JPopupMenu(), pop3 = new JPopupMenu();
	JMenuItem item[] = { new JMenuItem("이미지 설정"), new JMenuItem("설명 설정") };
	JMenuItem item1[] = { new JMenuItem("삭제"), new JMenuItem("설명 텍스트 입력") };
	int sel = 0, last, sel2;
	JLabel jl[] = new JLabel[5];
	String explan = "";
	int imgNum[] = new int[5];
	JLabel img[] = new JLabel[5];

	public Recommend() {
		setLayout(new BorderLayout());

		pop.add(sc);
		for (int i = 0; i < item.length; i++) {
			pop2.add(item[i]);
			pop3.add(item1[i]);
		}
		try {
			ResultSet rs = BaseFrame.stmt.executeQuery("select * from location");
			int i = 0;
			while (rs.next()) {
				in.add(loc[i] = new JButton(rs.getString("name")));
				i++;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		add(c);
		add(s, "South");

		c.add(BaseFrame.lbl("추천 여행지 관리", 2, 25), "North");
		c.add(cc);

		s.add(fl, "North");
		s.add(ss);

		fl.add(BaseFrame.lbl("설명 설정", 2, 25));
		fl.add(BaseFrame.btn("추가", a -> {
			JFileChooser file = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images", "jpg,png".split(","));
			file.setFileFilter(filter);
			file.setMultiSelectionEnabled(false);
			if (file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				img[last].setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(file.getSelectedFile().getPath())
						.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
				String title = ((TitledBorder) jp[sel].getBorder()).getTitle();
				File f = new File("./지급파일/images/recommend/" + BaseFrame.hashMap.get(title) + "/");
				try {
					ImageIO.write(ImageIO.read(new File(file.getSelectedFile().getPath())), "jpg",
							new File("././지급파일/images/recommend/busan/4.jpg"));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}));
		cc.setOpaque(true);
		cc.setBackground(BaseFrame.whiteColor);
		fl.setOpaque(true);
		fl.setBackground(BaseFrame.whiteColor);
		ss.setOpaque(true);
		ss.setBackground(BaseFrame.whiteColor);

		cc.setBorder(new EmptyBorder(0, 0, 50, 0));

		BaseDialog.size(ss, 0, 250);
		BaseDialog.size(pop, 120, 280);

		try {
			ResultSet rs = BaseFrame.stmt.executeQuery(
					"SELECT * FROM busticketbooking.recommend_info ri inner join recommend r on ri.recommend_no = r.no inner join location l on r.location_no = l.no group by name order by r.no ;");
			int i = 0;
			while (rs.next()) {
				cc.add(jp[i] = new JPanel(new BorderLayout()));
				TitledBorder title = new TitledBorder(new LineBorder(Color.BLACK), rs.getString("name"));
				jp[i].setBorder(title);
				jp[i].setOpaque(false);
				jp[i].add(jl[i] = new JLabel(new ImageIcon(Toolkit
						.getDefaultToolkit().getImage("지급파일/images/recommend/"
								+ BaseFrame.hashMap.get(rs.getString("name")) + "/" + rs.getString(2) + ".jpg")
						.getScaledInstance(200, 200, Image.SCALE_SMOOTH))));
				imgNum[i] = rs.getInt(2);
				int j = i;
				jp[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							pop.show(jp[j], e.getX(), e.getY());
							for (int j2 = 0; j2 < loc.length; j2++) {
								int k = j2;
								loc[j2].addActionListener(a -> {
									title.setTitle(loc[k].getText());
									jp[j].setBorder(title);
									BaseFrame.execute("update recommend set location_no = " + (k + 1) + " where no = "
											+ (sel + 1));
									repaint();
									revalidate();
								});
							}
						}
						if (e.getButton() == 3) {
							sel = j;
							pop2.show(jp[j], e.getX(), e.getY());
						}
					}
				});
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < item.length; i++) {
			item[i].addActionListener(a -> {
				if (a.getSource().equals(item[0])) {
					JFileChooser file = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & png Images",
							"jpg,png".split(","));
					file.setFileFilter(filter);
					file.setMultiSelectionEnabled(false);
					if (file.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
						jl[sel].setIcon(
								new ImageIcon(Toolkit.getDefaultToolkit().getImage(file.getSelectedFile().getPath())
										.getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
						String ad = ((TitledBorder) jp[sel].getBorder()).getTitle();
						if (ad.equals(explan)) {
							img[imgNum[sel] - 1].setIcon(
									new ImageIcon(Toolkit.getDefaultToolkit().getImage(file.getSelectedFile().getPath())
											.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
							File f = new File("./지급파일/images/recommend/" + BaseFrame.hashMap.get(ad) + "/" + imgNum[sel]
									+ ".jpg");
							try {
								ImageIO.write(ImageIO.read(new File(file.getSelectedFile().getPath())), "jpg", f);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				if (a.getSource().equals(item[1])) {
					explan = ((TitledBorder) jp[sel].getBorder()).getTitle();
					try {
						ResultSet rs = BaseFrame.stmt.executeQuery(
								"SELECT * FROM busticketbooking.recommend_info ri inner join recommend r on ri.recommend_no = r.no inner join location l on r.location_no = l.no where name = '"
										+ ((TitledBorder) jp[sel].getBorder()).getTitle() + "' order by r.no");
//						rs.last();
//						img = new JLabel[rs.getRow()];
//						rs.beforeFirst();
						int j = 0;
						while (rs.next()) {
							img[j].setIcon(new ImageIcon(Toolkit
									.getDefaultToolkit().getImage("지급파일/images/recommend/"
											+ BaseFrame.hashMap.get(rs.getString("name")) + "/" + (j + 1) + ".jpg")
									.getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
							img[j].setBorder(new LineBorder(Color.BLACK));
							repaint();
							revalidate();
							j++;
						}
						last = j;
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
		}
		for (int i = 0; i < item1.length; i++) {
			item1[i].addActionListener(a -> {
				if (a.getSource().equals(item1[0])) {
					String title = ((TitledBorder) jp[sel].getBorder()).getTitle();
					File f = new File(
							"./지급파일/images/recommend/" + BaseFrame.hashMap.get(title) + "/" + (sel2 + 1) + ".jpg");
					f.delete();
					img[sel2].setBorder(new LineBorder(Color.WHITE));
					img[sel2].setIcon(null);
				}
				if (a.getSource().equals(item1[1])) {
					String inp = JOptionPane.showInputDialog(null, "설명 텍스트를 입력해주세요.", "입력",
							JOptionPane.QUESTION_MESSAGE);
					BaseFrame.execute("update recommend_info set descrption = '" + inp + "' where recommend_no = "
							+ (sel + 1) + " and title = " + (sel2 + 1));
				}
			});
		}
		for (int i = 0; i < img.length; i++) {
			ss.add(img[i] = BaseDialog.size(new JLabel(), 150, 150));
			int j = i;
			img[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						pop3.show(img[j], e.getX(), e.getY());
						sel2 = j;
					}
				}
			});
		}

	}

	public static void main(String[] args) {
		new AdminMain();
	}
}
