package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Reserve extends BaseFrame {

	DefaultTableModel m = new DefaultTableModel(null, ",번호,기구명,층수,탑승인원,키제한,나이 제한,금액,기구설명".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return column == 0;
		};

		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return getValueAt(0, columnIndex).getClass();
		};

	};

	JTable t = new JTable(m) {
		public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
			var comp = (JComponent) super.prepareRenderer(renderer, row, column);
			if (t.getSelectedRow() == row) {
				comp.setBackground(Color.YELLOW);
			} else {
				comp.setBackground(Color.WHITE);
			}

			return comp;
		};
	};

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
	JTextField txt = new JTextField();
	DecimalFormat df = new DecimalFormat("#,##0");
	int sum = 0;

	public Reserve() {
		super("예매", 650, 500);
		data();
		ui();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Reserve();
	}

	void ui() {
		var n = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var s = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(n, "North");
		add(new JScrollPane(t));
		add(s, "South");

		n.add(btn("예매", a -> {
			int cnt = 0;
			for (int k = 0; k < m.getRowCount(); k++) {
				if (t.getValueAt(k, 0).equals(true)) {
					if (toInt(getOne("SELECT count(*) FROM ticket where t_date = curdate() and r_no='"
							+ t.getValueAt(k, 1) + "'")) > toInt(t.getValueAt(k, 4))) {
						eMsg(t.getValueAt(k, 2) + "은(는) 만석입니다.");
						return;
					}
					cnt++;

					if (cnt >= 5) {
						iMsg("메직패스에 당첨되었습니다.");
						int mag = toInt(Main.lbl[4].getText()) + 1;
						Main.lbl[4].setText("매직패스(" + mag + ")");
						Main.lbl[4].setEnabled(true);
					}
				}
			}

			if (cnt == 0) {
				eMsg("예매할 놀이기구를 선택해주세요.");
				return;
			}

			iMsg("예매가 완료되었습니다.");
			for (int k = 0; k < t.getRowCount(); k++) {
				if (t.getValueAt(k, 0).equals(true)) {
					System.out.println(
							"insert into ticket values(0," + uno + ",curdate(),'" + t.getValueAt(k, 1) + "',0)");
					execute("insert into ticket values(0," + uno + ",curdate(),'" + t.getValueAt(k, 1) + "',0)");
				}
			}

			dispose();

		}));

		s.add(lbl("총금액 :", JLabel.CENTER));
		s.add(sz(txt, 200, 30));

		int widths[] = { 50, 150, 50, 100, 100, 100, 100, 150 };
		for (int i = 1; i < m.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setPreferredWidth(widths[i - 1]);
		}

		var tmp = new TableModelListener() {
			int sum;

			@Override
			public void tableChanged(TableModelEvent e) {
				for (int k = 0; k < m.getRowCount(); k++) {
					if (t.getValueAt(k, 0).equals(true)) {
						try {
							sum += toInt(df.parse((String) t.getValueAt(k, 7)));
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

				txt.setText(df.format(sum));
			}
		};
		m.addTableModelListener(tmp);
		txt.setEnabled(false);
	}

	void data() {
		try {
			var rs = stmt.executeQuery("select * from ride");
			while (rs.next()) {
				if ((uheight >= toInt(rs.getString("r_height")) && uold >= toInt(rs.getString("r_old"))
						&& !(toInt(rs.getString("r_disable")) == 1 && udisable == 1))) {
					Object row[] = new Object[m.getColumnCount()];
					row[0] = false;
					for (int i = 1; i < m.getColumnCount(); i++) {
						System.out.println(toInt(rs.getString("r_old")));
						t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
						row[i] = rs.getString(i);
					}
					row[row.length - 1] = rs.getString("r_explation");
					m.addRow(row);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
