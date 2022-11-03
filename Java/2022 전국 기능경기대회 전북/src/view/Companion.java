package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.concurrent.Flow;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSpinner.DefaultEditor;

public class Companion extends BaseDialog {
	JSpinner spin[] = new JSpinner[3];

	public Companion(JTextField txt) {
		super("탑승객선택", 400, 300);
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		var cap = "성인(12세~),소아(만 2세 ~ 12세 미만),유아(만2세 미만)".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new BorderLayout());
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 150, 0), "West");
			temp.add(spin[i] = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1)));
			((DefaultEditor) spin[i].getEditor()).getTextField().setEnabled(false);
			((DefaultEditor) spin[i].getEditor()).getTextField().setDisabledTextColor(Color.BLACK);
			c.add(temp);
		}

		s.add(bbtn("확인", a -> {
			int v1 = cint(spin[0].getValue());
			int v2 = cint(spin[1].getValue());
			int v3 = cint(spin[2].getValue());
			if (v1 + v2 + v3 == 0) {
				emsg("탑승객인원을 선택해주세요.");
				return;
			}
			if (v1 + v2 + v3 > 5) {
				emsg("5명 이하로 선택해주세요.");
				return;
			}

			if (JOptionPane.showConfirmDialog(this,
					"성인 " + v1 + "명, 소아 " + v2 + "명, 유아 " + v3 + "명을 선택하셨습니다.\n예약을 계속 하시겠습니까?", "안내",
					JOptionPane.YES_NO_OPTION) == 0) {
				txt.setText("성인-" + v1 + ",소아-" + v2 + ",유아-" + v3);
			}
			dispose();
		}));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
	}
}
