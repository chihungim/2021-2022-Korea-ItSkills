package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

public class Detail extends BaseFrame {

	JLabel img;
	File f;

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15),
			new JTextField(15) };

	JButton btn;

	public Detail(ArrayList<Object> company, boolean b) {
		super("기업상세정보", 300, 600);
		setLayout(new BorderLayout(5, 5));
		add(sz(img = new JLabel(), 200, 200), "North");
		img.setBorder(new LineBorder(Color.BLACK));
		add(c = new JPanel(new GridLayout(0, 1, 5, 5)));

		var cap = "기업명,대표자,주소,직종,직원수".split(",");

		for (int i = 0; i < txt.length; i++) {
			var temp = new JPanel(new BorderLayout(5, 5));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30), "West");
			temp.add(txt[i]);
			c.add(temp);
			txt[i].setText(company.get(i + 1) + "");
			txt[i].setEnabled(false);
		}

		img.setIcon(toIcon(company.get(6), 280, 180));

		add(btn = btn("닫기", a -> {
			if (a.getActionCommand().equals("닫기")) {
				dispose();
			} else {
				setValues("update company set c_ceo = ?, c_address = ?, c_category = ? where c_no = ?", txt[1].getText(),
						txt[2].getText(),
						String.join(",",
								Arrays.stream(txt[3].getText().split(","))
										.map(x -> Arrays.asList(category).indexOf(x) + "").toArray(String[]::new)),
						company.get(0));
				if (f != null) {
					try {
						setValues("update company set c_img = ? where c_no = ?", new FileInputStream(f), company.get(0));
						Files.copy(f.toPath(), new File("./datafiles/기업/" + company.get(1) + "1.jpg").toPath(),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

				imsg("수정이 완료되었습니다.");
				dispose();
			}
		}), "South");

		txt[3].setText(String.join(",",
				Arrays.stream(txt[3].getText().split(",")).map(a -> category[toInt(a)]).toArray(String[]::new)));

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));

		if (b) {
			btn.setText("수정");
			txt[1].setEnabled(true);
			txt[2].setEnabled(true);
			txt[3].addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					new Category(txt[3]).addWindowListener(new before(Detail.this));
				};
			});

			img.addMouseListener(new MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2) {
						var jfc = new JFileChooser("./datafiles/회원사진");
						jfc.setFileFilter(new FileFilter() {

							@Override
							public String getDescription() {
								return "JPG Images";
							}

							@Override
							public boolean accept(File f) {
								return f.getName().contains(".jpg");
							}
						});

						if (jfc.showOpenDialog(Detail.this) == 0) {
							f = jfc.getSelectedFile();
							img.setIcon(getIcon(f.getPath(), 280, 180));
						}

					}
				};
			});
		}

		setVisible(true);
	}

}
