package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Inform extends BaseFrame {

	JTabbedPane tab = new JTabbedPane();
	String benefit[] = "국민연금,고용보험,산재보험,건강보험,상해보험,퇴직연금,사학연금,정기보너스,우수사원포상,장기근속자포상,인센티브제,휴가,유연근무제,시간선택제,자율근무제,차량유류비지급,통근버스 운행,야간교통비지급,기숙사운영,사택제공,아침식사 제공,점심식사 제공,저녁식사 제공,간식제공,건강검진,진료비지원,금연수당"
			.split(",");
	int xywh1[][] = { { 13, 30, 91, 30 }, { 100, 30, 107, 31 }, { 350, 30, 79, 25 }, { 450, 30, 97, 24 },
			{ 550, 20, 100, 40 }, { 14, 80, 89, 29 }, { 100, 80, 197, 32 }, { 350, 80, 85, 29 }, { 450, 80, 145, 31 },
			{ 13, 120, 91, 34 }, { 100, 120, 198, 33 }, { 350, 120, 93, 28 }, { 450, 120, 163, 29 },
			{ 13, 190, 91, 35 }, { 100, 190, 202, 35 }, { 350, 190, 90, 36 }, { 450, 190, 166, 35 },
			{ 13, 240, 91, 31 }, { 100, 240, 205, 30 }, { 350, 240, 100, 29 }, { 450, 240, 171, 31 } };
	int xywh2[][] = { { 16, 26, 87, 24 }, { 16, 61, 113, 31 }, { 142, 61, 176, 32 }, { 16, 107, 113, 30 },
			{ 143, 106, 174, 32 }, { 16, 151, 113, 28 }, { 142, 150, 175, 29 }, { 15, 192, 111, 30 },
			{ 142, 191, 175, 31 }, { 423, 21, 204, 135 } };
	JPanel chart;
	JLabel lbl1[] = new JLabel[10];
	JLabel lbl2[] = new JLabel[4];
	String cno;
	JLabel img;
	String info[] = new String[10];
	JComponent jc1[] = { lbl("조회수", JLabel.LEFT), lbl1[0] = lbl("", JLabel.LEFT), lbl("급여", JLabel.LEFT),
			lbl1[1] = lbl("", JLabel.LEFT), btn("급여 계산기", a -> {
			}), lbl("제목", JLabel.LEFT), lbl1[2] = lbl("", JLabel.LEFT), lbl("근무기간", JLabel.LEFT),
			lbl1[3] = lbl("", JLabel.LEFT), lbl("직종", JLabel.LEFT), lbl1[4] = lbl("", JLabel.LEFT),
			lbl("근무요일", JLabel.LEFT), lbl1[5] = lbl("", JLabel.LEFT), lbl("교통형태", JLabel.LEFT),
			lbl1[6] = lbl("", JLabel.LEFT), lbl("근무시간", JLabel.LEFT), lbl1[7] = lbl("", JLabel.LEFT),
			lbl("마감일", JLabel.LEFT), lbl1[8] = lbl("", JLabel.LEFT), lbl("복리후생", JLabel.LEFT),
			lbl1[9] = lbl("", JLabel.LEFT) };

	JComponent jc2[] = { lbl("채용기업 정보", JLabel.LEFT), lbl("기업이름", JLabel.LEFT), lbl2[0] = lbl("", JLabel.LEFT),
			lbl("대표자", JLabel.LEFT), lbl2[1] = lbl("", JLabel.LEFT), lbl("회사주소", JLabel.LEFT),
			lbl2[2] = lbl("", JLabel.LEFT), lbl("사업내용", JLabel.LEFT), lbl2[3] = lbl("", JLabel.LEFT),
			img = imglbl("./지급자료/이미지없음.png", 204, 135) };
	{
		img.setBorder(new LineBorder(Color.BLACK));
	}
	JPanel tab1, tab2;

	public Inform(String cno) {
		super("상세정보", 700, 500);
		this.cno = cno;

		setUI();
		setData();

		setVisible(true);
	}

	void setUI() {
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(lbl(getOne("select title from recruitment where c_no=" + cno), JLabel.CENTER, 25));
		n.add(btn("지원하기", a -> {

		}), "East");
		add(tab);

		tab.addTab("모진, 근무조건", tab1 = new JPanel(null));
		tab.addTab("기업정보", new JScrollPane(tab2 = new JPanel(null)));

		for (int i = 0; i < jc1.length; i++) {
			tab1.add(jc1[i]);
			jc1[i].setBounds(xywh1[i][0], xywh1[i][1], xywh1[i][2], xywh1[i][3]);
		}

		for (int i = 0; i < jc2.length; i++) {
			tab2.add(jc2[i]);
			jc2[i].setBounds(xywh2[i][0], xywh2[i][1], xywh2[i][2], xywh2[i][3]);
		}

		sz(tab2, 1, 800);

		((JPanel) (getContentPane())).setBorder(new EmptyBorder(5, 5, 5, 5));
	}

	void setData() {
		try {
			var rs = stmt.executeQuery(
					"SELECT r.hits, concat(r.standard, ' ', r.salary, '원'), r.title, r.period, d.name, r.week, e.name, r.time, r.deadline, r.benefit FROM recruitment r, details d, employment e where r.d_no=d.d_no and r.e_no=e.e_no and c_no="
							+ cno);
			if (rs.next()) {
				for (int i = 0; i < info.length; i++) {
					info[i] = rs.getString(i + 1);
				}
				String eno[] = info[9].split(",");
				info[9] = "";
				for (int i = 0; i < eno.length; i++) {
					eno[i] = benefit[rei(eno[i]) - 1];
				}
				info[9] = String.join(",", eno);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = stmt
					.executeQuery("select c_no, name, entrepreneur, address, detail from company where c_no=" + cno);
			while (rs.next()) {
				for (int i = 0; i < 4; i++) {
					lbl2[i].setText(rs.getString(i + 2));
				}

				System.out.println(rs.getString(2));

				if (new File("./지급자료/브랜드/" + rs.getString(2) + ".jpg").exists()) {

					img.setIcon(
							new ImageIcon(Toolkit.getDefaultToolkit().getImage("./지급자료/브랜드/" + rs.getString(2) + ".jpg")
									.getScaledInstance(200, 135, Image.SCALE_SMOOTH)));
				}
				rs.getString(2);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < info.length; i++) {
			lbl1[i].setText(info[i]);
		}

	}

	void setChart() {

	}

}
