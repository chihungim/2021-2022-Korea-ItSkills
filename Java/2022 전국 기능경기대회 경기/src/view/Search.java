package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import model.Game;

public class Search extends BasePage {

	JComboBox combo[] = { new JComboBox<String>(",¿Œ±‚,«“¿Œ¡ﬂ,π´∑·".split(",")), new JComboBox<String>(g_genre), };

	JTextField txt = new JTextField(15);
	JLabel icon = new JLabel(getIcon("./datafiles/±‚∫ªªÁ¡¯/9.png", 30, 30));

	HashMap<String, String> map = new HashMap<>();

	{
		map.put("§°", "∞°-Éâ");
		map.put("§§", "≥™-à¢");
		map.put("§ß", "¥Ÿ-ãL");
		map.put("§©", "∂Û-ê°");
		map.put("§±", "∏∂-ìJ");
		map.put("§≤", "πŸ-ïΩ");
		map.put("§µ", "ªÁ-öÔ");
		map.put("§∑", "æ∆-üÁ");
		map.put("§∏", "¿⁄-£†");
		map.put("§∫", "¬˜-ØÜ");
		map.put("§ª", "ƒ´-µi");
		map.put("§º", "≈∏-ªM");
		map.put("§Ω", "∆ƒ-¿ò");
		map.put("§æ", "«œ-∆R");
	}

	public Search() {
		super("∞Àªˆ");
		add(n = newJPanel(new FlowLayout(FlowLayout.CENTER)), "North");
		n.add(wlbl("±∏∫–", 0));
		n.add(combo[0]);
		n.add(wlbl("¿Â∏£", 0));
		n.add(combo[1]);
		n.add(wlbl("∞Àªˆ", 0));
		n.add(txt);
		n.add(icon);

		n.add(btn("µÓ∑œ«œ±‚", a -> {
			new GameInfo();
		})).setVisible(user == null);

		txt.addKeyListener(new KeyAdapter() {
			JPopupMenu pop = new JPopupMenu();

			@Override
			public void keyReleased(KeyEvent e) {
				if (txt.getText().isEmpty()) {
					pop.setVisible(false);
					return;
				}
				pop.removeAll();
				var games = getGames();
				for (var g : games) {
					var p = new JPanel(new BorderLayout());
					p.add(new JLabel(g.g_img(80, 80)), "West");
					p.add(new JLabel(g.g_name + "", JLabel.LEFT));
					pop.add(p);
					evt(p, a -> {
						if (user != null) {
							if (!g.validate(user)) {
								emsg("ø¨∑…¿Ã ∏¬¡ˆ æ Ω¿¥œ¥Ÿ.");
								return;
							}
							new GamePage(g);
						} else {
							new GameInfo();
						}
						pop.setVisible(false);
					});
				}
				pop.pack();
				pop.setFocusable(false);
				if (pop.getComponentCount() > 0)
					pop.show(txt, 0, txt.getHeight() + 10);
			}
		});
		evt(icon, a -> search());
		add(new JScrollPane(c = new JPanel(new GridLayout(0, 1))));
		c.setBackground(back);
		search();
		revalidate();
		repaint();
	}

	void search() {
		c.removeAll();

		for (var g : getGames()) {
			var tmp = newJPanel(new BorderLayout(5, 5));
			tmp.add(new JLabel(g.g_img(200, 200)), "West");
			var tmpc = newJPanel(new GridLayout(0, 1));
			tmp.add(tmpc);
			tmpc.add(wlbl("∞‘¿”∏Ì:" + g.g_name, JLabel.LEFT));
			tmpc.add(wlbl("¿Â∏£:" + g.displayGenre(), JLabel.LEFT));
			tmpc.add(wlbl("ø¨∑…:" + g.displayAge(), JLabel.LEFT));
			tmpc.add(wlbl("∆Ú¡°:" + g.avg(), JLabel.LEFT));
			tmpc.add(wlbl("∞°∞›:" + g.displayPrice(), JLabel.LEFT));
			c.add(tmp);
			tmp.setBorder(new LineBorder(Color.BLACK));
			evt(tmp, a -> {
				if (user != null) {
					if (!g.validate(user)) {
						emsg("ø¨∑…¿Ã ∏¬¡ˆ æ Ω¿¥œ¥Ÿ.");
						return;
					}
					new GamePage(g);
				} else {
					new GameInfo();
				}
			});
		}

		while (c.getComponentCount() < 3) {
			c.add(new JLabel());
		}
		revalidate();
		repaint();
	}

	public static void main(String[] args) {
		µπˆ±◊("2");
		new Search();
		mf.setVisible(true);

	}

	List<Game> getGames() {
		var keyword = Arrays.stream(txt.getText().split("")).map(a -> map.get(a) != null ? "[" + map.get(a) + "]" : a)
				.collect(Collectors.joining());

		var games = getrows("select * from game").stream().map(a -> new Game(a)).collect(Collectors.toList());

		games = games.stream().filter(x -> {
			var flag1 = keyword.isEmpty() ? true : x.g_name.toString().matches(keyword + ".*");
			var flag2 = true;
			if (combo[0].getSelectedIndex() == 2) {
				flag2 = cint(x.g_sale) != 0;
			} else if (combo[0].getSelectedIndex() == 3) {
				flag2 = cint(x.g_price) == 0;
			}
			var flag3 = true;
			if (!combo[1].getSelectedItem().toString().isEmpty())
				flag3 = x.g_genre().contains(combo[1].getSelectedIndex() + "");
			var flag4 = true;
			var flag5 = true;
			if (user != null) {
				if (!user.u_filter().contains("0")) {
					flag4 = !x.g_genre().stream().anyMatch(user.u_filter()::contains);
				}
				if (user.u_filter().contains("12"))
					flag5 = x.validate(user);
			}

			return flag1 && flag2 && flag3 && flag4 && flag5;
		}).collect(Collectors.toList());

		Comparator<Game> order1 = (o1, o2) -> Integer.compare(o2.¿Œ±‚µµ(), o1.¿Œ±‚µµ());
		var order = order1.thenComparing((o1, o2) -> Integer.compare(cint(o1.g_no), cint(o2.g_no)));
		if (combo[0].getSelectedIndex() == 1) {
			games = games.stream().sorted(order).collect(Collectors.toList()).subList(0, Math.min(5, games.size()));
		}
		return games;
	}

}
