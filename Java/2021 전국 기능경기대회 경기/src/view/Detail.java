package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class Detail extends BaseFrame {

	String title;
	JLabel imglbl;
	JTextArea area = new JTextArea();
	String cap[] = "탑승인원,탑승제한,위치".split(",");
	String info[] = new String[3];
	JCheckBox chk = new JCheckBox("장애인");

	public Detail(String title) {
		super("상세정보", 700, 400);
		this.title = title;
		data();
		ui();
		setVisible(true);
	}

	void ui() {
		setLayout(new BorderLayout());

		var w = new JPanel(new BorderLayout());
		var c = new JPanel(new BorderLayout());
		var s = new JPanel(new FlowLayout(2));
		var c_c = new JPanel(new BorderLayout());
		var c_s = new JPanel(new GridLayout());

		add(w, "West");
		add(c);
		add(s, "South");
		c.add(c_c);
		c.add(c_s, "South");

		c.add(lbl(title, 0, 25), "North");
		c_c.add(new JLabel("설명"), "North");
		c_c.add(new JScrollPane(area));
		w.add(imglbl);
		s.add(btn("예매", a -> btn_event()));

		for (int i = 0; i < info.length; i++) {
			var tmp = new JPanel(new FlowLayout());
			tmp.setOpaque(false);
			c_s.add(tmp);
			if (i == 3) {
				tmp.setLayout(new FlowLayout(3));
				tmp.add(chk);
			} else {
				tmp.add(new JLabel(cap[i] + " : " + info[i], JLabel.LEFT));
				tmp.setOpaque(false);
			}
		}

		imglbl.setBorder(new LineBorder(Color.BLACK));
		area.setLineWrap(true);
		area.setEnabled(false);
		area.setBorder(new LineBorder(Color.BLACK));
		w.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		c_c.setOpaque(false);
		c_s.setOpaque(false);
		chk.setOpaque(false);
		chk.setHorizontalAlignment(SwingConstants.RIGHT);

	}

	void data() {
		area.setText(getOne("select r_explation from ride where r_name = '" + title + "'").split("#")[0]);

		try {
			var rs = stmt.executeQuery("select r_max, r_height, r_floor from ride where r_name like '%" + title + "%'");
			if (rs.next()) {
				for (int i = 0; i < 3; i++) {
					info[i] = rs.getString(i + 1);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			var rs = stmt.executeQuery("select * from ride where r_name like '%" + title + "%'");
			System.out.println("select * from ride where r_name like '%" + title + "%'");
			if (rs.next()) {
				imglbl = new JLabel(getIcon("datafiles/이미지/" + title + ".jpg", 230, 300));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		chk.setSelected(getOne("select r_disable from ride where r_name = '" + title + "'").contentEquals("1"));
		chk.setEnabled(false);
	}

	void btn_event() {
		var no = getOne("select r_no from ride where r_name like '%" + title + "%'");
		if (toInt(getOne("select count(*) from ticket where t_date = curdate() and r_no=" + no)) > toInt(info[0])) {
			eMsg(title + "은(는) 만석입니다.");
			return;
		}

		execute("insert into ticket values(0,'" + uno + "',curdate(),'" + no + "',0)");

		iMsg("예매가 완료되었습니다.");

		for (var w : getFrames()) {
			if (w instanceof Main) {
				w.setVisible(true);
				w.addWindowListener(new Before(this));
			}
		}
	}
}
