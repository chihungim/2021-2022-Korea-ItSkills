

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Purchase extends Basedialog {
	JPanel n = new JPanel(new BorderLayout()), nc = new JPanel(new BorderLayout()), c = new JPanel(new FlowLayout(0, 5, 5));
	JScrollPane scr;
	int totw = 0, cpw = 0, cidx = -1, cno =0;
	ArrayList<card> card = new ArrayList<card>();
	GradientPaint blue = new GradientPaint(0, 0, new Color(49, 132, 237), 290, 130, new Color(89, 197, 241));
	GradientPaint red = new GradientPaint(0, 0, new Color(255, 68, 108), 290, 130, new Color(255, 143, 103));
	
//	public Purchase(int tot, Shop sh) {
	public Purchase(int tot) {
		super("결제", 350, 450);
		
		add(n,"North");
		n.add(nc);
		
		n.add(labelB(order.size() != 1 ? order.get(0).get(0).toString()+"의 결제건 외"+(order.size() - 1)+"건":order.get(0).get(0).toString(), 2, 15), "North");
		nc.add(labelB("총 금액", 2, 15),"West");
		nc.add(labelP(format.format(tot)+"원", 4, 12),"East");
		
		add(scr = new JScrollPane(c));
		try {
			ResultSet rs = stmt.executeQuery("select * from playment where user = "+NO);
			if(!rs.next()) {
				c.add(label("<html><font size = \"5\"; color = \"red\"; face = \"맑은 고딕\"><b>결제수단이 없습니다.</b></html>", 0));
			}
			rs.previous();
			while(rs.next()) {
				card c = new card(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(5));
				card.add(c);
				this.c.add(size(c, 290, 120));
				totw += 128;
				
				c.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						card.forEach(a->a.col = blue);
						cidx = card.indexOf((card)e.getSource());
						((card)e.getSource()).col = red;
						cpw = ((card)e.getSource()).pw;
						cno = ((card)e.getSource()).no;
						repaint();
						revalidate();
					}
				});
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		n.setOpaque(false);
		c.setBackground(Color.WHITE);
		nc.setOpaque(false);
		scr.setOpaque(false);
		
		add(btn("선택한 결제 수단으로 결제하기", a->{
			if(cidx == -1) {
				errmsg("결제할 카드를 선택해야 합니다.");
				return;
			}
			
			int yn = JOptionPane.showConfirmDialog(null, "정말로 "+format.format(tot)+"원을 결제하시겠습니까?","메시지",JOptionPane.YES_NO_OPTION);
			if(yn == JOptionPane.YES_OPTION) {
				JPanel c = new JPanel(new GridLayout(0, 1));
				JTextField tt = new JTextField(10);
				c.add(label("결제 비밀번호를 입력해주세요.", 2));
				c.add(tt);
				
				int asd = JOptionPane.showConfirmDialog(null, "결제 비밀번호를 입력해주세요.", "메시지",JOptionPane.OK_CANCEL_OPTION);
				
				if(asd == JOptionPane.OK_OPTION) {
					if(!tt.getText().equals(cpw+"")) {
						errmsg("비밀번호가 일치하지 않습니다.");
						return;
					}
					msg("주문이 완료되었습니다.");
					isPur = true;
					
//					execute("insert into receipt values(0"+tot+",'"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"',"+sh.sno+","+NO+","+cno+",0)");
					execute("insert into receipt values(0"+tot+",'"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"',"+shop.sno+","+NO+","+cno+",0)");
					dispose();
				}else {
					msg("사용자가 결제를 취소했습니다.");
					return;
				}
			}else {
				msg("사용자가 결제를 취소했습니다.");
				return;
			}
		}),"South");
		
		scr.setBorder(BorderFactory.createEmptyBorder());
		size(c, 260, totw);
		
		emp((JPanel)getContentPane(), 5, 5, 5, 5);
		
		setVisible(true);
	}
	
	class card extends JLabel {
		GradientPaint col;
		int pw, no;
		StringBuffer num;
		String name;
		
		public card(int no, String name, String num, int pw) {
			setOpaque(true);
			this.pw = pw;
			this.no= no;
			this.name = name;
			col =blue;
			this.num = new StringBuffer(num);
			this.num.insert(4, "-");
			this.num.insert(9, "-");
			this.num.insert(14, "-");
			
			setForeground(Color.WHITE);
			setText("<html><font face = \"맑은 고딕\";size = \"5\"; color = \"white\";><b><br></font><font face = \"맑은 고딕\"; size = \"4\"; color = \"white\";>"+this.num+"<br>"+NAME);
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setPaint(col);
			
			RoundRectangle2D rec = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20f, 20f);
			
			g2.fill(rec);
			g2.setColor(Color.WHITE);
			
			g2.setFont(new Font("맑은 고딕", Font.BOLD, 15));
			g2.drawString(name, 30, 45);
			
			g2.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
			g2.drawString(num.toString(), 30, 65);
			
			g2.drawString(NAME, 30, 85);
		}
	}

}
