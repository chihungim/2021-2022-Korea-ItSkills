package 전남;

import java.awt.Font;
import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Login extends Baseframe {
	JPanel c, s;
	String cap[] = "아이디,비밀번호".split(","), bcap[] = "로그인,회원가입".split(",");
	JTextField txt[] = { new JTextField(15), new JTextField(15)};
	JLabel jl;
	
	public Login() {
		super("로그인", 300, 200);
		
		add(jl = new JLabel("Room escape", JLabel.CENTER), "North");
		jl.setFont(new Font("궁서체",Font.BOLD, 30));
		add(c = new JPanel(new GridLayout(0, 1)));
		add(s = new JPanel(new GridLayout(1, 0, 5, 5)), "South");

		for (int i = 0; i < cap.length; i++) {
			var t = new JPanel();
			t.add(size(label(cap[i], JLabel.LEFT), 60, 15));
			t.add(txt[i]);
			c.add(t);
		}
		
		for (int i = 0; i < bcap.length; i++) {
			s.add(btn(bcap[i], a->{
				if(a.getActionCommand().equals(bcap[0])) {
					for (int j = 0; j < txt.length; j++) {
						if(txt[j].getText().isEmpty()) {
							errmsg("빈칸이 있습니다.");
							return;
						}
					}
					try {
						ResultSet rs = stmt.executeQuery("select u_no, u_name, u_id from user where u_id='"+txt[0].getText()+"' and u_pw='"+txt[1].getText()+"'");
						if(rs.next()) {
							NO = rs.getInt(1);
							NAME = rs.getString(2);
							ID = rs.getString(3);
							msg(NAME+"님 환영합니다.");
							new Mainf().addWindowListener(new be(Login.this));
							txt[0].setText("");
							txt[1].setText("");
						}else {
							errmsg("아이디 또는 비밀번호가 일치하지 않습니다.");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}else {
					txt[0].setText("");
					txt[1].setText("");
					new Sign().addWindowListener(new be(Login.this));
				}
			}));
		}
		
		emp(((JPanel)getContentPane()), 5, 5, 5, 5);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Login();
	}

}
