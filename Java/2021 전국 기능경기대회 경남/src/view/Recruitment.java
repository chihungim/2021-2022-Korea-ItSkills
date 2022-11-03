package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class Recruitment extends BaseFrame {

	JPanel local = new JPanel(new GridLayout(2, 9));
	String l[] = "서울,경기,인천,부산,대구,대전,경남,전남,충남,광주,울산,경북,전북,충북,강원,제주,세종,전국".split(",");
	JLabel lo[] = new JLabel[l.length];
	{
		for (int i = 0; i < lo.length; i++) {
			lo[i] = lbl(l[i], JLabel.CENTER, 15);
			lo[i].setOpaque(true);
			lo[i].setBackground(Color.WHITE);
			lo[i].setBorder(new LineBorder(Color.BLACK));
			lo[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					var lbl = ((JLabel) e.getSource());

					if (lbl.getBackground().equals(Color.WHITE)) {
						for (int i = 0; i < lo.length; i++) {
							lo[i].setBackground(Color.WHITE);
						}
						lbl.setBackground(Color.YELLOW);
						localtion = lbl.getText();
						if (localtion.equals("전국")) {
							localtion = "";
						}
					}

					super.mousePressed(e);
				}
			});
			local.add(lo[i]);
		}

		lo[lo.length - 1].setBackground(Color.YELLOW);
	}

	String localtion = "";
	String term = "";
	String week = "";
	String time = "";
	JTextField txt;
	JCheckBox[][] chk = {
			{ new JCheckBox("1일"), new JCheckBox("1주일이내"), new JCheckBox("1개월~3개월"), new JCheckBox("3개월~6개월"),
					new JCheckBox("6개월~1년"), new JCheckBox("1년이상") },
			{ new JCheckBox("월~금"), new JCheckBox("월~일"), new JCheckBox("주말"), new JCheckBox("요일협의") },
			{ new JCheckBox("오전"), new JCheckBox("오후"), new JCheckBox("종일"), new JCheckBox("시간협의") } };
	ButtonGroup bg[] = new ButtonGroup[3];
	JPanel s = new JPanel(new GridLayout(1, 0));

	JPanel[] sel = new JPanel[3];
	{
		String kind[] = "근무기간,근무요일,근무시간".split(",");
		for (int i = 0; i < sel.length; i++) {
			bg[i] = new ButtonGroup();
			s.add(sel[i] = new JPanel(new BorderLayout()));
			JLabel lbl = lbl(kind[i], 0, 15);
			lbl.setOpaque(true);
			lbl.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
			lbl.setBackground(Color.WHITE);
			sel[i].add(lbl, "North");
			sel[i].setBorder(new LineBorder(Color.BLACK));
			var c = new JPanel(new GridLayout(0, 2));

			sel[i].add(c);
			for (int j = 0; j < chk[i].length; j++) {
				c.add(chk[i][j]);
				bg[i].add(chk[i][j]);
				chk[i][j].setName(kind[i]);
				chk[i][j].addActionListener(a -> {
					var box = ((JCheckBox) a.getSource());

					if (box.isSelected()) {
						if (box.getName().equals("근무기간")) {
							term = box.getText();
						} else if (box.getName().equals("근무요일")) {
							week = box.getText();
						} else {
							time = box.getText();
						}
					}
				});
			}
		}
	}
	JButton btn = btn("검색하기", a -> search());

	JTextField sfield;

	JPanel sIpanel = new JPanel();

	JPanel sPanel = new JPanel(new BorderLayout());
	{

		var n = new JPanel();
		n.setLayout(new BoxLayout(n, BoxLayout.X_AXIS));
		sIpanel.setLayout(new BoxLayout(sIpanel, BoxLayout.Y_AXIS));
		int height = 50;
		String skind[] = "기업이름,제목,급여,급여종류,등록일".split(",");
		int w[] = { 150, 380, 200, 130, 130, 130 };

		for (int i = 0; i < skind.length; i++) {
			JLabel lbl = lbl(skind[i], JLabel.CENTER, 15);
			lbl.setOpaque(true);
			lbl.setBackground(Color.WHITE);
			lbl.setBorder(new LineBorder(Color.BLACK));
			lbl.setMaximumSize(new Dimension(w[i], height));
			n.add(lbl);
		}

		sPanel.add(n, "North");
		sPanel.add(sIpanel);
	}

	JComponent jc[] = { lbl("채용 정보", 0, 20), btn, lbl("지역", JLabel.CENTER), local, lbl("직종", JLabel.CENTER),
			sfield = new JTextField(), s, new JScrollPane(sPanel) };

	{
		sfield.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				new Choice(Recruitment.this, sfield);
				super.mousePressed(e);
			}
		});
	}

	int xywh[][] = { { 405, 11, 146, 39 }, { 766, 27, 90, 30 }, { 27, 88, 75, 27 }, { 141, 84, 708, 74 },
			{ 27, 171, 78, 28 }, { 142, 169, 709, 30 }, { 143, 211, 708, 139 }, { 19, 365, 950, 239 } };

	public Recruitment() {
		super("채용정보", 1000, 650);
		setUI();
		setVisible(true);
	}

	private void setUI() {
		setLayout(null);
		for (int i = 0; i < jc.length; i++) {
			add(jc[i]);
			jc[i].setBounds(xywh[i][0], xywh[i][1], xywh[i][2], xywh[i][3]);
		}
		search();

	}

	void search() {
		sIpanel.removeAll();
		try {
			int cnt = 0;

			String lq = localtion.equals("") ? "" : " and left(c.address,2) = '" + localtion + "' ";

			String pq = "";
			String wq = "";
			String tq = "";
			String dq = "";

			if (!sfield.getText().isEmpty()) {
				dq = " and d.name like '%" + sfield.getText() + "%'";
			}

			if (!term.equals(""))
				pq = term.equals("1일") ? " and r.period = '1일'"
						: term.equals("1주일 이내") ? " and r.period regexp '^[1234567]일$'"
								: term.equals("1개월~3개월") ? " and r.period regexp '^[123]개월$'"
										: term.equals("3개월~6개월") ? " and r.period regexp '^[3456]개월$'"
												: term.equals("6개월~1년")
														? " and r.period regexp '.*((6|7|8|9|10|11|12)개월|1년).*'"
														: " and r.period regexp '.*년.*'";
			if (!week.equals(""))
				wq = week.equals("월~금") ? " and r.week regexp '.*[^요일협의].*' and not (r.week regexp '.*[토|일].*'"
						: week.equals("월~일")
								? " and not (r.week regexp '.*요일협의.*') and r.week regexp '.*[월화수목금].*' and r.week regexp '.*[토일].*'"
								: week.equals("주말") ? "select * from recruitment r where r.week = '토, 일'"
										: " and r.week = '요일협의'";
			if (!time.equals(""))
				tq = time.equals("오전") ? " and hour(right(r.time, 5)) < 12"
						: time.equals("오후") ? " and hour(left(r.time, 5)) >= 12"
								: time.equals("종일") ? " and hour(left(r.time, 5)) < 12 and hour(right(r.time, 5)) >= 12"
										: " and r.time='시간협의'";

			String sql = "SELECT c.c_no, c.name, r.title, r.time, r.standard, r.date FROM recruitment r, company c, details d where r.c_no=c.c_no and c.d_no = d.d_no"
					+ tq + pq + wq + lq + dq + " order by c.c_no";

			System.out.println(sql);
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {

				cnt++;
				sIpanel.add(new SearchPanel(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6)));
			}
			if (cnt == 0) {
				eMsg("정보가 없습니다.");
			} else {
				iMsg(cnt + "건의 정보가 있습니다.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		repaint();
		revalidate();

	}

	class SearchPanel extends JPanel {

		JLabel lblKind;
		int widths[] = { 50, 100, 380, 130, 130, 130 };

		public SearchPanel(String cno, String name, String title, String time, String standard, String date) {
			String path = isPath(name);

			this.setLayout(new FlowLayout(0));
			this.setPreferredSize(new Dimension(1, 50));

			this.add(sz(new JLabel(img(path, 50, 40)), 50, 40));
			this.add(sz(new JLabel("<html><center>" + name, 0), 150, 25));

			this.add(sz(new JLabel(title), 310, 25));
			this.add(sz(new JLabel(time, 0), 130, 25));

			JLabel lbl = new JLabel(standard, 0);
			Color col = (standard.contentEquals("시급")) ? Color.green
					: (standard.contentEquals("일급")) ? Color.orange
							: (standard.contentEquals("월급")) ? Color.cyan : Color.pink;
			lbl.setBackground(col);
			lbl.setOpaque(true);
			new Thread(() -> {
				while (true) {
					lbl.setForeground(Color.white);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					lbl.setForeground(Color.black);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					repaint();
					revalidate();
				}

			}).start();
			this.add(sz(lbl, 100, 25));

			this.add(sz(new JLabel(date, 0), 130, 25));

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (LocalDate.now().isAfter(LocalDate.parse(date))) {
						eMsg("마감일이 지난 채용공고입니다.");
						return;
					} else {
						new Inform(cno);
					}
				}
			});
			this.setBorder(new CompoundBorder(new LineBorder(Color.black), new EmptyBorder(0, 0, 10, 0)));

		}

	}

	public static void main(String[] args) {
		new Recruitment();
	}
}
