package 전남;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TEst extends Baseframe {
	BufferedImage img, img2;
	JLabel imgLbl[] = new JLabel[10];
	String names[] = "강원도,경기도,경상남도,경상북도,전라남도,전라북도,제주도,충청남도,충청북도".split(",");
	int point[][] = { { 161, 0 }, { 45, 49 }, { 230, 435 }, { 263, 227 }, { 0, 506 }, { 79, 396 }, { 88, 768 },
			{ 37, 243 }, { 189, 218 } };
	Color col[] = { Color.red, Color.yellow, Color.green, Color.blue, Color.cyan, Color.magenta, Color.black,
			Color.orange };
	JPanel n, c, w;

	public TEst() {
		super("지역별 예약현황", 1250, 1000);
		setLayout(new BorderLayout());

		add(n = new JPanel(new GridLayout(0, 1)), "North");
		add(w = new JPanel(null), "West");
		add(c = new JPanel(new BorderLayout()));

		n.add(label("지역별 예약 현황", 0, 30));
		n.add(label("C H A R T", 0, 20));
		size(n, 1, 70);
		size(w, 500, 500);

		try {
			for (int i = 0; i < names.length; i++) {
				img = ImageIO.read(new File("./Datafiles/지도/" + names[i] + ".png"));
				img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
				
				for (int x = 0; x < img.getWidth(); x++) {
					for (int y = 0; y < img.getHeight(); y++) {
						Color color = new Color(img.getRGB(x, y));
						int r = color.getRed();
						int g = color.getGreen();
						int b = color.getBlue();
						if (color.getRGB() != -16777216) {
							img2.setRGB(x, y, new Color(r, g, b).getRGB());
						}
					}
				}
				
				imgLbl[i] = new JLabel(
						new ImageIcon(img2.getScaledInstance(img2.getWidth(), img2.getHeight(), Image.SCALE_DEFAULT)));
				imgLbl[i].setName(names[i]);
				
				w.add(imgLbl[i]);
				imgLbl[i].setBounds(point[i][0], point[i][1], img2.getWidth(), img2.getHeight());
				int j = i;
				imgLbl[i].addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						colorChange(j);
						
						String cad = ((JLabel) e.getSource()).getName();
						switch(cad){
						case "강원도":
							cad = "강원";
							break;
						case "경기도":
							cad = "경기";
							break;
						case "경상남도":
							cad = "경남";
							break;
						case "경상북도":
							cad = "경북";
							break;
						case "전라남도":
							cad = "전남";
							break;
						case "전라북도":
							cad = "전북";
							break;
						case "제주도":
							cad = "제주";
							break;
						case "충청남도":
							cad = "충남";
							break;
						case "충청북도":
							cad = "충북";
							break;
						}
						c.removeAll();
						repaint();
						revalidate();
					}

				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		setVisible(true);
	}
	
	private void colorChange(int i) {
		
		for (int j = 0; j < names.length; j++) {
			try {
				img = ImageIO.read(new File("./Datafiles/지도/"+names[j]+".png"));
				img2 = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					Color color = new Color(img.getRGB(x, y));
					int r = color.getRed();
					int g = color.getGreen();
					int b = color.getBlue();
					if (color.getRGB() != -16777216) {
						if(j == i)
							img2.setRGB(x, y, new Color(r, 0, 0).getRGB());
						else
							img2.setRGB(x, y, new Color(r, g, b).getRGB());
					}
				}
			}
			
			imgLbl[j].setIcon(new ImageIcon(img2.getScaledInstance(img2.getWidth(), img2.getHeight(), Image.SCALE_DEFAULT)));
		}
		
		repaint();
		revalidate();
	}

	public static void main(String[] args) {
		new TEst();
	}

}
