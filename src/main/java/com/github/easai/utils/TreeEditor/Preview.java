package com.github.easai.utils.TreeEditor;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.tree.DefaultMutableTreeNode;

public class Preview extends Frame {
	TreeEditor treeEditor;
	DefaultMutableTreeNode top;
	PreviewCanvas canvas;

	Preview(DefaultMutableTreeNode top, TreeEditor treeEditor) {
		this.top = top;
		this.treeEditor = treeEditor;
	}

	public void init() {
		canvas = new PreviewCanvas(top, treeEditor);
		add(canvas);
		setTitle("Preview");
		setSize(400, 400);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
	}
}

class PreviewCanvas extends Canvas {
	TreeEditor treeEditor;
	DefaultMutableTreeNode top;

	PreviewCanvas(DefaultMutableTreeNode top, TreeEditor treeEditor) {
		this.top = top;
		this.treeEditor = treeEditor;
	}

	public void paint(Graphics g) {
		treeEditor.printTree(top, 0, g);
	}
}
