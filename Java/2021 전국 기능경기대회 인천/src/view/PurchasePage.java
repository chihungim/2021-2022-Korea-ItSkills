package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.text.DecimalFormat;

import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import view.SearchPage.OrderItem;

public class PurchasePage extends BasePage {
	DefaultTableModel model = new DefaultTableModel(null, "이미지,상품이름,행사여부,가격,재고,수량,총합".split(",")) {
		public boolean isCellEditable(int row, int column) {
			return column == 5;
		};

		public java.lang.Class<?> getColumnClass(int columnIndex) {
			return JComponent.class;
		};
	};

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			if (value instanceof JComponent) {
				return (JComponent) value;
			} else {
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		};
	};

	JTable t = new JTable(model);
	JTextField txt = new JTextField(10);
	DecimalFormat df = new DecimalFormat("#,##0");

	public class tSpinner extends DefaultCellEditor {

		JSpinner s;
		JSpinner.DefaultEditor e;
		boolean valueSet;
		int up = 1;

		public tSpinner() {
			super(new JTextField());
			s = new JSpinner(new SpinnerNumberModel(1, 1, null, up));
			s.addChangeListener(c -> {
				int sum = 0;

				for (int i = 0; i < t.getRowCount(); i++) {
					sum += toInt(t.getValueAt(i, 6));
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			up = table.getValueAt(row, 2).toString().contentEquals("O") ? 2 : 1;
			int max = toInt(table.getValueAt(row, 4).toString());
			s.setModel(new SpinnerNumberModel(1, 1, max, up));
			s.setValue(value);
			return s;
		}

		@Override
		public Object getCellEditorValue() {
			return s.getValue();
		}
	};

	public PurchasePage(int sno, JPanel p) {
		setLayout(new BorderLayout());
		add(new JScrollPane(t));
		add(s = new JPanel(new BorderLayout()), "South");

		var s_c = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		var s_s = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		s.add(s_s, "South");
		s.add(s_c);

		s_c.add(lbl("금액", JLabel.RIGHT));
		s_c.add(txt);
		s_c.add(lbl("원", JLabel.LEFT));
		s_s.add(setB(btn("구매하기", a -> {
			var sum = 0;
			for (int i = 0; i < t.getRowCount(); i++) {
				sum += toInt(t.getValueAt(i, 6));
				int pno = toInt(getOne("select p_No from product where p_Name ='" + t.getValueAt(i, 1) + "'"));
				execute("insert into purchase values(0, '" + sno + "','" + pno + "','" + uno + "','"
						+ t.getValueAt(i, 5) + "','" + t.getValueAt(i, 6) + "', curdate())");
				execute("update stock set st_Count = st_Count - '" + t.getValueAt(i, 4) + "' where p_No =" + pno
						+ " and s_No = " + sno);
			}
			iMsg("총 " + df.format(sum) + "원 결제완료 되었습니다.");
			mf.swapPage(new MainPage());
		}), new LineBorder(Color.BLACK)));

		t.setDefaultRenderer(JComponent.class, dtcr);
		t.getColumnModel().getColumn(5).setCellEditor(new tSpinner());
		t.setRowHeight(80);
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		txt.setEditable(false);
		txt.setHorizontalAlignment(SwingConstants.RIGHT);

		for (var v : p.getComponents()) {
			var item = (OrderItem) v;
			model.addRow(new Object[] { new JLabel(getIcon("./datafile/image/" + item.name + ".jpg", 80, 80)),
					item.name, (item.state != 0) ? "O" : "X", item.price, item.stock, item.state != 0 ? 2 : 1,
					item.price });
		}

		t.getModel().addTableModelListener(e -> {
			if (e.getType() == TableModelEvent.UPDATE) {
				int r = e.getFirstRow();
				int c = e.getColumn();
				if (c == 5) {
					if (toInt(t.getValueAt(r, c)) > toInt(t.getValueAt(r, c - 1))) {
						eMsg("재고 개수를 초과하여 구매할 수 없습니다.");
						t.setValueAt(toInt(t.getValueAt(r, c - 1)), r, c);
					}

					var st = (t.getValueAt(r, c - 3)).toString().contentEquals("O") ? 0.5 : 1;
					int price = (int) (toInt(t.getValueAt(r, c - 2)) * toInt(t.getValueAt(r, c)) * st);
					t.setValueAt(price, r, c + 1);
					int sum = 0;
					for (int i = 0; i < t.getRowCount(); i++) {
						sum += toInt(t.getValueAt(i, c + 1));
					}
					txt.setText(df.format(sum));
				}
			}
		});

		int sum = 0;

		for (int i = 0; i < t.getRowCount(); i++) {
			sum += toInt(t.getValueAt(i, 6));
		}
		txt.setText(df.format(sum));
		setBorder(new EmptyBorder(50, 50, 50, 50));
	}

}