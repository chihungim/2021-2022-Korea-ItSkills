

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Seller extends Basedialog {
	JPanel c = new JPanel(new FlowLayout(1)), w = new JPanel(new BorderLayout()), wc = new JPanel(new GridLayout(0, 1, 0, 10));
	JButton jb[] = new JButton[3];
	JButton save[] = new JButton[2];
	String str[] = "메뉴관리,주문관리,통계".split(","), aa[] = "저장,배경 사진 등록".split(","),bb[] = "비밀번호,상호명,배달수수료".split(",");
	JComboBox<String> combo = new JComboBox<String>();
	PlaceH tt[] = new PlaceH[3];
	JTextArea ta;
	JLabel img;
	JComponent comp[] = {
		new JTextField(),	
		new JTextField(),	
		tt[0] = new PlaceH(50),
		tt[1] = new PlaceH(50),
		combo,
		tt[2] = new PlaceH(50),
		label("가게 설명", 2),
	};
	
	public Seller() {
		super("판매자 대시보드", 800, 600);
		
		add(w,"West");
		add(c);
		
		for (int i = 0; i < comp.length; i++) {
			c.add(size(comp[i], 555, 30));
			if(i<2) comp[i].setEnabled(false);
		}
		c.add(size(ta = new JTextArea(getone("select about from seller where no = "+NO)), 555, 230));
		ta.setLineWrap(true);
		ta.setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		for (int i = 0; i < tt.length; i++) {
			tt[i].setPlace(bb[i]);
		}
		
		for (int i = 0; i < save.length; i++) {
			c.add(size(save[i] = btn(aa[i], a->{
				if(a.getSource().equals(save[0])) {
					for (int j = 0; j < tt.length; j++) {
						if(tt[j].getText().isEmpty()) {
							errmsg("기본 정보는 모두 입력해야 합니다.");
							return;
						}
					}
					if(tt[2].getText().matches(".*\\D.*")) {
						errmsg("배달 수수료는 숫자로 입력해야 합니다.");
						return;
					}
					msg("정보가 수정되었습니다.");
					execute("update seller set pw = '"+tt[0].getText()+"', name = '"+tt[1].getText()+"', deliveryfee = '"+tt[2].getText()+"', category = '"+(combo.getSelectedIndex()+1)+"', about = '"+ta.getText()+"' where no = "+NO);
				}
				if(a.getSource().equals(save[1])) {
					FileDialog file = new FileDialog(this, "", FileDialog.LOAD);
					file.setDirectory(System.getProperty("user.home")+File.separator + "Documents");
					file.setFile("*.png");
					file.setVisible(true);
					File f = new File(file.getDirectory() + file.getFile());
					String path = "./지급자료/배경/"+NO+".png";
					if(!f.exists()) {
						return;
					}
					
					if(new File(path).exists()) {
						new File(path).delete();
					}
					Path from = Paths.get(f.getPath());
					Path to = Paths.get(path);
					try {
						Files.copy(from, to);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					msg("배경 사진이 등록되었습니다.");
				}
			}), 555, 30));
		}
		
		try {
			ResultSet rs = stmt.executeQuery("select * from category");
			while(rs.next()) {
				combo.addItem(rs.getString(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(new File("./지급자료/프로필/"+NO+".png").exists()) {
			w.add(img = new JLabel(img("프로필/"+NO+".png", 130, 130)));
		}else {
			w.add(img = new JLabel(img("프로필/upload.png", 130, 130)));
		}
		w.add(wc,"South");
		
		wc.add(label(NAME, 0));
		for (int i = 0; i < jb.length; i++) {
			wc.add(jb[i] = btn(str[i], a->{
				if(a.getSource().equals(jb[0])) {
					new Menu().addWindowListener(new be(this));
				}
				if(a.getSource().equals(jb[1])) {
					new OrderManage().addWindowListener(new be(this));
				}
				if(a.getSource().equals(jb[2])) {
					new Chart().addWindowListener(new be(this));
				}
			}));
		}
		
		try {
			ResultSet rs = stmt.executeQuery("select * from seller where no = "+NO);
			if(rs.next()) {
				for (int i = 0; i < comp.length - 3; i++) {
					((JTextField)comp[i]).setText(rs.getString(i+2));
				}
				((JTextField)comp[5]).setText(rs.getString("deliveryfee"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				FileDialog file = new FileDialog(Seller.this, "", FileDialog.LOAD);
				file.setDirectory(System.getProperty("user.home")+File.separator + "Documents");
				file.setFile("*.png");
				file.setVisible(true);
				File f = new File(file.getDirectory() + file.getFile());
				String path = "./지급자료/프로필/"+NO+".png";
				if(!f.exists()) {
					return;
				}
				
				if(new File(path).exists()) {
					new File(path).delete();
				}
				Path from = Paths.get(f.getPath());
				Path to = Paths.get(path);
				try {
					Files.copy(from, to);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				img.setIcon(img("프로필/"+NO+".png", 100, 100));
				msg("프로필 사진이 등록되었습니다.");
			}
		});
		
		emp(wc, 0, 5, 230, 5);
		size(wc, 0, 380);
		size(w, 200, 0);
		
		c.setOpaque(false);
		w.setOpaque(false);
		wc.setOpaque(false);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		NO = 1;
		NAME = "Lavender Bakery&Cafe";
		new Seller();
	}

}
