package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Admin extends BaseFrame {
	public Admin() {
		super("관리자", 500, 500);
		add(c = new JPanel(new GridLayout(5, 5, 5, 5)));
		add(s = new JPanel(new FlowLayout(1)), "South");

		for (var bc : "채용 정보,지원자 목록,공고 등록,지원자 분석,닫기".split(",")) {
			s.add(btn(bc, a -> {
				if (a.getActionCommand().equals("채용 정보")) {
					new AdminJobs().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("지원자 목록")) {
					new Applicant().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("공고 등록")) {
					new Post().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("지원자 분석")) {
					new Chart().addWindowListener(new before(this));
				} else if (a.getActionCommand().equals("닫기")) {
					dispose();
				}
			}));

		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				load();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				for (var w : getWindows()) {
					w.setVisible(w instanceof Main);
				}
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Admin();
	}

	void load() {
		c.removeAll();
		for (var rs : getValues("Select * from company")) {
			var img = new JLabel(toIcon(rs.get(6), 80, 80)) {
				float opacity = 0.2f;

				@Override
				protected void paintComponent(Graphics g) {
					var g2 = (Graphics2D) g;
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
					super.paintComponent(g);
				}
			};

			img.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					new Detail(rs, true).addWindowListener(new before(Admin.this));
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					img.opacity = 1f;
					img.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					img.opacity = 0.2f;
					img.repaint();
				}
			});

			img.setToolTipText(rs.get(1) + "");

			c.add(img);
			img.setBorder(new LineBorder(Color.BLACK));
		}
		revalidate();
		repaint();
	}
}
