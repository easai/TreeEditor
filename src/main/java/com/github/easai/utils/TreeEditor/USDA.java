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

public class USDA extends JApplet implements ActionListener, KeyListener {
	TreeEditor treeEditor = new TreeEditor("USDA.tre");
	JButton first = new JButton("Classes");
	JButton second = new JButton("Subclasses");
	JButton third = new JButton("Orders");
	JButton fourth = new JButton("Families");
	JButton fifth = new JButton("Genera");

	JTextField searchWord = new JTextField(15);
	JButton search = new JButton("Search");
	JButton katakana = new JButton("Katakana");
	JButton clear = new JButton("Clear");
	JPopupMenu popup = new JPopupMenu();
	JMenuItem copy = new JMenuItem("Copy");

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
		control.add(first);
		control.add(second);
		control.add(third);
		control.add(fourth);
		control.add(fifth);
		control.add(searchWord);
		control.add(search);
		// control.add(katakana);
		control.add(clear);
		Container pane = getContentPane();

		pane.setLayout(new BorderLayout());
		pane.add(control, BorderLayout.NORTH);
		pane.add(treeEditor.scroll, BorderLayout.CENTER);

		first.addActionListener(this);
		second.addActionListener(this);
		third.addActionListener(this);
		fourth.addActionListener(this);
		fifth.addActionListener(this);
		search.addActionListener(this);
		katakana.addActionListener(this);
		clear.addActionListener(this);

		searchWord.addKeyListener(this);
		treeEditor.tree.setEditable(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (popup != null)
			popup.setVisible(false);
		Object source = e.getSource();
		if (source == first) {
			treeEditor.collapseAll();
		} else if (source == second) {
			treeEditor.collapseDepth(3);
		} else if (source == third) {
			treeEditor.collapseDepth(4);
		} else if (source == fourth) {
			treeEditor.collapseDepth(5);
		} else if (source == fifth) {
			treeEditor.collapseDepth(6);
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
