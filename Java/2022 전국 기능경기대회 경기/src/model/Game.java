package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import view.Base;

public class Game implements Base {
	public Object g_no, g_genre, g_name, g_age, g_explan, g_price, g_sale, g_gd, g_ox, g_img;

	public Game(ArrayList<Object> game) {
		g_no = game.get(0);
		g_genre = game.get(1);
		g_name = game.get(2);
		g_age = game.get(3);
		g_explan = game.get(4);
		g_price = game.get(5);
		g_sale = game.get(6);
		g_gd = game.get(7);
		g_ox = game.get(8);
		g_img = game.get(9);
	}

	public ImageIcon g_img(int w, int h) {
		return getBlob(g_img, w, h);
	}

	public String displayAge() {
		return Base.g_age[cint(g_age)];
	}

	public boolean validate(User u) {
		var birth = LocalDate.parse(u.u_birth + "", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		var now = LocalDate.now();
		var temp = LocalDate.of(now.getYear(), birth.getMonthValue(), birth.getDayOfMonth());
		var age = now.getYear() - birth.getYear() + (temp.isBefore(now) ? 0 : -1);
		return age >= Game.age[cint(g_age)];
	}

	public List<String> g_genre() {
		return Arrays.asList(g_genre.toString().split(","));
	}                                      

	public String displayGenre() {
		return g_genre().stream().map(a -> Base.g_genre[cint(a)]).collect(Collectors.joining(","));
	}

	public int 인기도() {
		return getrows("select * from library where g_no = ?", g_no).size();
	}

	public String displayPrice() {
		var sale = cint(g_sale);
		var pr = sale / 100.0;
		if (cint(g_price) == 0) {
			return "무료";
		} else if (sale == 0) {
			return decformat(g_price) + "원";
		} else {
			return decformat(g_price) + "원 -> " + decformat((int) (cint(g_price) * pr)) + "원(" + g_sale + "% 할인중) 대상:"
					+ Base.g_gd[cint(g_gd)] + "↑";
		}
	}

	public String avg() {
		return String.format("%.1f",
				cdbl(getrows("select round(avg(r_score),1) from review where g_no = ?", g_no).get(0).get(0))) + "점";
	}

	public int ownPrice(User u) {
		var sale = cint(g_sale);
		var pr = sale / 100.0;

		if (sale == 0) {
			return cint(g_price);
		} else {
			if (u.gd() >= cint(g_gd)) {
				return (int) (cint(g_price) * pr);
			} else {
				return cint(g_price);
			}
		}
	}
}
