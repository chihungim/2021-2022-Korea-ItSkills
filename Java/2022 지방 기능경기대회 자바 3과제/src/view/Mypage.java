package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Mypage extends BaseFrame {

	DefaultTableModel m = model("번호,기업명,모집정보,시급,모집정원,최종학력,성별,합격여부,ano".split(","));
	JTable t = table(m);

	public Mypage() {
		super("마이페이지", 800, 400);
		add(hylbl("Mypage", 0, 30), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		c.add(cn = new JPanel(new GridLayout(0, 1)), "North");
		cn.add(hylbl("성명:" + uname, JLabel.LEFT, 15));
		cn.add(hylbl("성별:" + gender[toInt(getOne("select u_gender from user where u_no = ?", uno))], JLabel.LEFT, 13));
		cn.add(hylbl("최종학력:" + graduate[toInt(getOne("select u_graduate from user where u_no = ?", uno))], JLabel.LEFT,
				13));

		c.add(new JScrollPane(t));

		s.add(btn("PDF인쇄", a -> {
			try {
				t.print();
			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}));

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var r = t.getSelectedRow();
				if (r == -1)
					return;

				if (e.getButton() == 3) {
					var pop = new JPopupMenu();
					var item = new JMenuItem("삭제");
					item.addActionListener(a -> {
						setValues("delete from applicant where a_no = ?", t.getValueAt(r, 8));
						load();
						imsg("삭제가 완료되었습니다.");
					});
					pop.add(item);

					if (t.getValueAt(r, 7).toString().equals("불합격")) {
						pop.show(t, e.getX(), e.getY());
					}
				}
			}
		});

		int idx[] = { 0, 1, 3, 4, 5, 6, 7, 8 };
		int w[] = { 50, 80, 100, 60, 100, 60, 60, 0 };

		for (int i = 0; i < idx.length; i++) {
			t.getColumnModel().getColumn(idx[i]).setMinWidth(w[i]);
			t.getColumnModel().getColumn(idx[i]).setMaxWidth(w[i]);
		}
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		load();
		setVisible(true);
	}

	void load() {
		var rs = getValues(
				"select row_number() over(), c_name, e_title, format(e_pay, '#,##0'), e_people, e_graduate, e_gender, a_apply, a_no from applicant a, company c, employment e where a.e_no = e.e_no and c.c_no = e.c_no and a.u_no = ?",
				uno);

		for (var r : rs) {
			r.set(5, graduate[toInt(r.get(5))]);
			r.set(6, gender[toInt(r.get(6))]);
			r.set(7, apply[toInt(r.get(7))]);
		}

		addRow(m, rs);
	}

	public static void main(String[] args) {
		uno = "1";
		new Mypage();
	}
}
