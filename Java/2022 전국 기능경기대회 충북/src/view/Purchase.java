package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class Purchase extends BaseFrame {

	ArrayList<Object> store;
	CSpinner spinner = new CSpinner(1, 5, 1) {
		void cValue(int dir) {
			if (value + dir > max) {
				emsg("최대 " + max + "개 까지 구매하실 수 있습니다.");
				return;
			}

			if (value + dir < min) {
				emsg("최소 " + max + "개 이상은 구매애햐 합니다.");
				return;
			}
			pricelbl.setText(df.format(cint(store.get(3)) * (value + dir)) + "원");
			super.cValue(dir);
		};
	};
	JLabel pricelbl;

	public Purchase(ArrayList<Object> store) {
		super("구매", 700, 300);
		this.store = store;
		setLayout(new GridLayout(1, 0));
		add(new JLabel(getIcon("./datafile/스토어/" + store.get(1) + ".jpg", 300, 300)));
		add(c = newJPanel(new BorderLayout()));
		c.add(cc = newJPanel(new GridLayout(0, 1, 5, 5)));
		c.add(cs = newJPanel(new GridLayout(1, 0, 5, 5)), "South");

		var lbl1 = lbl(store.get(1) + "", JLabel.LEFT, 15);
		var lbl2 = lbl(df.format(cint(store.get(3))) + "원", JLabel.LEFT, 15);
		var temp3 = newJPanel(new FlowLayout(FlowLayout.LEFT));
		temp3.add(sz(lbl("설명", JLabel.LEFT, 12), 60, 30));
		temp3.add(lbl(store.get(2) + "", JLabel.LEFT)).setForeground(Color.LIGHT_GRAY);

		lbl1.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		lbl2.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
		temp3.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));

		cc.add(lbl1);
		cc.add(lbl2);
		cc.add(temp3);
		cc.add(spinner);
		var temp4 = newJPanel(new FlowLayout(FlowLayout.RIGHT));

		cc.add(temp4);
		temp4.add(lbl("총 상품금액", 0, 15));
		temp4.add(pricelbl = lbl(df.format(cint(store.get(3))) + "원", 0, 20)).setForeground(Color.RED);

		cs.add(bbtn("취소", a -> dispose()));
		cs.add(rbtn("구매하기", a -> {
			new Payment(this).addWindowListener(new before(this));
		}));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
		setVisible(true);
	}

}
