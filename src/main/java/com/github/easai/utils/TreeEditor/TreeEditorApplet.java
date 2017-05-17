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

public class TreeEditorApplet extends JApplet implements ActionListener, KeyListener {
	TreeEditor treeEditor = new TreeEditor("aves.tre");
	JButton order = new JButton("Orders");
	JButton family = new JButton("Families");
	JButton genus = new JButton("Genera");
	JButton species = new JButton("Species");
	JButton subspecies = new JButton("Subspecies");
	JTextField searchWord = new JTextField(20);
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
		control.add(order);
		control.add(family);
		control.add(genus);
		control.add(species);
		// control.add(subspecies);
		control.add(searchWord);
		control.add(search);
		control.add(katakana);
		control.add(clear);
		Container pane = getContentPane();

		pane.setLayout(new BorderLayout());
		pane.add(control, BorderLayout.NORTH);
		pane.add(treeEditor.scroll, BorderLayout.CENTER);

		order.addActionListener(this);
		family.addActionListener(this);
		genus.addActionListener(this);
		species.addActionListener(this);
		subspecies.addActionListener(this);
		search.addActionListener(this);
		katakana.addActionListener(this);
		clear.addActionListener(this);

		treeEditor.tree.setEditable(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (popup != null)
			popup.setVisible(false);
		Object source = e.getSource();
		if (source == order) {
			treeEditor.collapseAll();
		} else if (source == family) {
			treeEditor.collapseDepth(3);
		} else if (source == genus) {
			treeEditor.collapseDepth(4);
		} else if (source == species) {
			treeEditor.collapseDepth(5);
		} else if (source == subspecies) {
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
