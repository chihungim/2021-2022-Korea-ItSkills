package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TradeLog extends BaseFrame {
	DefaultTableModel m = model("번호,상품명,날짜,수량,금액,b_img,b_no".split(","));
	JTable t = table(m);

	JRadioButton radio[] = { new JRadioButton("구매내역", true), new JRadioButton("판매내역") };
	JLabel imglbl;

	JComboBox<String> combo = new JComboBox<String>();
	JTextField txt;

	ButtonGroup bg = new ButtonGroup();

	DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer() {
		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			var jc = (JComponent) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			var d = m.getDataVector();
			var bases = m.getDataVector().stream().map(x -> x.get(1)).collect(Collectors.toList());
			HashMap<String, Integer> map = new HashMap<>();
			d.stream().filter(a -> bases.stream().filter(x -> x.equals(a.get(1))).count() > 1).forEach(x -> {
				if (!map.containsKey(x.get(1) + ""))
					map.put(x.get(1) + "", parsedec(x.get(4)));
				if (parsedec(x.get(4)) < map.get(x.get(1))) {
					map.replace(x.get(1) + "", parsedec(x.get(4)));
				}
			});
			var key = t.getValueAt(row, 1).toString();
			var v = parsedec(t.getValueAt(row, 4));

			if (map.containsKey(key) && map.get(key) == v) {
				jc.setFont(new Font("", Font.ITALIC, 15));
				jc.setForeground(Color.blue);
			} else {
				jc.setFont(new JTable().getFont());
				jc.setForeground(Color.BLACK);
			}
			return jc;
		};
	};

	public TradeLog() {
		super("농산물거래내역", 1200, 600);
		for (int i = 0; i < radio.length; i++)
			bg.add(radio[i]);
		add(n = newJPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		add(c = newJPanel(new BorderLayout()));
		add(s = newJPanel(new FlowLayout(FlowLayout.LEFT)), "South");

		n.add(lbl("농산물 거래내역", JLabel.CENTER, 20));
		n.add(radio[0]);
		n.add(radio[1]);

		c.add(cn = newJPanel(new FlowLayout(FlowLayout.LEFT)), "North");
		c.add(cc = newJPanel(new BorderLayout()));
		cn.add(lbl("월", JLabel.LEFT));
		cn.add(combo);

		cc.add(new JScrollPane(t));
		cc.add(imglbl = new JLabel(), "East");

		s.add(new JLabel("합계", JLabel.CENTER));
		s.add(txt = new JTextField(20));
		txt.setEnabled(false);

		sz(imglbl, 500, 500);
		imglbl.setBorder(new LineBorder(Color.BLACK));
		combo.addItem("전체");
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < t.getColumnCount(); i++) {
			t.getColumnModel().getColumn(i).setCellRenderer(dtcr);
		}
		t.getColumn("번호").setMaxWidth(60);
		t.getColumn("번호").setMinWidth(60);
		t.getColumn("b_img").setMinWidth(0);
		t.getColumn("b_img").setMaxWidth(0);
		t.getColumn("b_no").setMinWidth(0);
		t.getColumn("b_no").setMaxWidth(0);

		radio[0].addActionListener(a -> data());
		radio[1].addActionListener(a -> data());

		for (int i = 1; i <= 12; i++)
			combo.addItem(i + "월");
		combo.addItemListener(i -> data());
		data();

		evt(t, a -> {
			if (t.getSelectedRow() == -1)
				return;

			if (a.getClickCount() == 2 && a.getButton() == 1) {
				imglbl.setIcon(getBlob(t.getValueAt(t.getSelectedRow(), 5), 500, 500));
			}

			var pop = new JPopupMenu();
			var item = new JMenuItem(radio[0].isSelected() ? "가격비교" : "농산물관리");
			pop.add(item);
			item.addActionListener(e -> {
				if (e.getActionCommand().equals("가격비교")) {
					new Chart(t.getValueAt(t.getSelectedRow(), 6) + "").addWindowListener(new before(this));
				} else {
					new BaseManage(t.getValueAt(t.getSelectedRow(), 6) + "", user.get(0) + "", true)
							.addWindowListener(new before(this));
				}
			});

			if (a.getButton() == 3)
				pop.show(t, a.getX(), a.getY());
		});
		setVisible(true);
	}

	void data() {
		String sql = "";
		int sum = 0;
		imglbl.setIcon(null);
		if (radio[0].isSelected()) {
			sql = "SELECT p_no, b_name, p_date, p_quantity, format(f_amount, '#,##0'), b_img, b.b_no  FROM farm f , base b, purchase p  where p.f_no = f.f_no and f.b_no = b.b_no and  p.u_no = ? "
					+ (combo.getSelectedIndex() != 0 ? "and month(p_date) = " + combo.getSelectedIndex() : "");
		} else {
			sql = "SELECT s_no, b_name, s_date, s_quantity, format(f_amount, '#,##0'), b_img, b.b_no FROM sale s , base b, farm f  where s.f_no = f.f_no and f.b_no = b.b_no and  f.u_no = ? "
					+ (combo.getSelectedIndex() != 0 ? "and month(s_date) = " + combo.getSelectedIndex() : "");
		}

		var rs = getrows(sql, user.get(0));

		for (var r : rs) {
			sum += parsedec(r.get(4));
		}
		addrow(rs, m);

		txt.setText(decformat(sum));
	}

	public static void main(String[] args) {
		디버그("1");
		new TradeLog();
	}
}
