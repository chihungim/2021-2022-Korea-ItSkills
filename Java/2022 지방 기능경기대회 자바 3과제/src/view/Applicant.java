package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Applicant extends BaseFrame {
	public Applicant() {
		super("������ ����", 400, 400);
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		load();
		setVisible(true);
	}

	void load() {
		c.removeAll();

		var rs = getValues(
				"select u_img, c_name, u_name, year(now()) - year(u_birth) + 1, u_birth, u_graduate, u_email, a_no, c.c_no from user u , company c, applicant a, employment e where e.e_no = a.e_no and a.u_no = u.u_no and e.c_no = c.c_no  and a.a_apply = 0");

		for (var r : rs) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(new JLabel(toIcon(r.get(0), 80, 80)), "West");
			var tempc = new JPanel(new GridLayout(0, 1));
			temp.add(tempc);
			tempc.add(hylbl("���� ȸ��:" + r.get(1), JLabel.LEFT, 20));
			tempc.add(lbl("�̸�:" + r.get(2)+"(����:"+r.get(3)+"��)", JLabel.LEFT));
			tempc.add(lbl("�������:" + r.get(4), JLabel.LEFT));
			tempc.add(lbl("�����з�:" + graduate[toInt(r.get(5))], JLabel.LEFT));
			tempc.add(lbl("email:" + r.get(6), JLabel.LEFT));
			c.add(temp);
			
			var pop = new JPopupMenu();
			for(var bc : "�հ�,���հ�".split(",")) {
				var item = new JMenuItem(bc);
				item.addActionListener(a->{
					var con = Arrays.asList(apply).indexOf(a.getActionCommand());
					
					setValues("update applicant set a_apply = ? where a_no = ?", con, r.get(7));
					
					if(con == 1) {
						setValues("update company set c_employee = c_employee + 1 where c_no = ?",
								r.get(8));						
					}
					
					imsg("�ɻ簡 �Ϸ�Ǿ����ϴ�.");
					load();
				});
				pop.add(item);
			}
			
			temp.setComponentPopupMenu(pop);
			
			temp.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), new EmptyBorder(5, 5, 5, 5)));
		}

		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		new Applicant();
	}

}
