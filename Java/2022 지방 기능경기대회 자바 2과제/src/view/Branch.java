package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.concurrent.Flow;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.border.EmptyBorder;

public class Branch extends BaseFrame {

	JPanel m, m2;
	JLabel m1;
	int pr = 0;
	String tno;

	public Branch(String cno) {
		super("지점소개", 800, 800);
		add(n = new JPanel(new BorderLayout()), "North");
		n.add(ne = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "East");
		n.add(nw = new JPanel(new FlowLayout(FlowLayout.LEFT)), "West");

		var cafe = getValue("select * from cafe where c_no = ?", cno);
		var themes = cafe.get(2).toString().split(",");
		pr = toInt(cafe.get(6));

		var tmp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		nw.add(sz(new JScrollPane(tmp), 180, 80));
		ne.add(btn("예약하기", a -> {
			new Reservation(cno, tno).addWindowListener(new before(this));
		}));

		for (var th : themes) {
			var img = new JLabel(getIcon("./datafiles/테마/" + th + ".jpg", 50, 50)) {
				@Override
				public JToolTip createToolTip() {
					return new JToolTip();
				}
			};
			img.setToolTipText(getValue("select t_name from theme where t_no = ?", th).get(0) + "");
			tmp.add(img);
			img.setEnabled(false);
			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (var th : tmp.getComponents()) {
						th.setEnabled(false);
					}
					img.setEnabled(true);
					tno = th;
					contents();
				}

			});
		}

		tno = themes[0];
		tmp.getComponent(0).setEnabled(true);

		add(c = new JPanel(new BorderLayout(5, 5)));
		var title = lbl(cafe.get(1) + "", JLabel.LEFT, 30);
		c.add(title, "North");
		c.add(m = new JPanel(new GridLayout(1, 0)));
		m.add(m1 = new JLabel());
		m.add(m2 = new JPanel(new BorderLayout()));
		title.setForeground(Color.ORANGE);
		c.setBorder(new EmptyBorder(5, 5, 5, 5));
		c.setBackground(Color.BLACK);
		m.setOpaque(false);
		m2.setOpaque(false);
		contents();
		setVisible(true);
	}

	public static void main(String[] args) {
		new Branch("A-001");
	}

	private void contents() {
		m2.removeAll();
		m1.setIcon(getIcon("./datafiles/테마/" + tno + ".jpg", 400, 620));
		var theme = getValue("select * from theme where t_no = ?", tno);

		var lbl1 = lbl(theme.get(1) + "", JLabel.LEFT, 25);
		var lbl2 = lbl("<html>" + theme.get(3) + "", JLabel.LEFT, 20);
		var lbl3 = lbl(
				"<html>장르:"
						+ getValue("select g_name from theme t , genre g where t.g_no = g.g_no and t.t_no = ?", tno)
								.get(0)
						+ "<br>최대 인원:" + theme.get(4) + "명<br>시간:" + theme.get(5) + "분<br>가격:"
						+ new DecimalFormat("#,##0").format(pr) + "원",
				JLabel.LEFT, 15);

		m2.add(lbl1, "North");
		m2.add(lbl2);
		m2.add(lbl3, "South");
		lbl1.setForeground(Color.WHITE);
		lbl2.setForeground(Color.WHITE);
		lbl3.setForeground(Color.WHITE);

		revalidate();
		repaint();
	}
}
