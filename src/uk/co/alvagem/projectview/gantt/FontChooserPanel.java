/*
 * FontChooserPanel.java
 * Created on 28-May-2004
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.gantt;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * FontChooserPanel
 * @author Bruce.Porteous
 *
 */
public class FontChooserPanel extends JPanel {

	/** The font sizes that can be selected. */
	public static final String[] SIZES = {"9", "10", "11", "12", "14", "16", "18",
										  "20", "22", "24", "28", "36", "48", "72"};

	/** The list of fonts. */
	private JList fontlist;

	/** The list of sizes. */
	private JList sizelist;

	/** The checkbox that indicates whether the font is bold. */
	private JCheckBox bold;

	/** The checkbox that indicates whether or not the font is italic. */
	private JCheckBox italic;


	/**
	 * 
	 */
	public FontChooserPanel(){
		super();
		init();
	}
	
	/**
	 *
	 * @param font  the initial font to display.
	 */
	public FontChooserPanel(Font font){
		super();
		init();
		setSelectedFont(font);
	}


	private void init(){
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = g.getAvailableFontFamilyNames();

		setLayout(new BorderLayout());
		JPanel right = new JPanel(new BorderLayout());

		JPanel fontPanel = new JPanel(new BorderLayout());
		fontPanel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), 
							"Font"));
		fontlist = new JList(fonts);
		JScrollPane fontpane = new JScrollPane(this.fontlist);
		fontpane.setBorder(BorderFactory.createEtchedBorder());
		fontPanel.add(fontpane);
		add(fontPanel);

		JPanel sizePanel = new JPanel(new BorderLayout());
		sizePanel.setBorder(BorderFactory.createTitledBorder(
							BorderFactory.createEtchedBorder(), 
							"Size"));
		this.sizelist = new JList(SIZES);
		JScrollPane sizepane = new JScrollPane(this.sizelist);
		sizepane.setBorder(BorderFactory.createEtchedBorder());
		sizePanel.add(sizepane);

		JPanel attributes = new JPanel(new GridLayout(1, 2));
		bold = new JCheckBox("Bold");
		italic = new JCheckBox("Italic");
		attributes.add(this.bold);
		attributes.add(this.italic);
		attributes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
							 "Style"));

		right.add(sizePanel, BorderLayout.CENTER);
		right.add(attributes, BorderLayout.SOUTH);

		add(right, BorderLayout.EAST);
	}
	


	/**
	 * Returns a Font object representing the selection in the panel.
	 *
	 * @return the font.
	 */
	public Font getSelectedFont() {
		return new Font(getSelectedName(), getSelectedStyle(), getSelectedSize());
	}

	/**
	 * Returns the selected name.
	 *
	 * @return the name.
	 */
	public String getSelectedName() {
		return (String) this.fontlist.getSelectedValue();
	}

	/**
	 * Returns the selected style.
	 *
	 * @return the style.
	 */
	public int getSelectedStyle() {
		if (this.bold.isSelected() && this.italic.isSelected()) {
			return Font.BOLD + Font.ITALIC;
		}
		if (this.bold.isSelected()) {
			return Font.BOLD;
		}
		if (this.italic.isSelected()) {
			return Font.ITALIC;
		}
		else {
			return Font.PLAIN;
		}
	}

	/**
	 * Returns the selected size.
	 *
	 * @return the size.
	 */
	public int getSelectedSize() {
		String selected = (String) this.sizelist.getSelectedValue();
		if (selected != null) {
			return Integer.parseInt(selected);
		}
		else {
			return 10;
		}
	}

	/**
	 * Initializes the contents of the dialog from the given font
	 * object.
	 *
	 * @param font the font from which to read the properties.
	 */
	public void setSelectedFont (Font font) {
		if (font == null) {
			throw new NullPointerException();
		}
		this.bold.setSelected(font.isBold());
		this.italic.setSelected(font.isItalic());

		String fontName = font.getName();
		fontlist.clearSelection();		
		fontlist.setSelectedValue(fontName,true);

		String fontSize = String.valueOf(font.getSize());
		sizelist.clearSelection();
		sizelist.setSelectedValue(fontSize,true);		
	}

}
