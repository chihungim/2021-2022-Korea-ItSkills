package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Payment extends BaseFrame {

	public Payment(Purchase p) {
		super("결제", 400, 500);

		add(n = newJPanel(new BorderLayout()), "North");
		n.add(new JLabel(getIcon("./datafile/스토어/" + p.store.get(1) + ".jpg", 250, 250)));
		n.add(ns = newJPanel(new GridLayout(0, 1)), "South");

		ns.add(lbl(p.store.get(1) + "", JLabel.CENTER, 15));
		ns.add(lbl("수량 " + p.spinner.value + "개", JLabel.CENTER, 12));

		add(c = new JPanel(new GridLayout(0, 1)));
		var temp1 = new JPanel(new BorderLayout());
		var temp2 = new JPanel(new BorderLayout());

		var l1 = lbl("상품단가", JLabel.LEFT, 15);
		var l2 = lbl(df.format(cint(p.store.get(3))) + "원", JLabel.RIGHT, 15);
		var l3 = lbl("결제금액", JLabel.LEFT, 15);
		var l4 = lbl(df.format(cint(p.store.get(3)) * p.spinner.value) + "원", JLabel.RIGHT, 15);

		temp1.add(l1, "West");
		temp1.add(l2, "East");
		temp2.add(l3, "West");
		temp2.add(l4, "East");

		c.add(temp1);
		c.add(temp2);

		c.add(rbtn("결제하기", a -> {
			if (JOptionPane.showConfirmDialog(this, "결제 하시겠습니까?", "결제", JOptionPane.YES_NO_OPTION) == 0) {
				execute("insert orderlist values(0,?,?,?,?)", uno, p.store.get(0), p.spinner.value, LocalDate.now());
				imsg("결제가 완료되었습니다.");
			}
			for (var w : getWindows())
				w.setVisible(w instanceof Store);
		}));

		temp1.setBorder(new MatteBorder(0, 0, 1, 0, Color.WHITE));

		b(Color.black, temp1, temp2);

		f(Color.WHITE, l1, l2, l3, l4);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

}
