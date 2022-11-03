package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

public class Sign extends BaseFrame {

	{
		local[0] = "";
	}

	JTextField txt[] = { new JTextField(15), new JTextField(15), new JTextField(15), new JTextField(15),
			new JTextField(10), new JTextField(18), };

	JComboBox combo[] = { new JComboBox<String>(
			"naver.com,outlook.com,daum.com,gmail.com,nate.com,kebi.com,yahoo.com,korea.com,empal.com,hanmail.net"
					.split(",")),
			new JComboBox<String>(graduate), new JComboBox<String>(local) };

	JRadioButton rbtn[] = { new JRadioButton("��"), new JRadioButton("��") };

	JComponent jc[][] = { { txt[0] }, { txt[1] }, { txt[2] }, { txt[3] }, { txt[4], lbl("@", 0), combo[0] },
			{ rbtn[0], rbtn[1] }, { combo[1] }, { combo[2], txt[5] } };

	JLabel img;
	File f;

	public Sign() {
		super("ȸ������", 600, 500);

		add(c = new JPanel(new BorderLayout(5, 5)));
		c.add(cc = new JPanel(new GridLayout(0, 1)));
		c.add(ce = new JPanel(new BorderLayout()), "East");
		add(s = new JPanel(new FlowLayout(FlowLayout.RIGHT)), "South");

		var cap = "�̸�,���̵�,��й�ȣ,�������,�̸���,����,�����з�,�ּ�".split(",");

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT), 60, 30));
			for (int j = 0; j < jc[i].length; j++) {
				temp.add(jc[i][j]);
			}
			cc.add(temp);
		}

		var bg = new ButtonGroup();
		for (var r : rbtn)
			bg.add(r);

		rbtn[0].setSelected(true);

		combo[1].insertItemAt("", 0);
		combo[1].setSelectedIndex(0);
		combo[1].removeItemAt(combo[1].getItemCount() - 1);

		combo[2].addItemListener(i -> {
			txt[5].setEnabled(!i.getItem().toString().isEmpty());
			txt[5].setText("");
		});

		txt[5].setEnabled(false);

		ce.add(sz(img = new JLabel(), 200, 200));
		ce.setBorder(new EmptyBorder(5, 5, 200, 5));
		c.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "ȸ������"));

		img.setBorder(new LineBorder(Color.BLACK));

		s.add(btn("����", a -> {
			for (var t : txt) {
				if (t.getText().isEmpty()) {
					emsg("��ĭ�� �����մϴ�.");
					return;
				}
			}

			for (var c : combo) {
				if (c.getSelectedItem().toString().isEmpty()) {
					emsg("��ĭ�� �����մϴ�.");
					return;
				}
			}

			if (img.getIcon() == null) {
				emsg("��ĭ�� �����մϴ�.");
				return;
			}

			if (getOne("select * from user where u_id = ?", txt[1].getText()) != null) {
				emsg("�̹� �����ϴ� ���̵� �Դϴ�.");
				txt[1].setText("");
				txt[1].requestFocus();
				return;
			}

			var pw = txt[2].getText();

			if (!(pw.matches(".*[0-9].*") && pw.matches(".*[a-zA-Z].*") && pw.matches(".*[!@#$].*"))
					|| pw.length() < 4) {
				emsg("��й�ȣ ������ ��ġ���� �ʽ��ϴ�.");
				return;
			}

			try {
				var d = LocalDate.parse(txt[3].getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				if (d.isAfter(LocalDate.now())) {
					emsg("������� ������ ���� �ʽ��ϴ�.");
					txt[3].setText("");
					txt[3].requestFocus();
					return;
				}
			} catch (Exception e1) {
				emsg("������� ������ ���� �ʽ��ϴ�.");
				txt[3].setText("");
				txt[3].requestFocus();
				return;
			}

			try {
				setValues("insert user values(0,?,?,?,?,?,?,?,?,?)", txt[0].getText(), txt[1].getText(), txt[2].getText(),
						txt[3].getText(), txt[4].getText() + "@" + combo[0].getSelectedItem(),
						rbtn[0].isSelected() ? 1 : 2, combo[1].getSelectedIndex() - 1,
						combo[2].getSelectedItem() + " " + txt[5].getText(), new FileInputStream(f));
				Files.copy(f.toPath(),
						new File("./datafiles/ȸ������/" + getOne("select count(*) from user") + ".jpg").toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			imsg("ȸ�������� �Ϸ�Ǿ����ϴ�.");
			dispose();
		}));

		img.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					var jfc = new JFileChooser("./datafiles/ȸ������");
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

					if (jfc.showOpenDialog(Sign.this) == 0) {
						f = jfc.getSelectedFile();
						img.setIcon(getIcon(f.getPath(), 200, 180));
					}
				}
			}
		});

		((JPanel) getContentPane()).setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
	}

	public static void main(String[] args) {
		new Sign();
	}
}
