

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Mainf extends Basedialog {
	JPanel n = new JPanel(new FlowLayout(2)), c = new JPanel(null), s = new JPanel(new BorderLayout()), sn = new JPanel(new GridLayout(1, 0, 10, 0));
	JLabel jl[] = {
			label("로그아웃", 0),
			label("마이페이지", 0)
	};
	JLabel img[] = new JLabel[5];
	boolean chk = true;
	cate cate[] = new cate[10];
	String str[] = "모두,베이커리,디저트,편의점,중식,일식,멕시칸,아메리칸,한식,알코올".split(",");
	
	public Mainf() {
		super("기능 배달", 1200, 600);
		
		
		add(n,"North");
		add(c);
		add(s,"South");
		
		for (int i = 0; i < jl.length; i++) {
			n.add(jl[i]);
			jl[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getSource().equals(jl[0])) {
						NO = -1;
						NAME = "";
						dispose();
					}
					if(e.getSource().equals(jl[1])) {
						new Mypage().addWindowListener(new be(Mainf.this));
					}
				}
			});
		}
		
		try {
			ResultSet rs = stmt.executeQuery("SELECT s.no,s.name, c.name, count(seller) as cnt FROM receipt r, seller s, category c where r.seller = s.no and s.category = c.no group by seller order by cnt desc, s.no asc limit 5");
			int i = 1;
			while(rs.next()) {
				String name = rs.getString(2);
				String cate = rs.getString(3);
				int no = rs.getInt(1);
				int k = i;
				addCom(c, img[i - 1] = new JLabel() {
					Font f = new Font("맑은 고딕",Font.BOLD, 40);
					Font f1 = new Font("맑은 고딕",Font.BOLD, 22);
					Font f2 = new Font("맑은 고딕",Font.PLAIN, 13);
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						Graphics2D g2 = (Graphics2D) g;
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						
						
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
						g2.drawImage(img("배경/"+no+".png").getImage(), 0, 0, 1154, 300, this);
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
						GradientPaint gp = new GradientPaint(0, 0, Color.RED, 654, 0, Color.BLUE);
						g2.setPaint(gp);
						g2.fillRect(0, 0, 1154, 300);
						
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
						g2.setColor(Color.WHITE);
						g2.setFont(f);
						g2.drawString("#" + k, 100, 150);
						g2.setFont(f1);
						g2.drawString(name, 100, 180);
						g2.setFont(f2);
						g2.drawString(cate, 100, 200);
					}
				}, (i-1)*1160, 0, 1154, 300);
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		s.add(labelB("카테고리별로 탐색", 2, 15),"North");
		s.add(sn);
		
		
		emp((JPanel)getContentPane(), 15, 15, 15, 15);
		
		size(s, 0, 200);
		
		new Thread(()->{
			while(chk) {
				for (int i = 0; i < img.length; i++) {
					int x = img[i].getX();
					img[i].setLocation(x -= 10, 0);
					if(x == -1170) {
						img[i].setLocation(1160 * 4, 0);
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}).start();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				chk = false;
			}
		});
		
		sn.add(cate[0] = new cate(str[0], 0));
		
		cate[0].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (int i = 0; i < 10; i++) {
					cate[i].b();
				}
				
				if(e.getClickCount() == 2) {
					dispose();	
					new Search(0).addWindowListener(new be(Mainf.this));
				}
				((cate)e.getSource()).click();
				repaint();
				revalidate();
			}
		});
		
		for (int i = 1; i < 10; i++) {
			sn.add(cate[i] = new cate(str[i], i));
			int k = i;
			cate[i].addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					for (int j = 0; j < 10; j++) {
						cate[j].b();
					}
					
					if(e.getClickCount() == 2) {
						dispose();	
						new Search(k).addWindowListener(new be(Mainf.this));
					}
					((cate)e.getSource()).click();
					repaint();
					revalidate();
				}
			});
		}
		
		n.setOpaque(false);
		c.setOpaque(false);
		s.setOpaque(false);
		sn.setOpaque(false);
		
		setVisible(true);
	}
	
	class cate extends JLabel {
		Color col = Color.LIGHT_GRAY;
		String name;
		image img;
		int idx;
		
		public cate(String cate, int idx) {
			setLayout(null);
			name = cate;
			this.idx = idx;
			
			add(img = new image("카테고리/"+(idx == 0 ? "all":idx)+".png"));
			addCom(img, 5, 10, 100, 100);
		}
		
		void b() {
			col = Color.LIGHT_GRAY;
		}
		
		void click() {
			col = Color.ORANGE;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(col);
			
			RoundRectangle2D rec = new RoundRectangle2D.Float(3f, 3f, getWidth() - 3, getHeight() - 3, 100, 100);
			
			g2.fill(rec);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("맑은 고딕", Font.BOLD,12));
			FontMetrics f = getFontMetrics(g2.getFont());
			g2.setColor(Color.BLACK);
			int w = f.stringWidth(name);
			g2.drawString(name, (getWidth() - w) / 2, 140);
			
			g2.setColor(Color.WHITE);
			g2.fill(new RoundRectangle2D.Float(10, 10, 90, 90, 90, 90));
			g2.drawImage(img("카테고리/"+(idx == 0 ? "all":idx)+".png").getImage(), 20, 20, 70, 70, this);
		}
	}
	
	class image extends JLabel {
		
		public image(String path) {
		}
	}
	
	public static void main(String[] args) {
		NO = 1;
		TYPE = "user";
		new Mainf();
	}
 
}
