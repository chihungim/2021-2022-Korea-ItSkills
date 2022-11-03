package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import db.DBManager;

public class Login extends BaseFrame {
	
	String cap[] = "ID,PW".split(",");
	JTextField txt[] = { new JTextField(), new JPasswordField() };
	
	public Login() {
		super("로그인", 400, 250);
		this.setLayout(new GridLayout(0, 1, 10, 10));
		for (int i = 0; i < 2; i++) {
			var tmp = new JPanel(new BorderLayout());
			tmp.add(sz(lbl(cap[i], 2), 50, 30), "West");
			tmp.add(txt[i]);
			this.add(tmp);
		}
		
		this.add(btn("로그인", a->{
			String id = txt[0].getText(), pw = txt[1].getText();
			if (id.isEmpty() || pw.isEmpty()) {
				eMsg("아이디와 비밀번호를 모두 입력해야 합니다.");
				return;
			}

			if (id.equals("admin") && pw.equals("1234")) {
				iMsg("관리자님 환영합니다.");
				new Admin().addWindowListener(new Before(this));
				return;
			}
			
			try {
				var rs = DBManager.rs("select * from user where id='"+id+"' and pw='"+pw+"'");
				if (rs.next()) {
					uno = rs.getString("serial");
					uname = rs.getString("name");
					ubirth = LocalDate.parse(rs.getString("birth")).getYear()+"";
					uage = LocalDate.now().minusYears(LocalDate.parse(rs.getString("birth")).getYear()).getYear()+"";
//					uage = LocalDate.parse(LocalDate.now().getYear()).minusYears().getYear()+"";
					System.out.println(ubirth+", "+uage);
					iMsg(uname+"님 안녕하세요.");
					home.setLog(true);
					dispose();
					return;
				} else {
					eMsg("일치하는 회원정보가 없습니다.");
					return;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}));
		
		setEmpty((JPanel)getContentPane(), 10, 10, 10, 10);
		this.setVisible(true);
	}
	
}
