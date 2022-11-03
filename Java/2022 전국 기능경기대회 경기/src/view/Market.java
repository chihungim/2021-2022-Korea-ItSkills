package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class Market extends BaseDialog {
	DefaultTableModel m = model("��¥,�ǸŰ�".split(","));
	JTable table = table(m);
	JTextField txt;
	JButton btn;

	public Market(String sno, String ino) {
		super("���͵��", 300, 400);
		setLayout(new BorderLayout(10, 10));
		add(c = new JPanel(new BorderLayout()));
		add(s = newJPanel(new FlowLayout(FlowLayout.RIGHT)), "South");
		c.add(sz(new JScrollPane(table), 0, 80), "North");
		c.add(cc = new JPanel(new GridLayout(0, 1)));

		var data = getrows(
				"select d_date, format(m_price,'#,##0') from market m, deal d, storage s where m.m_no = d.m_no and m.s_no = s.s_no and s.i_no = ? order by d_date desc",
				ino);

		int avg = (int) data.stream().mapToInt(a -> parsedec(a.get(1))).average().getAsDouble();
		int min = data.stream().mapToInt(a -> parsedec(a.get(1))).min().getAsInt();
		int last = parsedec(data.get(0).get(1));

		for (var d : data) {
			if (parsedec(d.get(1)) == min) {
				var l = new JLabel(d.get(1) + "", JLabel.CENTER);
				l.setForeground(Color.RED);
				d.set(1, l);
			}
		}

		JComponent jc[] = { lbl(getrows("Select i_name from item where i_no = ?", ino).get(0).get(0) + "", JLabel.LEFT),
				lbl(decformat(last), JLabel.LEFT), lbl(decformat(avg), JLabel.LEFT), txt = new JTextField(15) };
		var cap = "�����۸�,������ �ǸŰ�,��� �ǸŰ�,�ǸŰ�".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 120, 0), "West");
			temp.add(jc[i]);
			cc.add(temp);
		}

		cc.setBorder(new LineBorder(Color.BLACK));

		s.add(btn = btn("���", a -> {
			if (a.getActionCommand().equals("���")) {
				if (txt.getText().isEmpty()) {
					emsg("1���� ū ���ڷ� �Է��ϼ���.");
					return;
				}
				execute("insert market values(0,?,?,?,?)", BasePage.user.u_no, sno, txt.getText(), 0);
				imsg("����� �Ϸ�Ǿ����ϴ�.");
				BasePage.mf.updateALL();
			} else {
				if (cint(BasePage.user.u_money) < mprice) {
					emsg("�ݾ��� �����մϴ�.");
					new Charge().setVisible(true);
					return;
				}
				execute("update user set u_money = u_money + ? where u_no = ?", mprice, uno);
				execute("update user set u_money = u_money - ? where u_no = ?", mprice, BasePage.user.u_no);
				execute("insert storage values(0,?,?)", BasePage.user.u_no, sno);
				execute("insert deal values(0,?,?,?)", BasePage.user.u_no, mno, LocalDate.now().toString());
				execute("update market set m_ox = 1 where m_no = ?", mno);
				BasePage.mf.updateALL();
				new Info(ino).setVisible(true);
			}
		}));

		addrow(data, m);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	int mprice = 0;
	String mno, uno;

	public Market(String mno, String sno, String ino, String uno, int mprice) {
		this(sno, ino);
		this.mno = mno;
		this.uno = uno;
		this.mprice = mprice;
		txt.setText(decformat(mprice));
		txt.setEnabled(false);
		btn.setText("����");
	}

}
