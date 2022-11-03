package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Mail extends BaseFrame {
	JTabbedPane tab = new JTabbedPane();
	JPanel p[] = new JPanel[3];
	String cap[] = "받은메일함,보낸메일함,메일쓰기".split(",");
	boolean stateRead;
	DefaultTableModel m = new DefaultTableModel(null, ",제목,보낸사람,날짜,mano,detail,read".split(",")) {
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		}

	};
	JTable t = new JTable(m) {
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		}

		public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
			JComponent jc = (JComponent) super.prepareRenderer(renderer, row, column);

			return jc;
		};
	};
	JCheckBox chk = new JCheckBox("전체");
	JButton btn[] = new JButton[2];
	JPanel flow = new JPanel(new BorderLayout());
	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (table.getValueAt(row, 6).toString().contentEquals("0") && stateRead) {
				comp.setBackground(Color.pink);
			} else
				comp.setBackground(null);
			return comp;
		};
	};
	int cnt;
	JTextField txt[] = new JTextField[2];
	JTextArea area;

	public Mail() {
		super("메일함", 450, 600);
		setUI();
		setVisible(true);
	}

	void setUI() {
		add(lbl("메일함", 0, 20), "North");
		add(tab);

		for (int i = 0; i < p.length; i++) {
			tab.add(cap[i], p[i] = new JPanel(new BorderLayout()));
			setEmpty(p[i], 5, 5, 5, 5);
		}

		flow.add(chk, "West");

		var tmp = new JPanel();
		flow.add(tmp, "East");

		tmp.add(btn[0] = btn("읽음", a -> {
			boolean state = false;
			for (int i = 0; i < t.getRowCount(); i++) {
				if (t.getValueAt(i, 0).equals(true)) {
					state = true;
					break;
				}
			}
			if (!state) {
				eMsg("선택한 메일이 없습니다.");
				return;
			}
			for (int i = 0; i < t.getRowCount(); i++) {
				if (t.getValueAt(i, 0).equals(true)) {
					execute("update mail set `read` = 1 where ma_no ='" + t.getValueAt(i, 4).toString() + "'");
				}
			}
			addRow(mailSql("send"));
		}));

		tmp.add(btn[1] = btn("삭제", a -> {
			boolean state = false;
			for (int i = 0; i < t.getRowCount(); i++) {
				if (t.getValueAt(i, 0).equals(true)) {
					state = true;
					break;
				}
			}
			if (!state) {
				eMsg("선택한 메일이 없습니다.");
				return;
			}
			for (int i = 0; i < t.getRowCount(); i++) {
				if (t.getValueAt(i, 0).equals(true)) {
					execute("delete from mail where ma_no ='" + t.getValueAt(i, 4).toString() + "'");
				}
			}
			eMsg("삭제가 완료되었습니다.");
			addRow(mailSql("send"));
		}));

		send();
		iMsg("읽지 않은 메일이 " + cnt + "건 있습니다.");

		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		int widths[] = { 150, 60, 60 };
		for (int i = 1; i < widths.length + 1; i++) {
			t.getColumnModel().getColumn(i).setPreferredWidth(widths[i - 1]);
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		t.getColumnModel().getColumn(0).setMaxWidth(30);
		for (int i = 4; i <= 6; i++) {
			t.getColumnModel().getColumn(i).setMinWidth(0);
			t.getColumnModel().getColumn(i).setMaxWidth(0);
		}

		for (int i = 0; i < t.getRowCount(); i++) {
			if (t.getValueAt(i, 6).toString().contentEquals("0")) {
				t.setBackground(Color.pink);
			} else
				t.setBackground(null);
		}

		tab.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println(tab.getSelectedIndex());
				switch (tab.getSelectedIndex()) {
				case 0:
					send();
					break;
				case 1:
					receive();
					break;
				default:
					write();
					break;
				}
			}
		});

		t.setRowHeight(25);
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					execute("update mail set `read` = 1 where ma_no ='" + t.getValueAt(t.getSelectedRow(), 4).toString()
							+ "'");
					addRow(mailSql("send"));
				} else if (e.getClickCount() == 1) {
					if (t.getSelectedColumn() != 0) {
						String mano = t.getValueAt(t.getSelectedRow(), 4).toString();
						write();
						txt[0].setText(t.getValueAt(t.getSelectedRow(), 2).toString());
						txt[1].setText(t.getValueAt(t.getSelectedRow(), 1).toString());
						area.setText(t.getValueAt(t.getSelectedRow(), 5).toString());
					}
				}
			}
		});

		setEmpty((JPanel) getContentPane(), 10, 10, 10, 10);
		setVisible(true);
	}

	void addRow(String sql) {
		m.setRowCount(0);
		try {
			var rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Object row[] = { false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getString(6) };
				if (rs.getString(6).contentEquals("0"))
					cnt++;
				m.addRow(row);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void send() {
		init();
		stateRead = true;
		p[0].add(flow, "North");
		p[0].add(new JScrollPane(t));
		addRow(mailSql("send"));

		repaint();
		revalidate();

	}

	void receive() {
		init();
		stateRead = false;
		p[1].add(flow, "North");
		p[1].add(new JScrollPane(t));
		addRow(mailSql("receive"));

		repaint();
		revalidate();

	}

	void write() {
		init();

		JPanel mailP = new JPanel(new BorderLayout());
		((JPanel) tab.getSelectedComponent()).add(mailP);

		var flow = new JPanel(new FlowLayout(0));

		mailP.add(flow, "North");
		mailP.add(new JScrollPane(area = new JTextArea()));
		area.setLineWrap(true);

		sz(flow, 1, 70);

		flow.add(sz(new JLabel("보낸사람"), 120, 25));
		flow.add(sz(txt[0] = new JTextField(), 250, 25));
		flow.add(sz(new JLabel("제목"), 120, 25));
		flow.add(sz(txt[1] = new JTextField(), 250, 25));

		if (tab.getSelectedIndex() == 2) {
			mailP.add(s = new JPanel(), "South");
			s.add(btn("보내기", a -> {
				int division = (type.contentEquals("user")) ? 1 : 2;
				String title = "";
				int no = (mno == 0) ? uno : mno;
				if (txt[0].getText().isEmpty()) {
					eMsg("받는사람을 입력해주세요.");
					txt[0].requestFocus();
					return;
				}
				if (txt[1].getText().isEmpty()) {
					if (JOptionPane.showConfirmDialog(null, "제목이 지정되지 않았습니다.\n메일을 보내시겠습니까?", "확인",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						title = "(제목없음)";
					} else {
						txt[1].requestFocus();
						return;
					}
				}

				iMsg("메일이 정상적으로 보내졌습니다.");
				String sender = "";
				if (type.contentEquals("user")) {
					sender = getOne("select * from manager where email='" + txt[0].getText() + "'");
				} else
					sender = getOne("select * from user where email='" + txt[0].getText() + "'");
				execute("insert into mail values(0, '" + division + "','" + no + "','" + sender + "','" + title + "','"
						+ area.getText() + "',curdate(),0)");
				tab.setSelectedIndex(1);
			}));
		}
	}

	void init() {
		chk.setSelected(false);
		for (int i = 0; i < p.length; i++)
			p[i].removeAll();
		btn[0].setVisible(tab.getSelectedIndex() == 0);
	}

	public static void main(String[] args) {
		type = "user";
		uno = 1;
		new Mail();
	}

}
