package com.github.easai.utils.TreeEditor;
// TreeEditor.java -- a tree structure data editor 

import java.awt.BorderLayout;

// Erica Asai<easai@acm.org>
// Time-stamp: <2004-04-14 21:49:06 @easai>

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

// Comments: 
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeEditor extends JScrollPane implements ActionListener, Printable, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JApplet applet = null;
	public String fileName = "aves-en.tre";
	public DefaultTreeModel treeModel;

	// (load-file "j://elisp//java.el")
	// (java-jmenu "TreeEditor" '("Files" "Edit" "View""Help") '(("Open" "Save"
	// "SaveAs" "Save as HTML" "Print Preview" "Print English Documents"
	// "Exit")("Undo Delete" "Add" "Copy" "Add Image" "ApplyAll" "Delete" "Sort"
	// "SortAll" "Search")("Collapse All""Expand All""Collapse""Collapse Node at
	// Same Level""Refresh" "Font Size")("About TreeEditor")))

	public Hashtable<JMenuItem, Integer> comp = new Hashtable<JMenuItem, Integer>();
	public TreeEditorMenu menu = new TreeEditorMenu();
	public JTree tree;
	public DefaultMutableTreeNode top;

	Stack<DefaultMutableTreeNode> deletedNodes = new Stack<DefaultMutableTreeNode>();
	Stack<DefaultMutableTreeNode> deletedNodesFrom = new Stack<DefaultMutableTreeNode>();
	String searchWord = "";
	ArrayList<TreePath> pathArray = new ArrayList<TreePath>();
	int searchIndex = 0;
	JPopupMenu popup = new JPopupMenu();
	int N = 0;
	int newNodeSuffix;
	int height;
	int leftOffset;
	String orgTree = "";
	static public Logger log = LoggerFactory.getLogger(TreeEditor.class);

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            the TRE file
	 */
	public TreeEditor(String fileName) {
		this.fileName = fileName;
		init();
	}

	/**
	 * Initialization procedure.
	 */
	public void init() {

		top = new DefaultMutableTreeNode("top");
		readTree(top, fileName);
		setTree(top);
		expandAll();
		/*
		 * addWindowListener(new WindowAdapter() { public void
		 * windowClosing(WindowEvent e) { quit(); } });
		 */

		tree.addMouseListener(this);

		popupInit();

		log.info("TreeEditor started");
	}

	/**
	 * Initializes the popup menu.
	 */
	private void popupInit() {
		String popupMenu[] = { "Add","Delete","Open","Save","Copy" };
		int popupMenu_num[] = { TreeEditorMenu.nEditAdd,TreeEditorMenu.nEditDelete,TreeEditorMenu.nFilesOpen,TreeEditorMenu.nFilesSave,TreeEditorMenu.nEditCopy };
		JMenuItem mi;
		popup.removeAll();
		for (int i = 0; i < popupMenu.length; i++) {
			mi = new JMenuItem(popupMenu[i]);
			mi.addActionListener(this);
			popup.add(mi);
			comp.put(mi, new Integer(popupMenu_num[i]));
			popup.pack();
		}
	}

	/**
	 * Searches the tree.
	 */
	public void search() {
		String word = JOptionPane.showInputDialog("Search: ", searchWord);
		search(word);
	}

	/**
	 * Searches for the specified word.
	 * 
	 * @param word
	 *            the search keyword
	 */
	public void search(String word) {
		if (word == "")
			return;
		if (searchWord.equals(word)) {
			searchNext();
		} else
			search(top, word);
	}

	/**
	 * Removes diacritics for search.
	 * 
	 * @param str
	 * @return
	 */
	String removeAccents(String str) {
		str = str.replace((char) 0x00e0, 'a');
		str = str.replace((char) 0x00e1, 'a');
		str = str.replace((char) 0x00e2, 'a');
		str = str.replace("" + (char) 0x00e6, "ae"); // char -> String
		str = str.replace((char) 0x00e7, 'c');
		str = str.replace((char) 0x00e8, 'e');
		str = str.replace((char) 0x00e9, 'e');
		str = str.replace((char) 0x00ea, 'e');
		str = str.replace((char) 0x00eb, 'e');
		str = str.replace((char) 0x00ec, 'i');
		str = str.replace((char) 0x00ed, 'i');
		str = str.replace((char) 0x00ee, 'i');
		str = str.replace((char) 0x00ef, 'i');
		str = str.replace((char) 0x00f2, 'o');
		str = str.replace((char) 0x00f3, 'o');
		str = str.replace((char) 0x00f4, 'o');
		str = str.replace((char) 0x00f9, 'u');
		str = str.replace((char) 0x00fa, 'u');
		str = str.replace((char) 0x00fb, 'u');
		str = str.replace((char) 0x00fc, 'u');

		return str;
	}

	/**
	 * For search by Roman alphabets
	 * 
	 * @param str
	 *            the String
	 * @return
	 */
	String toKatakana(String str) {
		String katakana = "";
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (0x3040 < ch && ch < 0x30a0)// hiragana
			{
				katakana += (char) (ch + 0x60);
			} else
				katakana += (char) ch;
		}
		return katakana;
	}

	/**
	 * For internal use.
	 * 
	 * @param ch
	 *            the character
	 * @return the offset
	 */
	int consonantOffset(char ch) {
		int offset = 0;
		switch (ch) {
		case 'k':
			offset = 0x30ab;
			break;
		case 'g':
			offset = 0x30ac;
			break;

		case 's':
			offset = 0x30b5;
			break;
		case 'z':
			offset = 0x30b6;
			break;

		case 't':
			offset = 0x30bf;
			break;
		case 'd':
			offset = 0x30c0;
			break;

		case 'n':
			offset = 0x30ca;
			break;

		case 'h':
			offset = 0x30cf;
			break;
		case 'b':
			offset = 0x30d0;
			break;
		case 'p':
			offset = 0x30d1;
			break;

		case 'm':
			offset = 0x30de;
			break;

		case 'y':
			offset = 0x30e4;
			break;

		case 'r':
			offset = 0x30e9;
			break;

		case 'w':
			offset = 0x30ef;
			break;

		case 'l':
			offset = 0x30a1;
			break;

		}
		return offset;
	}

	/**
	 * Roman alphabets -> katakana conversion
	 * 
	 * @param str
	 *            the String
	 * @return the converted String
	 */
	String latinToKatakana(String str) {
		int state = 0;
		String katakana = "";
		int offset = 0;
		char prev = '\0';
		int len = str.length();
		int vowelOffset = 0;
		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);
			vowelOffset = 0;
			switch (state) {
			case 0:
				switch (ch) {
				case 'a':
					katakana += (char) 0x30a2;
					break;
				case 'i':
					katakana += (char) 0x30a4;
					break;
				case 'u':
					katakana += (char) 0x30a6;
					break;
				case 'e':
					katakana += (char) 0x30a8;
					break;
				case 'o':
					katakana += (char) 0x30aa;
					break;
				case '-':
					katakana += (char) 0x30fc;
					break;

				case 'k':
				case 'g':
				case 's':
				case 'z':
				case 't':
				case 'd':
				case 'h':
				case 'b':
				case 'p':
				case 'm':
				case 'y':
				case 'r':
				case 'w':
				case 'l':
				case 'f':
					offset = consonantOffset(ch);
					state = 1;
					break;

				case 'j':
					state = 5;
					break;
				case 'n':
					state = 4;
					break;
				case 'c':
					state = 6;
					break;

				default:
					katakana += ch;
					break;
				}
				break;
			case 1:
				switch (ch) {
				case 'a':
				case 'i':
				case 'u':
				case 'e':
				case 'o':
					if (prev == 'n' || prev == 'm' || prev == 'r') {
						switch (ch) {
						case 'a':
							break;
						case 'i':
							vowelOffset = 1;
							break;
						case 'u':
							vowelOffset = 2;
							break;
						case 'e':
							vowelOffset = 3;
							break;
						case 'o':
							vowelOffset = 4;
							break;
						}
						katakana += (char) (offset + vowelOffset);
					} else if (prev == 'h' || prev == 'b' || prev == 'p') {
						switch (ch) {
						case 'a':
							break;
						case 'i':
							vowelOffset = 3;
							break;
						case 'u':
							vowelOffset = 6;
							break;
						case 'e':
							vowelOffset = 9;
							break;
						case 'o':
							vowelOffset = 12;
							break;
						}
						katakana += (char) (offset + vowelOffset);
					} else if (prev == 'y') {
						switch (ch) {
						case 'a':
							break;
						case 'u':
							vowelOffset = 2;
							break;
						case 'o':
							vowelOffset = 4;
							break;
						}
						katakana += (char) (offset + vowelOffset);
					} else if (prev == 't' || prev == 'd') {
						switch (ch) {
						case 'a':
							break;
						case 'i':
							vowelOffset = 2;
							break;
						case 'u':
							vowelOffset = 5;
							break;
						case 'e':
							vowelOffset = 7;
							break;
						case 'o':
							vowelOffset = 9;
							break;
						}
						katakana += (char) (offset + vowelOffset);
					} else if (prev == 'w') {
						switch (ch) {
						case 'a':
							katakana += (char) 0x30ef;
							break;
						case 'i':
							katakana += (char) 0x30a6;
							katakana += (char) 0x30a3;
							break;
						case 'u':
							katakana += (char) 0x30a6;
							break;
						case 'e':
							katakana += (char) 0x30a6;
							katakana += (char) 0x30a7;
							break;
						case 'o':
							katakana += (char) 0x30f2;
							break;
						}
					} else if (prev == 'f') {
						katakana += (char) 0x30d5;
						switch (ch) {
						case 'a':
							katakana += (char) 0x30a1;
							break;
						case 'i':
							katakana += (char) 0x30a3;
							break;
						case 'u':
							katakana += (char) 0x30a5;
							break;
						case 'e':
							katakana += (char) 0x30a7;
							break;
						case 'o':
							katakana += (char) 0x30a9;
							break;
						}
					} else {
						switch (ch) {
						case 'a':
							break;
						case 'i':
							vowelOffset = 2;
							break;
						case 'u':
							vowelOffset = 4;
							break;
						case 'e':
							vowelOffset = 6;
							break;
						case 'o':
							vowelOffset = 8;
							break;
						}
						katakana += (char) (offset + vowelOffset);
					}
					offset = 0;
					state = 0;
					break;
				case 'h':
					if (prev == 's') {
						state = 5;
					} else if (prev == 't') {
						katakana += (char) 0x30c6;
						state = 7;
					} else if (prev == 'd') {
						katakana += (char) 0x30c7;
						state = 7;
					} else if (prev == ch) {
						katakana += (char) 0x30c3;
					} else {
						state = 0;
					}
					break;
				case 's':
					if (prev == 't') {
						state = 3;
					} else if (prev == ch) {
						katakana += (char) 0x30c3;
					}
					break;
				case 'y':
					if (prev == ch) {
						katakana += (char) 0x30c3;
					} else {
						if (prev == 'n' || prev == 'm' || prev == 'r') {
							vowelOffset = 1;
						} else if (prev == 'h' || prev == 'b' || prev == 'p') {
							vowelOffset = 3;
						} else if (prev == 't' || prev == 'd') {
							vowelOffset = 2;
						} else if (prev == 'w') {
							offset = 0x30a2;
							vowelOffset = 4;
						} else {
							vowelOffset = 2;
						}
						katakana += (char) (offset + vowelOffset);
						state = 7;
					}
					break;
				default:
					if (prev == ch) {
						katakana += (char) 0x30c3;
					} else {
						katakana += ch;
						state = 0;
					}
					break;
				}
				break;
			case 2:
				switch (ch) {
				case 'a':
					katakana += (char) 0x30e3;
					break;
				case 'i':
					katakana += (char) 0x30a3;
					break;
				case 'u':
					katakana += (char) 0x30e5;
					break;
				case 'e':
					katakana += (char) 0x30a7;
					break;
				case 'o':
					katakana += (char) 0x30e7;
					break;
				case 'y':
					katakana += (char) 0x30b7;
					state = 7;
					break;
				default:
					katakana += ch;
					state = 0;
					break;
				}
				break;
			case 3:
				switch (ch) {
				case 'a':
				case 'i':
				case 'u':
				case 'e':
				case 'o':
					katakana += (char) 0x30c4;
					break;
				default:
					katakana += ch;
				}
				switch (ch) {
				case 'a':
					katakana += (char) 0x30a1;
					break;
				case 'i':
					katakana += (char) 0x30a3;
					break;
				case 'e':
					katakana += (char) 0x30a7;
					break;
				case 'o':
					katakana += (char) 0x30a9;
					break;
				}
				state = 0;
				break;
			case 4:
				switch (ch) {
				case 'n':
					katakana += (char) 0x30f3;
					break;
				case 'a':
					katakana += (char) 0x30ca;
					state = 0;
					break;
				case 'i':
					katakana += (char) 0x30cb;
					state = 0;
					break;
				case 'u':
					katakana += (char) 0x30cc;
					state = 0;
					break;
				case 'e':
					katakana += (char) 0x30cd;
					state = 0;
					break;
				case 'o':
					katakana += (char) 0x30ce;
					state = 0;
					break;
				case 'y':
					katakana += (char) 0x30cb;
					state = 7;
					break;
				default:
					katakana += (char) 0x30f3;
					offset = consonantOffset(ch);
					switch (ch) {
					case 'j':
						state = 5;
						break;
					case 'c':
						state = 6;
						break;
					default:
						state = 1;
						break;
					}
					break;
				}
				break;
			case 5:
				switch (ch) {
				case 'a':
				case 'i':
				case 'u':
				case 'e':
				case 'o':
					if (prev == 'j') {
						katakana += (char) 0x30b8;
					} else if (prev == 'h') {
						katakana += (char) 0x30b7;
					}
					state = 0;
					break;
				case 'j':
					katakana += (char) 0x30c3;
					break;
				case 'y':
					katakana += (char) 0x30b8;
					state = 7;
					break;
				default:
					katakana += ch;
					state = 0;
				}
				switch (ch) {
				case 'a':
					katakana += (char) 0x30e3;
					break;
				case 'u':
					katakana += (char) 0x30e5;
					break;
				case 'e':
					katakana += (char) 0x30a7;
					break;
				case 'o':
					katakana += (char) 0x30e7;
					break;
				}
				break;
			case 6:
				switch (ch) {
				case 'a':
				case 'i':
				case 'u':
				case 'e':
				case 'o':
					katakana += (char) 0x30c1;
					state = 0;
					break;
				case 'h':
					break;
				case 'c':
					katakana += (char) 0x30c3;
					break;
				case 'y':
					katakana += (char) 0x30c1;
					state = 7;
					break;
				default:
					katakana += ch;
					state = 0;
				}
				switch (ch) {
				case 'a':
					katakana += (char) 0x30e3;
					break;
				case 'u':
					katakana += (char) 0x30e5;
					break;
				case 'e':
					katakana += (char) 0x30a7;
					break;
				case 'o':
					katakana += (char) 0x30e7;
					break;
				}
				break;
			case 7:
				switch (ch) {
				case 'a':
					katakana += (char) 0x30e3;
					break;
				case 'i':
					katakana += (char) 0x30a3;
					break;
				case 'u':
					katakana += (char) 0x30e5;
					break;
				case 'e':
					katakana += (char) 0x30a7;
					break;
				case 'o':
					katakana += (char) 0x30e7;
					break;
				default:
					katakana += ch;
					break;
				}
				state = 0;
				break;
			}
			prev = ch;
		}
		if (0 < len) {
			char ch = str.charAt(len - 1);
			if (ch == 'n')
				katakana += (char) 0x30f3;
		}
		return katakana;
	}

	/**
	 * Searches the tree.
	 * 
	 * @param n
	 *            the tree node
	 * @param word
	 *            the search word
	 */
	public void search(DefaultMutableTreeNode n, String word) {
		TreeSelectionModel selectionModel = tree.getSelectionModel();
		selectionModel.clearSelection();
		selectionModel.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		pathArray.clear();
		DefaultMutableTreeNode node = null;
		String text = "", w = "";
		TreeNode list[];
		TreePath path = null;
		w = word.toLowerCase();
		w = removeAccents(word);
		w = toKatakana(w);
		int count = 0;

		for (Enumeration e = n.postorderEnumeration(); e.hasMoreElements();) {
			if (100 < count) {
				JOptionPane.showMessageDialog(this, "Too many search results");
				break;
			}
			node = (DefaultMutableTreeNode) e.nextElement();
			text = (String) node.getUserObject();
			text = text.toLowerCase();
			text = removeAccents(text);
			if (text != null && text.contains(w)) {
				list = node.getPath();
				path = new TreePath(list);
				selectionModel.addSelectionPath(path);
				pathArray.add(path);
				count++;
			}
		}
		searchIndex = 0;
		searchNext();
		searchWord = word;
	}

	/**
	 * Searches the next word in the tree.
	 */
	public void searchNext() {
		int nPaths = pathArray.size();
		if (pathArray == null || nPaths <= 0)
			return;
		TreePath path = null;
		TreeSelectionModel selectionModel = tree.getSelectionModel();
		selectionModel.clearSelection();
		for (int i = 0; i < nPaths; i++) {
			path = pathArray.get(i);
			selectionModel.addSelectionPath(path);
		}
		path = pathArray.get(searchIndex);
		tree.scrollPathToVisible(path);
		searchIndex++;
		if (pathArray.size() <= searchIndex)
			searchIndex = 0;
	}

	// deliberately leaving this redundancy of passing top
	/**
	 * @param top
	 *            the top node
	 */
	public void setTree(DefaultMutableTreeNode top) {
		treeModel = new DefaultTreeModel(top);
		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setEditable(true);
		tree.setRootVisible(false);
		tree.setInvokesStopCellEditing(true);
		// trees.expandPath(new TreePath(top.getPath()));
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				// TreeNode node=
				// (TreeNode) tree.getLastSelectedPathComponent();
				if (node == null)
					return;
				Object nodeInfo = node.getUserObject();
			}
		});
		
		getViewport().add(tree);
		revalidate();
	}

	/**
	 * Deletes the specified node.
	 */
	private void deleteNode() {
		DefaultMutableTreeNode node = null, up;
		TreePath path = tree.getSelectionPath();
		if (path != null) {
			node = (DefaultMutableTreeNode) (path.getLastPathComponent());

			up = (DefaultMutableTreeNode) node.getParent();
			deletedNodes.push(node);
			deletedNodesFrom.push(up);
			treeModel.removeNodeFromParent(node);
			tree.scrollPathToVisible(new TreePath(up.getPath()));
		}
	}

	/**
	 * undo
	 */
	private void undo() {
		if (deletedNodes.empty())
			return;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) deletedNodes.pop();
		DefaultMutableTreeNode from = (DefaultMutableTreeNode) deletedNodesFrom.pop();
		if (from == null) {
			from = top;
		}
		treeModel.insertNodeInto(node, from, from.getChildCount());
		tree.makeVisible(new TreePath(node.getPath()));
	}

	/**
	 * Adds a node.
	 */
	private void addNode() {
		DefaultMutableTreeNode node = null;
		TreePath path = tree.getSelectionPath();
		if (path == null) {
			node = top;
		} else {
			node = (DefaultMutableTreeNode) (path.getLastPathComponent());
		}
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode((newNodeSuffix++) + " ");
		// newNode.addMouseListener(this);
		treeModel.insertNodeInto(newNode, node, node.getChildCount());
		tree.startEditingAtPath(new TreePath(newNode.getPath()));
		tree.makeVisible(new TreePath(newNode.getPath()));
	}

	/**
	 * Adds an image.
	 */
	private void addImage() {
		DefaultMutableTreeNode node = null;
		TreePath path = tree.getSelectionPath();
		if (path == null) {
			node = top;
		} else {
			node = (DefaultMutableTreeNode) (path.getLastPathComponent());
		}

		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new JButton("added button"));
		// new DefaultMutableTreeNode(new Image());
		treeModel.insertNodeInto(newNode, node, node.getChildCount());
		tree.makeVisible(new TreePath(newNode.getPath()));
	}

	/**
	 * Collapses all nodes.
	 */
	void collapseAll() {
		collapseNode(top);
	}

	/**
	 * Collapses the node.
	 */
	void collapseDepth() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		TreeNode list[] = node.getPath();
		collapseNode(top, list.length);
	}

	/**
	 * Collapses the node.
	 */
	void collapseNode() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		collapseNode(selectedNode);
	}

	/**
	 * Collapses the node.
	 * 
	 * @param n
	 */
	void collapseNode(DefaultMutableTreeNode n) {
		collapseNode(n, -1);
	}

	/**
	 * Collapses the node.
	 * 
	 * @param depth
	 */
	void collapseDepth(int depth) {
		collapseNode(top, depth);
	}

	/**
	 * Collapses the node.
	 * 
	 * @param n
	 *            the node
	 * @param depth
	 *            the depth
	 */
	void collapseNode(DefaultMutableTreeNode n, int depth) {
		for (Enumeration e = n.postorderEnumeration(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			TreeNode list[] = node.getPath();
			TreePath path = new TreePath(list);
			if (!node.isRoot() && (list.length == depth || depth == -1)) {
				tree.collapsePath(path);
			}
		}
	}

	/**
	 * Expands all the nodes.
	 */
	void expandAll() {
		int i = 0;
		int newHeight = 0;
		for (Enumeration e = top.depthFirstEnumeration(); e.hasMoreElements(); e.nextElement()) {
			if (i > 0) {
				newHeight += tree.getRowBounds(i - 1).height;
			}
			tree.expandRow(i++);
		}
		newHeight += getInsets().top;
		// ?
		newHeight += getInsets().bottom * 2;
		newHeight += menu.mb.getSize().height;
		Dimension screenSize = getSize();
		if (newHeight > screenSize.height) {
			setSize(screenSize.width, newHeight);
		}
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (popup != null)
			popup.setVisible(false);
		Object source = e.getSource();
		int num = ((Integer) comp.get(source)).intValue();
		switch (num) {
		case TreeEditorMenu.nFilesOpen:
			read();
			break;
		case TreeEditorMenu.nFilesSave:
			save();
			break;
		case TreeEditorMenu.nFilesSaveAs:
			saveAs();
			break;
		case TreeEditorMenu.nFilesSaveasHTML:
			saveHTML();
			break;
		case TreeEditorMenu.nFilesSaveasXML:
			saveXML();
			break;
		case TreeEditorMenu.nFilesPrintEnglishDocuments:
			print();
			break;
		case TreeEditorMenu.nFilesExit:
			quit();
			break;
		case TreeEditorMenu.nEditUndoDelete:
			undo();
			break;
		case TreeEditorMenu.nEditSearch:
			search();
			break;
		case TreeEditorMenu.nEditAdd:
			addNode();
			break;
		case TreeEditorMenu.nEditAddImage:
			addImage();
			break;
		case TreeEditorMenu.nEditCopy:
			copyLeaf();
			break;
		case TreeEditorMenu.nEditDelete:
			deleteNode();
			break;
		case TreeEditorMenu.nEditSort:
			sortSelected();
			break;
		case TreeEditorMenu.nEditSortAll:
			sortAll();
			break;
		case TreeEditorMenu.nEditApplyAll:
			applyAll();
			break;
		case TreeEditorMenu.nViewCollapseAll:
			collapseAll();
			break;
		case TreeEditorMenu.nViewCollapse:
			collapseNode();
			break;
		case TreeEditorMenu.nViewCollapseNodeatSameLevel:
			collapseDepth();
			break;
		case TreeEditorMenu.nViewExpandAll:
			expandAll();
			break;
		case TreeEditorMenu.nViewRefresh:
			repaint();
			break;
		case TreeEditorMenu.nViewFontSize:
			break;
		case TreeEditorMenu.nHelpAboutTreeEditor:
			break;
		}
	}

	/**
	 * Sorts the selected nodes.
	 */
	void sortSelected() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		sortNodes(selectedNode);
	}

	/**
	 * Sorts the nodes.
	 * 
	 * @param selectedNode
	 */
	void sortNodes(DefaultMutableTreeNode selectedNode) {
		ArrayList<DefaultMutableTreeNode> array = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode node;

		int depth = selectedNode.getPath().length;

		for (Enumeration e = selectedNode.postorderEnumeration(); e.hasMoreElements();) {
			node = (DefaultMutableTreeNode) e.nextElement();
			TreeNode list[] = node.getPath();
			if (depth == list.length - 1) {
				array.add(node);
			}
		}

		int nNodes = array.size();

		for (int i = 0; i < nNodes; i++) {
			node = array.get(i);
			treeModel.removeNodeFromParent(node);
		}

		if (0 < nNodes) {
			ArrayList<Integer> sorted = new ArrayList<Integer>();
			for (int i = 0; i < nNodes; i++) {
				node = array.get(i);
				String title = (String) node.getUserObject();
				int nSorted = sorted.size();
				if (0 < nSorted) {
					int j = 0;
					while (((String) (array.get(sorted.get(j)).getUserObject())).compareTo(title) < 0 && ++j < nSorted)
						;
					sorted.add(j, new Integer(i));
				} else
					sorted.add(0, new Integer(i));
			}
			int index = 0;
			for (int i = 0; i < nNodes; i++) {
				index = sorted.get(i);
				node = array.get(index);
				treeModel.insertNodeInto(node, selectedNode, i);

			}
			tree.expandPath(new TreePath(selectedNode.getPath()));
		}
	}

	/**
	 * Sorts all the nodes.
	 */
	void sortAll() {
		for (Enumeration e = top.postorderEnumeration(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			sortNodes(node);
		}
	}

	/**
	 * For applyNode().
	 */
	void applyAll() {
		applyNode(top);
	}

	/**
	 * For internal use only.
	 * 
	 * @param n
	 *            the node
	 */
	void applyNode(DefaultMutableTreeNode n) {
		for (Enumeration e = n.postorderEnumeration(); e.hasMoreElements();) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (node != null) {
				String leaf = (String) node.getUserObject();
				int index = leaf.indexOf(" ");
				String newLeaf = "";
				if (0 < index)
					newLeaf = leaf.substring(index) + " " + leaf.substring(0, index);
				node.setUserObject(newLeaf);
			}
		}
	}

	/**
	 * Copies the leaf.
	 */
	public void copyLeaf() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (selectedNode == null)
			return;
		String leaf = (String) selectedNode.getUserObject();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			clipboard.setContents(new StringSelection(leaf), null);
		}
	}

	/**
	 * Copies the local leaf.
	 */
	public void copyLeafLocal() {
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (selectedNode == null)
			return;
		String leaf = (String) selectedNode.getUserObject();
		Clipboard clipboard = new Clipboard("clipboard");
		StringSelection stringSelection = new StringSelection(leaf);
		clipboard.setContents(stringSelection, stringSelection);
	}

	/**
	 * Initializes the panel.
	 */
	void initPanel() {
		top = new DefaultMutableTreeNode("top");
		readTree(top, fileName);
		setTree(top);
		expandAll();
	}

	/**
	 * Saves the tree if it is modified.
	 */
	public void saveIfModified() {
		String curTree = parseTree();
		if (!curTree.equals(orgTree)) {
			int answer = JOptionPane.showConfirmDialog(null,
					"The modification to this file will be discarded.  Before closing this file, would you like to save the alteration to the file?",
					"Confirm File Save", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) {
				save();
			}
		}
	}

	/**
	 * Opens a FileDialog for TRE file.
	 */
	public void read() {
		saveIfModified();
		String fn = "";
		FileDialog fd = new FileDialog((JFrame) null, "Open", FileDialog.LOAD);
		fd.setVisible(true);
		if ((fn = fd.getFile()) != null) {
			fileName = fd.getDirectory() + fn;
			readTreeFile(fileName);
		}
	}

	/**
	 * Reads the TRE file.
	 * 
	 * @param fileName
	 *            the TRE file
	 */
	public void readTreeFile(String fileName) {
		readTree(top, fileName);
		if (treeModel != null) {
			treeModel.reload();
			expandAll();
			repaint();
			orgTree = this.parseTree();
		}
	}

	/**
	 * Reads the specified file.
	 * 
	 * @param fileName
	 *            the file
	 * @return the contents of the file
	 */
	public String readFile(String fileName) {
		String buffer = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			char buf[] = new char[255];
			int len;
			while ((len = reader.read(buf, 0, 255)) != -1) {
				buffer += new String(buf, 0, len);
			}
			reader.close();
		} catch (Exception e) {
			log.error("File read error.", e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {
				log.error("File read error.", e);
			}
		}

		return buffer;
	}

	/**
	 * Reads the specified TRE file.
	 * 
	 * @param top
	 *            the top node
	 * @param fileName
	 *            the TRE file
	 */
	public void readTree(DefaultMutableTreeNode top, String fileName) {
		StringBuffer buffer = new StringBuffer();
		DefaultMutableTreeNode node = top, buf;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		FileInputStream fis = null;
		InputStream is = null;
		try {
			log.info("Reading file: " + fileName);
			if (fileName == null || fileName.isEmpty() || !((new File(fileName)).exists())) {
				throw new Exception("File not found: " + fileName);
			}
			if (top != null) {
				top.removeAllChildren();
			}
			// setTitle(fileName);
			if (applet != null) {
				URL url = new URL(applet.getDocumentBase(), fileName);
				is = url.openConnection().getInputStream();
				isr = new InputStreamReader(is, "UTF8");
			} else {
				File file = new File(fileName);
				if (!file.exists()) {
					JOptionPane.showMessageDialog(this, "File not found");
					return;
				}
				fis = new FileInputStream(fileName);
				isr = new InputStreamReader(fis, "UTF8");
			}
			reader = new BufferedReader(isr);
			int ch;
			int currentLevel = 0;
			do {
				ch = reader.read();
				if (ch == '\n' || ch == -1) {
					if (buffer.length() >= 0) {
						int len = 0;

						while (len < buffer.length() && (buffer.charAt(len) == ' ' || buffer.charAt(len) == '\r'
								|| buffer.charAt(len) == '\t' || buffer.charAt(len) == '\n')) {
							len++;
						}
						if (buffer.length() != len) {
							int level = 0;
							while (buffer.charAt(0) == '-') {
								level++;
								buffer = buffer.deleteCharAt(0);
							}
							for (int i = level; i <= currentLevel; i++) {
								buf = (DefaultMutableTreeNode) node.getParent();
								if (buf == null) {
									node = (DefaultMutableTreeNode) node.getRoot();
								} else {
									node = buf;
								}
							}
							buf = new DefaultMutableTreeNode(buffer.toString());
							node.add(buf);
							node = buf;
							currentLevel = level;
							buffer.delete(0, buffer.length());
						}
					}
				} else if (ch != '\r') {
					buffer.append((char) ch);
				}
			} while (ch > -1);
			reader.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (reader != null)
					reader.close();
				else if (isr != null)
					isr.close();
				else if (is != null)
					is.close();
				else if (fis != null)
					fis.close();
			} catch (Exception e) {
				log.error("File read error:", e);
			}
		}
	}

	/**
	 * Gets the String representations of the tree.
	 * 
	 * @param top
	 *            the top node
	 * @param level
	 *            the level
	 * @param start
	 *            the char
	 * @param endline
	 *            the new line char
	 * @return the String representations of the tree.
	 */
	private String parseTree() {
		return parseTree(top, 0, "-", "\n");
	}

	/**
	 * Parses the tree.
	 * 
	 * @param top
	 *            the top node
	 * @param level
	 *            the level
	 * @param start
	 *            the char
	 * @param endline
	 *            the new line char
	 * @return the String representations of the tree.
	 */
	private String parseTree(DefaultMutableTreeNode top, int level, String start, String endline) {
		String res = "";
		if (top != null) {
			String prefix = "";
			DefaultMutableTreeNode buffer;
			for (int i = 0; i < level; i++)
				prefix += start;
			if (!top.isLeaf()) {
				for (int i = 0; i < top.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) top.getChildAt(i);
					res += prefix + ((String) buffer.getUserObject()) + endline
							+ parseTree(buffer, level + 1, start, endline);
				}
			}
		}
		return res;
	}

	/**
	 * Parses the tree.
	 * 
	 * @param top
	 *            the top node of the tree
	 * @param level
	 *            the level
	 * @return the HTML string that represents the tree
	 */
	private String parseTreeHTML(DefaultMutableTreeNode top, int level) {
		String res = "";
		if (top != null) {
			String buf = "";
			DefaultMutableTreeNode buffer;
			if (!top.isLeaf()) {
				int i;
				buf += "<ul>";
				for (i = 0; i < top.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) top.getChildAt(i);
					buf += "<li>" + ((String) buffer.getUserObject()) + "</li>\n" + parseTreeHTML(buffer, level + 1);
					N++;
				}
				buf += "</ul>";
				return buf;
			}
		}
		return res;
	}

	/**
	 * Parses the tree.
	 * 
	 * @param top
	 *            the top node
	 * @param level
	 *            the level
	 * @return the String representations of the tree.
	 */
	public String parseTreeXML(DefaultMutableTreeNode top, int level) {
		String res = "";
		if (top != null) {
			String buf = "";
			DefaultMutableTreeNode buffer;
			if (!top.isLeaf()) {
				int i;
				// buf += "<node>";
				for (i = 0; i < top.getChildCount(); i++) {
					buffer = (DefaultMutableTreeNode) top.getChildAt(i);
					buf += "<node><value>" + ((String) buffer.getUserObject()) + "</value>\n"
							+ parseTreeXML(buffer, level + 1) + "</node>";
					N++;
				}
				// buf += "</node>";
				return buf;
			}
		}
		return res;
	}

	/**
	 * Saves the tree.
	 */
	public void save() {
		if (fileName != "") {
			writeTree(top, fileName, "-", "\n");
		} else {
			saveAs();
		}
	}

	/**
	 * Prompts for TRE file.
	 */
	private void saveAs() {
		FileDialog fd = new FileDialog((JFrame) null, "Save", FileDialog.SAVE);
		fd.setVisible(true);
		String fn = "";
		if ((fn = fd.getFile()) != null) {
			writeTree(top, fd.getDirectory() + fn, "-", "\n");
		}
	}

	/**
	 * Prompts for HTML file.
	 */
	private void saveHTML() {
		FileDialog fd = new FileDialog((JFrame) null, "Save", FileDialog.SAVE);
		fd.setVisible(true);
		String fn = "";
		if ((fn = fd.getFile()) != null) {
			writeTreeHTML(top, fd.getDirectory() + fn);
		}
	}

	/**
	 * Prompts for XML output.
	 */
	private void saveXML() {
		FileDialog fd = new FileDialog((JFrame) null, "Save", FileDialog.SAVE);
		fd.setVisible(true);
		String fn = "";
		if ((fn = fd.getFile()) != null) {
			writeTreeXML(top, fd.getDirectory() + fn);
		}
	}

	/**
	 * Writes out the tree in TRE format.
	 * 
	 * @param top
	 *            the top node
	 * @param fileName
	 *            the file
	 * @param start
	 *            the char
	 * @param endline
	 *            the new line char
	 */
	public void writeTree(DefaultMutableTreeNode top, String fileName, String start, String endline) {
		try {
			writeFile(parseTree(top, 0, start, endline), fileName);
		} catch (Exception e) {
			log.error("File write error:", e);
		}
	}

	/**
	 * Writes out the tree in HTML format.
	 * 
	 * @param top
	 *            the top node of the tree
	 * @param fileName
	 *            the file
	 */
	public void writeTreeHTML(DefaultMutableTreeNode top, String fileName) {
		String buffer;
		try {
			buffer = readFile("first.html");
			buffer += parseTreeHTML(top, 0);
			buffer += readFile("last.html");
			writeFile(buffer, fileName);
		} catch (Exception e) {
			log.error("File write (HTML) error.", e);
		}
	}

	/**
	 * Writes out the tree in XML format.
	 * 
	 * @param top
	 *            the top node
	 * @param fileName
	 *            the file
	 */
	public void writeTreeXML(DefaultMutableTreeNode top, String fileName) {
		String buffer;
		try {
			buffer = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";
			buffer += parseTreeXML(top, 0);
			writeFile(buffer, fileName);
		} catch (Exception e) {
			log.error("File write (XML) error.", e);
		}
	}

	/**
	 * Writes out the text.
	 * 
	 * @param str
	 *            the text
	 * @param fileName
	 *            the file
	 * @throws Exception
	 *             exception
	 */
	public void writeFile(String str, String fileName) throws Exception {
		log.info("Saving the tree: " + fileName);
		FileOutputStream fos = null;
		Writer out = null;
		try {
			fos = new FileOutputStream(fileName);
			out = new OutputStreamWriter(fos, "UTF8");
			out.write(str);
			out.close();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
				else if (fos != null)
					fos.close();
			} catch (IOException e) {
				throw e;
			}
		}

	}

	/**
	 * Parses the tree.
	 * 
	 * @param top
	 *            the top node
	 * @param level
	 *            the level
	 * @param g
	 *            Graphics
	 * @param h
	 *            the height
	 * @return the height
	 */
	private int parsePrintTree(DefaultMutableTreeNode top, int level, Graphics g, int h) {
		DefaultMutableTreeNode buffer;
		if (!top.isLeaf()) {
			for (int i = 0; i < top.getChildCount(); i++) {
				buffer = (DefaultMutableTreeNode) top.getChildAt(i);
				g.drawString(((String) buffer.getUserObject()), leftOffset + level * 10, h);
				h += height;
				h = parsePrintTree(buffer, level + 1, g, h);
			}
		}
		return h;
	}

	/**
	 * Prints out the tree.
	 */
	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		Book book = new Book();
		book.append(this, job.defaultPage());
		job.setPageable(book);
		if (job.printDialog()) {
			try {
				job.print();
			} catch (Exception e) {
				log.error("Print error:", e);
			}
		}
	}

	/**
	 * Opens the proview window.
	 */
	public void preview() {
		Preview p = new Preview(top, this);
		p.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.print.Printable#print(java.awt.Graphics,
	 * java.awt.print.PageFormat, int)
	 */
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
		printTree(top, 0, g);
		return Printable.PAGE_EXISTS;
	}

	/**
	 * Prints out the tree.
	 * 
	 * @param top
	 *            the top node
	 * @param level
	 *            the level
	 * @param g
	 *            the Graphics object
	 */
	public void printTree(DefaultMutableTreeNode top, int level, Graphics g) {
		if (top == null)
			return;

		// Font font=new Font("monospaced", Font.PLAIN, 16);
		// FontMetrics fontM=getFontMetrics(font);		
		FontMetrics fontMetrics = getFontMetrics(getFont());
		// int width=fontM.charWidth('-');
		height = fontMetrics.getHeight();
		Insets insets = getInsets();
		// int leftOffset = insets.left;
		int h = insets.top;
		parsePrintTree(top, 0, g, h);
	}

	/*
	 * For right click menu.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		int x0 = e.getX();
		int y0 = e.getY();

		if (e.getButton() == MouseEvent.BUTTON3)
		// if(e.isPopupTrigger())
		{
			popup.show(tree, x0, y0);
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Quits the app.
	 */
	public void quit() {
		saveIfModified();
	}

	/**
	 * The program entry point.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		final String OPTION_FILE = "trefile";
		final String OPTION_USAGE = "usage";
		Options opt = new Options();
		opt.addOption("f", OPTION_FILE, true, "the TRE file");
		opt.addOption("?", OPTION_USAGE, false, "print this message");

		try {
			String treFile = "";

			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(opt, args);

			if (cmd.hasOption(OPTION_FILE)) {
				treFile = cmd.getOptionValue(OPTION_FILE);
			}
			if (cmd.hasOption(OPTION_USAGE)) {
				throw new Exception();
			}

			TreeEditor.log.info("TRE file specified: " + treFile);
			new TreeEditorFrame(treFile);
		} catch (Exception e) {
			HelpFormatter help = new HelpFormatter();
			help.printHelp("TreeEditor", opt);
			TreeEditor.log.error("Error starting TreeEditor:", e);
		}
	}
}
