package com.github.easai.utils.TreeEditor;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JFrame;

public class TreeEditorFrame extends JFrame {
	TreeEditor treeEditor=null;

	TreeEditorFrame(String treFile){
		treeEditor=new TreeEditor(treFile);
		treeEditor.init();
		init();
	}
	
	public void init(){
		getContentPane().add(treeEditor,BorderLayout.CENTER);
		treeEditor.menu.setMenu(this, (ActionListener) treeEditor, treeEditor.comp, Locale.US);
		setTitle("TreeEditor");
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
}
