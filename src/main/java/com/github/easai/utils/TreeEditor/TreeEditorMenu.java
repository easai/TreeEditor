package com.github.easai.utils.TreeEditor;

import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class TreeEditorMenu {
	Locale locale = new Locale("en", "US");
	JMenuItem mi;
	public JMenuBar mb = new JMenuBar();
	JMenu m[];
	String menus[] = { "Files", "Edit", "View", "Help" };
	String menuitems[][] = {
			{ "Open", "Save", "SaveAs", "SaveasHTML", /*"PrintPreview", "PrintEnglishDocuments",*/ "Exit" },
			{ "UndoDelete", "Add", "Copy", "AddImage", "ApplyAll", "Delete", "Sort", "SortAll", "Search" },
			{ "CollapseAll", "ExpandAll", "Collapse", "CollapseNodeatSameLevel", "Refresh", "FontSize" },
			{ "AboutTreeEditor" } };

	final static int nFilesOpen = 0;
	final static int nFilesSave = 1;
	final static int nFilesSaveAs = 2;
	final static int nFilesSaveasHTML = 3;
	final static int nFilesPrintPreview = 4;
	final static int nFilesPrintEnglishDocuments = 5;
	final static int nFilesExit = 6;
	final static int nEditUndoDelete = 7;
	final static int nEditAdd = 8;
	final static int nEditCopy = 9;
	final static int nEditAddImage = 10;
	final static int nEditApplyAll = 11;
	final static int nEditDelete = 12;
	final static int nEditSort = 13;
	final static int nEditSortAll = 14;
	final static int nEditSearch = 15;
	final static int nViewCollapseAll = 16;
	final static int nViewExpandAll = 17;
	final static int nViewCollapse = 18;
	final static int nViewCollapseNodeatSameLevel = 19;
	final static int nViewRefresh = 20;
	final static int nViewFontSize = 21;
	final static int nHelpAboutTreeEditor = 22;

	int mi_num[][] = {
			{ nFilesOpen, nFilesSave, nFilesSaveAs, nFilesSaveasHTML, /* nFilesPrintPreview, nFilesPrintEnglishDocuments,*/
					nFilesExit },
			{ nEditUndoDelete, nEditAdd, nEditCopy, nEditAddImage, nEditApplyAll, nEditDelete, nEditSort, nEditSortAll,
					nEditSearch },
			{ nViewCollapseAll, nViewExpandAll, nViewCollapse, nViewCollapseNodeatSameLevel, nViewRefresh,
					nViewFontSize },
			{ nHelpAboutTreeEditor } };

	public void setMenu(JFrame frame, ActionListener l, Hashtable<JMenuItem, Integer> comp, Locale locale) {
		this.locale = locale;
		setMenu(l, comp);
		frame.setJMenuBar(mb);
	}

	public void setMenu(JApplet ap, ActionListener l, Hashtable<JMenuItem, Integer> comp, Locale locale) {
		this.locale = locale;
		setMenu(l, comp);
		ap.setJMenuBar(mb);
	}

	public void setMenu(ActionListener l, Hashtable<JMenuItem, Integer> comp) {
		// setMnemonic(new MenuShortcut(KeyEvent.VK_A))
		m = new JMenu[menus.length];
		ResourceBundle menuStrings = null;
		ResourceBundle menuItemStrings = null;
		if (locale != Locale.US) {
			menuStrings = ResourceBundle.getBundle("TreeEditorMenuMenu", locale);
			menuItemStrings = ResourceBundle.getBundle("TreeEditorMenuMenuItem", locale);
		}

		for (int i = 0; i < menus.length; i++) {
			if (locale == Locale.US)
				m[i] = new JMenu(menus[i]);
			else
				m[i] = new JMenu(menuStrings.getString(menus[i]));
			if (i != menus.length - 1) {
				mb.add(m[i]);
			}
			for (int j = 0; j < menuitems[i].length; j++) {
				if (locale == Locale.US)
					m[i].add(mi = new JMenuItem(menuitems[i][j]));
				else
					m[i].add(mi = new JMenuItem(menuItemStrings.getString(menuitems[i][j])));
				comp.put(mi, new Integer(mi_num[i][j]));
				mi.addActionListener(l);
				// if ( // disabled menuitems
				// mi_num[i][j] == nRun ||
				// ((ap != null)
				// && (mi_num[i][j] == nOpenURL
				// || mi_num[i][j] == nSave)))

				// mi.setEnabled (false);
				// else if (mi_num[i][j] == nQuit)
				// mi.setShortcut(new
				// MenuShortcut(KeyEvent.VK_Q|KeyEvent.CTRL_MASK));
			}
		}
		mb.add(Box.createHorizontalGlue());
		mb.add(m[menus.length - 1]);

	}
}
