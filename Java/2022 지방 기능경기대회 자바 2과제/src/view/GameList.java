package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class GameList extends BaseFrame {

	DefaultTableModel m = model("번호,날짜,지점명,장르,테마명".split(","));
	JTable t = table(m);

	public GameList() {
		super("게임리스트", 800, 400);
		add(lbl("회원명:" + uname, 0, 25), "North");
		add(new JScrollPane(t));

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				data();
			}
		});

		int idx[] = { 0, 1, 3 };
		int w[] = { 80, 100, 100 };

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				var r = t.getSelectedRow();
				if (r == -1) {
					return;
				}

				var d = LocalDate.parse(t.getValueAt(r, 1) + "", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (d.isAfter(LocalDate.now())) {
					emsg("미래로 예약된 게임은 실행할 수 없습니다.");
					return;
				}

				new RoomEscape(t.getValueAt(r, 0) + "").addWindowListener(new before(GameList.this));
			}
		});

		for (int i = 0; i < idx.length; i++) {
			t.getColumnModel().getColumn(idx[i]).setMinWidth(w[i]);
			t.getColumnModel().getColumn(idx[i]).setMaxWidth(w[i]);
		}
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	void data() {
		var rs = getValues(
				"select r_no, r_date, c_name, g_name, t_name from reservation r , genre g , theme t , cafe c where r.c_no = c.c_no and g.g_no = t.g_no and r.t_no = t.t_no and r_attend = 0 and u_no = ?",
				uno);
		addRow(m, rs);
	}

	public static void main(String[] args) {
		uno = "1";
		new GameList();
	}
}
