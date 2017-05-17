package com.github.easai.utils.TreeEditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class Plantae extends JApplet implements ActionListener, KeyListener {
	TreeEditor treeEditor = new TreeEditor("Plantae.tre");
	JButton classButton = new JButton("Classes");
	JButton subclass = new JButton("Subclasses");
	JButton order = new JButton("Orders");
	JButton family = new JButton("Families");

	JTextField searchWord = new JTextField(15);
	JButton search = new JButton("Search");
	JButton katakana = new JButton("Katakana");
	JButton clear = new JButton("Clear");
	JPopupMenu popup = new JPopupMenu();
	JMenuItem copy = new JMenuItem("Copy");

	void popupInit() {
		copy.addActionListener(this);
		popup.add(copy);
	}

	public void init() {
		treeEditor.applet = this;
		treeEditor.initPanel();
		treeEditor.tree.setEditable(false);
		JPanel control = new JPanel();
		control.setLayout(new FlowLayout());
		control.add(classButton);
		control.add(subclass);
		control.add(order);
		control.add(family);
		control.add(searchWord);
		control.add(search);
		control.add(katakana);
		control.add(clear);
		Container pane = getContentPane();

		pane.setLayout(new BorderLayout());
		pane.add(control, BorderLayout.NORTH);
		pane.add(treeEditor.scroll, BorderLayout.CENTER);

		classButton.addActionListener(this);
		subclass.addActionListener(this);
		order.addActionListener(this);
		family.addActionListener(this);
		search.addActionListener(this);
		katakana.addActionListener(this);
		clear.addActionListener(this);

		searchWord.addKeyListener(this);
		treeEditor.tree.setEditable(true);
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_ENTER:
			String word = searchWord.getText();
			if (!word.equals(""))
				treeEditor.search(word);
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (popup != null)
			popup.setVisible(false);
		Object source = e.getSource();
		if (source == classButton) {
			treeEditor.collapseAll();
		} else if (source == subclass) {
			treeEditor.collapseDepth(3);
		} else if (source == order) {
			treeEditor.collapseDepth(4);
		} else if (source == family) {
			treeEditor.collapseDepth(5);
		} else if (source == search) {
			String word = searchWord.getText();
			treeEditor.search(word);
		} else if (source == katakana) {
			String word = searchWord.getText();
			String katakana = treeEditor.latinToKatakana(word);
			searchWord.setText(katakana);
		} else if (source == clear) {
			searchWord.setText("");
		}
	}
}
