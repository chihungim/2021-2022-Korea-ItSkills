import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Detail extends BaseDialog {
	JPanel p = new JPanel(new GridLayout(0, 1, 0, 5));
	JLabel lbl;
	JScrollPane jsc = new JScrollPane(p);

	String name;

	public Detail(String name) {
		super("상세설명", 400, 500);

		this.name = name;

		UI();
		setVisible(true);
	}

	void UI() {
		add(lbl = lbl("상세설명", 2, 25), "North");
		add(jsc);

		try {
			var rs = stmt.executeQuery(
					"select * from recommend_info ri, recommend r, location l where ri.recommend_no = r.no and r.location_no = l.no and name = '"
							+ name + "'");
			while (rs.next()) {
				var tmp = new JPanel(new BorderLayout());
				tmp.add(new JLabel(new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage("지급파일/images/recommend/" + hashMap.get(name) + "/" + rs.getString("title") + ".jpg")
						.getScaledInstance(320, 250, Image.SCALE_SMOOTH))), "North");
				if (!rs.getString("description").isEmpty()) {
					JTextArea area = new JTextArea(rs.getString("description"));
					tmp.add(size(area, 280, 150));
					area.setBorder(new LineBorder(Color.BLACK));
					area.setLineWrap(true);
				} else {
					size(tmp, 280, 250);
				}

				p.add(tmp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		((JPanel) getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
		p.setBackground(Color.WHITE);
		lbl.setBorder(new EmptyBorder(0, 0, 15, 0));
	}

	public static void main(String[] args) {
		new Detail("강원도");
	}
}
