import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class UserManage extends JPanel {
	JPanel n, c, s;
	JPanel n_w, n_e;

	JLabel title;

	JTextField txt = new JTextField(13);

	JButton btn[] = new JButton[3];

	DefaultTableModel m = BaseFrame.model("순번,아이디,비밀번호,성명,이메일,포인트,예매수".split(","));
	JTable t = BaseFrame.table(m);
	JScrollPane jsc = new JScrollPane(t);

	JPopupMenu menu = new JPopupMenu();
	JMenuItem item = new JMenuItem("예매 조회");

	String cap[] = " ,저장,삭제".split(",");
	boolean isChanged;

	public UserManage() {

		UI();
		event();
	}

	void event() {
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 3) {
					menu.show(t, e.getX(), e.getY());
					t.setRowSelectionInterval(t.rowAtPoint(e.getPoint()), t.rowAtPoint(e.getPoint()));
				}
			}
		});

		item.addActionListener(e -> {
			new UserInfo(BaseFrame.toInt(t.getValueAt(t.getSelectedRow(), 0)));
		});

	}

	void UI() {
		setLayout(new BorderLayout(10, 10));

		add(n = new JPanel(new BorderLayout()), "North");
		add(c = new JPanel(new BorderLayout()));
		add(s = new JPanel(new FlowLayout(2)), "South");

		n.add(n_w = new JPanel(new FlowLayout(0)), "West");
		n.add(n_e = new JPanel(new FlowLayout(2)), "East");

		n_w.add(title = BaseFrame.lbl("사용자 관리", 2, 25));

		BaseFrame.HintText(txt, "성명");
		n_e.add(txt);
		n_e.add(btn[0] = BaseFrame.btn("사용자 조회", e -> {
			search(txt.getText());
		}));

		c.add(jsc);

		menu.add(item);

		for (int i = 1; i < btn.length; i++) {
			s.add(btn[i] = BaseFrame.btn(cap[i], e -> {
				if (e.getActionCommand().equals("저장")) {
					for (int j = 0; j < t.getRowCount(); j++) {
						int idx = j;
						try {
							var rs = BaseFrame.stmt
									.executeQuery("select * from user where no = '" + t.getValueAt(idx, 0) + "'");
							if (rs.next()) {
								for (int k = 0; k < 6; k++) {
									if (t.getValueAt(idx, k) != rs.getString(k + 1)) {
										BaseFrame.imsg = new BaseFrame.iMsg("수정내용을 저장 완료하였습니다");
										BaseFrame.execute("update user set id = '" + t.getValueAt(idx, 1) + "', pwd = '"
												+ t.getValueAt(idx, 2) + "', name = '" + t.getValueAt(idx, 3)
												+ "', email = '" + t.getValueAt(idx, 4) + "', point = '"
												+ t.getValueAt(idx, 5) + "' where no = '" + t.getValueAt(idx, 0) + "'");
										return;
									}
								}
							}
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else {
					BaseFrame.emsg = new BaseFrame.eMsg("삭제를 완료하였습니다");
					BaseFrame.execute("delete from user where no = '" + t.getValueAt(t.getSelectedRow(), 0) + "'");
					search("");
				}
			}));
		}

		search("");

	}

	void search(String name) {
		if (name.equals("성명")) {
			return;
		}

		m.setRowCount(0);

		BaseFrame.addRow(m,
				"select u.no, u.id, u.pwd, u.name, u.email, u.point, count(user_no) from user u, reservation r where r.user_no = u.no and name like '%"
						+ name + "%' group by user_no order by u.no");
	}
}
