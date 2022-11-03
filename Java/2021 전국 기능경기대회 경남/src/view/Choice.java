
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Choice extends JDialog {

	int xywh[][] = { { 13, 17, 142, 29 }, { 170, 17, 69, 29 }, { 431, 18, 107, 25 }, { 15, 65, 307, 382 },
			{ 338, 66, 237, 339 }, { 477, 424, 97, 22 } };

	JPanel cate;
	JPanel shopList;
	JTextField searchField;
	ArrayList<String> types = new ArrayList<>();
	ArrayList<String> cap = new ArrayList<String>();
	HashMap<String, JPanel> hashMap = new HashMap<>();

	JComponent jc[] = { searchField = new JTextField(), BaseFrame.btn("검색", a -> search()),
			BaseFrame.lbl("선택한 직종", JLabel.LEFT), new JScrollPane(cate = new JPanel(new GridLayout(0, 1))),
			new JScrollPane(shopList = new JPanel()), BaseFrame.btn("선택완료", a -> Done()) };

	CompanyRegisteration cr;

	JTextField txt;
	
	public Choice(BaseFrame b, JTextField txt) {
		super(b, true);
		this.txt = txt;
		setUI();
		setData();
		search();
		setVisible(true);
	}

	void setUI() {
		setTitle("직종선택");
		setLayout(null);
		setLocationRelativeTo(null);
		setSize(600, 500);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		shopList.setLayout(new BoxLayout(shopList, BoxLayout.Y_AXIS));

		for (int i = 0; i < jc.length; i++) {
			add(jc[i]);
			jc[i].setBounds(xywh[i][0], xywh[i][1], xywh[i][2], xywh[i][3]);
		}

	}

	void search() {
		cate.removeAll();
		types.forEach(a -> {
			try {
				var rs = BaseFrame.stmt.executeQuery(
						"SELECT t.t_no, d_no, d.name FROM details d, type t where d.t_no=t.t_no and t.name = '" + a
								+ "'" + (searchField.getText().isEmpty() ? ""
										: "  and d.name like '%" + searchField.getText() + "%'"));

				var boxes = new ArrayList<JCheckBox>();
				while (rs.next()) {
					boxes.add(new JCheckBox(rs.getString(3)));
				}
				cate.add(new Category(a, boxes));
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		revalidate();
		repaint();
	}

	void Done() {
		if (hashMap.keySet().size() > 1) {
			BaseFrame.eMsg("하나만 선택해주세요.");
			return;
		}

		String str = "";

		for (String s : hashMap.keySet()) {
			if (str.equals("")) {
				str += s;
			} else {
				str += "," + s;
			}
		}

		txt.setText(str);
		
		dispose();
	}

	void setData() {
		try {
			var rs = BaseFrame.stmt.executeQuery("select * from type");
			while (rs.next()) {
				types.add(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class Category extends JPanel {

		ArrayList<JCheckBox> comboName;

		public Category(String title, ArrayList<JCheckBox> comboName) {
			setLayout(new BorderLayout());
			this.comboName = comboName;
			var c = new JPanel(new GridLayout(0, 2));
			add(BaseFrame.lbl(title, JLabel.LEFT, 15), "North");
			add(c);
			for (int i = 0; i < comboName.size(); i++) {
				comboName.get(i).addActionListener(a -> {

					var box = ((JCheckBox) a.getSource());
					JPanel m = new JPanel(new BorderLayout());
					if (((JCheckBox) a.getSource()).isSelected()) {

						JPanel p = new JPanel(new BorderLayout());
						m.setName(box.getText());
						m.add(p);
						p.add(BaseFrame.lbl(box.getText(), JLabel.LEFT));
						p.add(BaseFrame.btn("X", o -> {
							hashMap.remove(box.getText());
							shopList.remove(m);
							shopList.repaint();
							shopList.revalidate();
							box.setSelected(false);
						}), "East");

						shopList.add(BaseFrame.sz(m, 200, 40));
						cap.add(box.getText());

						hashMap.put(box.getText(), m);

						m.setMaximumSize(new Dimension(200, 40));
						p.setBorder(new EmptyBorder(5, 5, 5, 5));
						m.setBorder(new LineBorder(Color.BLACK));
						shopList.repaint();
						shopList.revalidate();
					} else {
						shopList.remove(hashMap.get(box.getText()));
						hashMap.remove(box.getText());
						shopList.revalidate();
						shopList.repaint();
					}
				});
				c.add(comboName.get(i));
			}

		}
	}
}