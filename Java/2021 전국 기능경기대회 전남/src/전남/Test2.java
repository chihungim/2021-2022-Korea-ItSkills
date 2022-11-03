package Àü³²;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Test2 extends JFrame {

    public Test2() {

        String[] columnNames = {"Column"};
        Object[][] data = {
                {"A"},{"B"},{"C"},{"D"},{"E"},{"F"},
                {"A"},{"B"},{"C"},{"D"},{"E"},{"F"},
                {"A"},{"B"},{"C"},{"D"},{"E"},{"F"},
                {"A"},{"B"},{"C"},{"D"},{"E"},{"F"},
                {"A"},{"B"},{"C"},{"D"},{"E"},{"F"},
        };

        add(new JScrollPane(new JTable(data, columnNames)));
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (Exception e) {
                    // No Nimbus
                }
                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar:ScrollBarThumb[Enabled].backgroundPainter",
                        new FillPainter(Color.LIGHT_GRAY));
                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar:ScrollBarThumb[MouseOver].backgroundPainter",
                        new FillPainter(new Color(127, 169, 191)));
                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar:ScrollBarTrack[Enabled].backgroundPainter",
                        new FillPainter(new Color(190, 212, 223)));

                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar:\"ScrollBar.button\".size", 0);
                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar.decrementButtonGap", 0);
                UIManager.getLookAndFeelDefaults().put(
                        "ScrollBar.incrementButtonGap", 0);

                new Test2();
            }
        });
    }
    
    

}
class FillPainter implements Painter<JComponent> {
	
	private final Color color;
	
	public FillPainter(Color c) { color = c; }
	
	@Override
	public void paint(Graphics2D g, JComponent object, int width, int height) {
		g.setColor(color);
		g.fillRect(0, 0, width-1, height-1);
	}
	
}