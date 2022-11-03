package view;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

public class Movie extends BasePage {
	JPanel m = newJPanel(null);

	JCheckBox chk1[] = new JCheckBox[genre.length - 1];
	JCheckBox chk2[] = { new JCheckBox("상영중", true), new JCheckBox("미상영", true) };
	JTextField txt = new Hint("Movie Title").txt(80);

	JRadioButton radio[] = { new JRadioButton("예매순", true), new JRadioButton("별점순") };

	public Movie() {
		super("Movie");
		setLayout(new FlowLayout(FlowLayout.CENTER));
		m.setLayout(new BoxLayout(m, BoxLayout.Y_AXIS));
		add(m);
		m.add(n = newJPanel(null));
		n.setLayout(new BoxLayout(n, BoxLayout.Y_AXIS));
		m.add(c = newJPanel(new BorderLayout()));
		c.add(cn = newJPanel(new FlowLayout(FlowLayout.RIGHT)), "North");
		c.add(cc = newJPanel(new GridLayout(0, 5, 20, 20)));

		var cap = "장르,상영,제목".split(",");

		LayoutManager[] layouts = { new GridLayout(0, 9), new FlowLayout(FlowLayout.LEFT, 0, 0),
				new FlowLayout(FlowLayout.LEFT, 5, 5) };

		for (int i = 0; i < chk1.length; i++) {
			chk1[i] = new JCheckBox(genre[i + 1]);
			chk1[i].setName(i + 1 + "");
		}

		ArrayList<ArrayList<JComponent>> jc = new ArrayList<>();
		jc.add(new ArrayList<JComponent>(Arrays.asList(chk1)));
		jc.add(new ArrayList<JComponent>(Arrays.asList(chk2)));
		jc.add(new ArrayList<JComponent>(Arrays.asList(txt, rndBtn("검색", a -> search()))));

		for (int i = 0; i < cap.length; i++) {
			var temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
			temp.add(sz(lbl(cap[i], JLabel.LEFT, 0, 15), 60, 30));
			var tempc = new JPanel(layouts[i]);
			temp.add(tempc);

			for (var j : jc.get(i))
				tempc.add(j);
			n.add(temp);
		}

		cn.setBorder(new MatteBorder(2, 0, 0, 0, Color.BLACK));
		var bg = new ButtonGroup();
		for (var r : radio) {
			cn.add(r);
			bg.add(r);
		}

		search();
	}

	void search() {
		cc.removeAll();
		String avg = "(select ifnull(round(avg(c_rate),1),0) from comment c where m.m_no = c.m_no) `avg`";
		String cnt = "(select count(*) from reservation r where r.m_no = m.m_no) `cnt`";

		String order = "order by `cnt` desc, avg desc, m.m_no asc";
		if (radio[1].isSelected())
			order = "order by `avg` desc,`cnt` desc, m.m_no asc";
		var onscreen = getrows("select m_no from theater").stream()
				.flatMap(a -> Arrays.stream(a.get(0).toString().split("\\."))).collect(Collectors.toList());
		var g = Arrays.stream(chk1).filter(a -> a.isSelected()).map(i -> i.getName()).collect(Collectors.toList());

		var rs = getrows("select m.*, " + avg + ", " + cnt + " from movie m where m_title like ? " + order,
				"%" + txt.getText() + "%").stream().filter(x -> {
					var flag = (chk2[0].isSelected() && chk2[1].isSelected());
					if (chk2[0].isSelected()) {
						flag = onscreen.contains(x.get(0) + "");
					} else {
						flag = !onscreen.contains(x.get(0) + "");
					}
					var flag2 = g.isEmpty() ? true
							: Arrays.stream(x.get(3).toString().split("\\.")).anyMatch(g::contains);
					return flag2 && flag;
				}).collect(Collectors.toList());

		if (rs.isEmpty()) {
			Arrays.stream(chk1).forEach(x -> x.setSelected(false));
			Arrays.stream(chk2).forEach(x -> x.setSelected(true));
			txt.setText("");
			radio[0].setSelected(true);
			emsg("검색결과가 없습니다.");
			search();
			return;
		} else {
			for (var r : rs) {
				var temp = new JPanel(new BorderLayout(5, 5));
				var header = lbl("No." + (rs.indexOf(r) + 1), JLabel.CENTER, 15);
				var temps = new JPanel(new GridLayout(0, 1));

				f(header, Color.WHITE);
				b(header, rs.indexOf(r) + 1 <= 5 ? red : Color.DARK_GRAY);
				temp.add(header, "North");
				var poster = new JLabel(getIcon("./지급자료/image/movie/" + r.get(0) + ".jpg", 200, 300));
				temp.add(poster);
				temp.add(temps, "South");

				temps.add(lbl(r.get(1) + "", JLabel.LEFT, 15));
				temps.add(lbl(Arrays.stream(r.get(3).toString().split("\\.")).map(a -> genre[cint(a)])
						.collect(Collectors.joining(",")), JLabel.LEFT));

				String star = "<html><font color = 'yellow'>";

				for (int i = 0; i < 5; i++) {
					if (i > cdbl(r.get(7)) - 1)
						star += "<font color = 'rgb(200,200,200)'>";
					star += "★";
				}

				evt(poster, a -> new MovieInformation(r).setVisible(true));

				star += "<font color = 'black'>" + r.get(7);
				temps.add(lbl(star, JLabel.LEFT));

				poster.setLayout(new FlowLayout(FlowLayout.LEFT));
				poster.add(ageLimit(r.get(5) + ""));
				cc.add(sz(temp, 200, 350));
			}
		}

		revalidate();
		repaint();

	}
}
