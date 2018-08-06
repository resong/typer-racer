package com.resong.racer.structures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 *
 * @author jeff
 */
public class TrieVisualizer extends JFrame {

	/***************************************************************************
	 * INSTANCE VARIABLES
	 **************************************************************************/
	private DelegateTree<TrieNodeADT<Integer>, Integer> tree;
	private TrieADT<Integer> trie;
	private VisualizationViewer<TrieNodeADT<Integer>, Integer> viewer;
	private JPanel pnlVis;
	private JLabel lblEmptyValue;
	private JLabel lblSizeValue;
	private JList lstWords;
	private JRadioButton radAdd;
	private JRadioButton radPrefix;
	private JRadioButton radRemove;
	private JRadioButton radWord;
	private JCheckBox chkSortAscending;
	private JTextField txtAddRemWord;
	private JTextField txtCheckWord;
	private JToolBar tbZoom;
	private JButton btnZoomIn;
	private JButton btnZoomOut;
	private JButton btnClear;
	private JToggleButton btnShowDetail;
	private JButton btnAddRem;
	private JButton btnCheck;
	private Transformer<TrieNodeADT<Integer>, Shape> noDetailTransformer;
	private Transformer<TrieNodeADT<Integer>, Shape> detailTransformer;
	private DetailVertexLabelTransformer<Integer> detailedLabelTransformer;
	private Transformer<TrieNodeADT<Integer>, Font> noDetailFont;
	private Transformer<TrieNodeADT<Integer>, Font> detailFont;

	Factory<DirectedGraph<TrieNodeADT<Integer>, Integer>> graphFactory = new Factory<DirectedGraph<TrieNodeADT<Integer>, Integer>>() {

		public DirectedGraph<TrieNodeADT<Integer>, Integer> create() {
			return new DirectedSparseMultigraph<TrieNodeADT<Integer>, Integer>();
		}
	};
	Factory<DelegateTree<TrieNodeADT<Integer>, Integer>> treeFactory = new Factory<DelegateTree<TrieNodeADT<Integer>, Integer>>() {

		public DelegateTree<TrieNodeADT<Integer>, Integer> create() {
			return new DelegateTree<TrieNodeADT<Integer>, Integer>(graphFactory);
		}
	};
	Factory<Integer> edgeFactory = new Factory<Integer>() {

		int i = 0;

		public Integer create() {
			return i++;
		}
	};

	/**
	 * Creates the main window
	 */
	public TrieVisualizer() {
		trie = new Trie<Integer>();
		initComponents();

	}

	private class DetailShapeTransformer<V> extends AbstractVertexShapeTransformer<V> implements Transformer<V, Shape> {

		public DetailShapeTransformer() {

			setSizeTransformer(new Transformer<V, Integer>() {

				public Integer transform(V v) {
					return 120;
				}
			});

			setAspectRatioTransformer(new Transformer<V, Float>() {

				public Float transform(V v) {
					return 0.5f;
				}
			});
		}

		public Shape transform(V i) {
			return factory.getRoundRectangle(i);
		}
	}

	private class DetailVertexLabelTransformer<T> implements Transformer<TrieNodeADT<T>, String> {

		public String transform(TrieNodeADT<T> i) {

			try {
				Class klazz = i.getClass();
				String lines = "<html>";

				for (Field f : klazz.getDeclaredFields()) {

					f.setAccessible(true);
					Object data = f.get(i);
					lines += f.getName() + ": ";

					if (data == null)
						lines += "null";
					else
						lines += data.toString();

					lines += "<br>";
				}

				return lines + "</html>";
			} catch (Exception ex) {
				System.out.println(ex.toString());
				return "ERROR";
			}
		}

	}

	private TreeLayout getTreeLayout() {
		TreeLayout<TrieNodeADT<Integer>, Integer> layout;

		if ((this.btnShowDetail != null) && (this.btnShowDetail.isSelected()))
			layout = new TreeLayout<TrieNodeADT<Integer>, Integer>(tree, 200, 150);
		else
			layout = new TreeLayout<TrieNodeADT<Integer>, Integer>(tree, 50, 50);

		return layout;
	}

	/**
	 * Initializes the components used to visualize the trie
	 */
	private JPanel createTreeVisualizer() {

		tree = treeFactory.create();
		tree.addVertex(trie.getRoot());

		viewer = new VisualizationViewer<TrieNodeADT<Integer>, Integer>(this.getTreeLayout());
		viewer.setBackground(Color.white);

		this.noDetailTransformer = viewer.getRenderContext().getVertexShapeTransformer();
		this.detailTransformer = new DetailShapeTransformer();
		this.detailedLabelTransformer = new DetailVertexLabelTransformer<Integer>();
		this.noDetailFont = new Transformer<TrieNodeADT<Integer>, Font>() {

			public Font transform(TrieNodeADT<Integer> i) {
				return new Font("SansSerif", Font.BOLD, 14);
			}
		};

		this.detailFont = new Transformer<TrieNodeADT<Integer>, Font>() {

			public Font transform(TrieNodeADT<Integer> i) {
				return new Font("SansSerif", Font.PLAIN, 9);
			}
		};

		// Setup up a new vertex to paint transformer...
		Transformer<TrieNodeADT<Integer>, Paint> vertexPaint = new Transformer<TrieNodeADT<Integer>, Paint>() {

			public Paint transform(TrieNodeADT<Integer> i) {
				return Color.GREEN;
			}
		};

		viewer.getRenderContext().setVertexShapeTransformer(this.noDetailTransformer);
		viewer.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		viewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		viewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		viewer.setVertexToolTipTransformer(this.detailedLabelTransformer);
		viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		viewer.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.BLACK));
		viewer.getRenderContext().setVertexFontTransformer(this.noDetailFont);

		DefaultModalGraphMouse treeMouse = new DefaultModalGraphMouse();
		viewer.setGraphMouse(treeMouse);
		treeMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		final ScalingControl treeScaler = new CrossoverScalingControl();

		btnShowDetail = new JToggleButton("Show Detail");
		btnShowDetail.setSelected(false);
		btnShowDetail.setHorizontalTextPosition(SwingConstants.CENTER);
		btnShowDetail.setVerticalTextPosition(SwingConstants.BOTTOM);

		btnShowDetail.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (btnShowDetail.isSelected()) {
					viewer.getRenderContext().setVertexShapeTransformer(detailTransformer);
					viewer.getRenderContext().setVertexLabelTransformer(detailedLabelTransformer);
					viewer.getRenderContext().setVertexFontTransformer(detailFont);
					viewer.setGraphLayout(getTreeLayout());
					updateGUI();
				} else {
					viewer.getRenderContext().setVertexShapeTransformer(noDetailTransformer);
					viewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
					viewer.getRenderContext().setVertexFontTransformer(noDetailFont);
					viewer.setGraphLayout(getTreeLayout());
					updateGUI();
				}
			}

		});

		btnClear = new JButton("Clear");
		btnClear.setFocusable(false);
		btnClear.setHorizontalTextPosition(SwingConstants.CENTER);
		btnClear.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnClear.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				trie.clear();
				tree = treeFactory.create();
				tree.setRoot(trie.getRoot());
				viewer.getGraphLayout().setGraph(tree);
				viewer.repaint();
				updateGUI();
			}
		});

		// Zoom in button
		btnZoomIn = new JButton("Zoom In");
		btnZoomIn.setFocusable(false);
		btnZoomIn.setHorizontalTextPosition(SwingConstants.CENTER);
		btnZoomIn.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnZoomIn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				treeScaler.scale(viewer, 1.1f, viewer.getCenter());
			}
		});

		// Zoom out button
		btnZoomOut = new JButton("Zoom Out");
		btnZoomOut.setFocusable(false);
		btnZoomOut.setHorizontalTextPosition(SwingConstants.CENTER);
		btnZoomOut.setText("Zoom Out");
		btnZoomOut.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnZoomOut.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				treeScaler.scale(viewer, 1 / 1.1f, viewer.getCenter());
			}
		});

		tbZoom.add(btnZoomIn);
		tbZoom.add(btnZoomOut);
		tbZoom.add(btnClear);
		tbZoom.add(btnShowDetail);

		// GraphZoomScrollPane pnlTree = new GraphZoomScrollPane(viewer);

		return viewer;

	}

	private JPanel createVisualizationPanel() {

		// Main visualization panel
		JPanel pnlVisualization = new JPanel();
		// pnlVisualization.setMaximumSize(new java.awt.Dimension(200, 200));

		// Inner visualization panel
		pnlVis = new JPanel();
		pnlVis.setBorder(BorderFactory.createTitledBorder("Trie Visualization"));
		GroupLayout pnlVisLayout = new GroupLayout(pnlVis);
		// BorderLayout pnlVisLayout = new BorderLayout();

		// Create the toolbar
		tbZoom = new JToolBar();
		tbZoom.setFloatable(false);
		tbZoom.setRollover(true);

		JPanel pnlTree = this.createTreeVisualizer();

		pnlVis.setLayout(pnlVisLayout);

		pnlVis.add(pnlTree);// , BorderLayout.CENTER);
		pnlVis.add(tbZoom);// , BorderLayout.SOUTH);

		pnlVisLayout.setHorizontalGroup(pnlVisLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(tbZoom, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
				.addGroup(pnlVisLayout.createSequentialGroup().addGap(12, 12, 12)
						.addComponent(pnlTree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGap(12, 12, 12)));
		pnlVisLayout.setVerticalGroup(pnlVisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				pnlVisLayout.createSequentialGroup()
						.addComponent(pnlTree, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(tbZoom, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)));

		GroupLayout pnlVisualizationLayout = new GroupLayout(pnlVisualization);
		pnlVisualization.setLayout(pnlVisualizationLayout);
		pnlVisualizationLayout
				.setHorizontalGroup(
						pnlVisualizationLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(GroupLayout.Alignment.TRAILING, pnlVisualizationLayout
										.createSequentialGroup().addContainerGap().addComponent(pnlVis,
												GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap()));
		pnlVisualizationLayout.setVerticalGroup(pnlVisualizationLayout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlVisualizationLayout.createSequentialGroup().addContainerGap()
						.addComponent(pnlVis, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap()));

		return pnlVisualization;

	}

	private JMenuBar createMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		JMenu mnuFile = new JMenu();
		JMenuItem mniFileExit = new JMenuItem();

		mnuFile.setText("File");

		mniFileExit.setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
		mniFileExit.setMnemonic('x');
		mniFileExit.setText("Exit");
		mniFileExit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(-1);
			}

		});
		mnuFile.add(mniFileExit);

		menuBar.add(mnuFile);

		return menuBar;
	}

	private JPanel createAddRemovePanel() {

		// Add / Remove panel
		JPanel pnlAddRem = new JPanel();
		pnlAddRem.setBorder(BorderFactory.createTitledBorder("Add / Remove Word"));

		// Button group for the Add / Remove radio buttons
		ButtonGroup bgAddRemove = new ButtonGroup();

		// Add / Remove word text field
		JLabel lblAddRemWord = new JLabel("Word:");
		txtAddRemWord = new JTextField();

		// Add radio button
		radAdd = new JRadioButton("Add");
		radAdd.setMnemonic('A');
		radAdd.setSelected(true);
		radAdd.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (!radAdd.isSelected()) {
					btnAddRem.setText("Remove");
					btnAddRem.setMnemonic('R');
				} else {
					btnAddRem.setText("Add");
					btnAddRem.setMnemonic('A');
				}
			}

		});
		bgAddRemove.add(radAdd);

		// Remove radio button
		radRemove = new JRadioButton("Remove");
		radRemove.setMnemonic('R');
		bgAddRemove.add(radRemove);

		// Add/Remove button
		btnAddRem = new JButton("Add");
		btnAddRem.setMnemonic('d');
		btnAddRem.setMaximumSize(new java.awt.Dimension(50, 30));
		btnAddRem.setMinimumSize(new java.awt.Dimension(50, 30));
		btnAddRem.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnAddRemActionPerformed(evt);
			}
		});

		// Layout for Add / Remove panel
		GroupLayout pnlAddRemLayout = new GroupLayout(pnlAddRem);
		pnlAddRem.setLayout(pnlAddRemLayout);
		pnlAddRemLayout.setHorizontalGroup(pnlAddRemLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
				GroupLayout.Alignment.TRAILING,
				pnlAddRemLayout.createSequentialGroup().addContainerGap().addGroup(pnlAddRemLayout
						.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(pnlAddRemLayout.createSequentialGroup().addComponent(radAdd)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(radRemove)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(btnAddRem, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
						.addGroup(GroupLayout.Alignment.LEADING,
								pnlAddRemLayout.createSequentialGroup().addComponent(lblAddRemWord)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtAddRemWord, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)))
						.addContainerGap()));
		pnlAddRemLayout.setVerticalGroup(pnlAddRemLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlAddRemLayout.createSequentialGroup()
						.addGroup(pnlAddRemLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblAddRemWord).addComponent(txtAddRemWord, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(
								pnlAddRemLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(radAdd)
										.addComponent(radRemove).addComponent(btnAddRem, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		return pnlAddRem;
	}

	private void removeNode(TrieNodeADT<Integer> node) {

		tree.removeVertex(node);

		TreeLayout<TrieNodeADT<Integer>, Integer> layout = this.getTreeLayout();
		LayoutTransition<TrieNodeADT<Integer>, Integer> lt = new LayoutTransition<TrieNodeADT<Integer>, Integer>(viewer,
				viewer.getGraphLayout(), layout);
		Animator animator = new Animator(lt);
		animator.start();
		viewer.repaint();
	}

	private void addEdge(TrieNodeADT<Integer> parent, TrieNodeADT<Integer> child) {

		if ((tree.containsVertex(parent)) && (tree.containsVertex(child)) && (tree.isNeighbor(parent, child))) {
			return;
		}

		int edge = tree.getEdgeCount();
		tree.addEdge(edge, parent, child);
		viewer.getRenderContext().getPickedEdgeState().pick(edge, true);

		TreeLayout<TrieNodeADT<Integer>, Integer> layout = this.getTreeLayout();
		LayoutTransition<TrieNodeADT<Integer>, Integer> lt = new LayoutTransition<TrieNodeADT<Integer>, Integer>(viewer,
				viewer.getGraphLayout(), layout);
		Animator animator = new Animator(lt);
		animator.start();
		viewer.repaint();

	}

	private void updateWordList() {
		DefaultListModel model = (DefaultListModel) this.lstWords.getModel();

		model.clear();

		Iterator<String> strings;

		if (this.chkSortAscending.isSelected())
			strings = this.trie.ascendingStringIterator();
		else
			strings = this.trie.descendingStringIterator();

		while (strings.hasNext()) {
			model.addElement(strings.next());
		}
	}

	private void updateStats() {
		this.lblEmptyValue.setText(this.trie.isEmpty() ? "Yes" : "No");
		this.lblSizeValue.setText("" + this.trie.size());
	}

	private void updateGUI() {

		this.updateTree();
		this.updateWordList();
		this.updateStats();
		this.viewer.repaint();

	}

	private void updateTree() {

		HashSet<TrieNodeADT<Integer>> treeVertices = new HashSet<TrieNodeADT<Integer>>(this.tree.getVertices());
		HashSet<TrieNodeADT<Integer>> trieVertices = new HashSet<TrieNodeADT<Integer>>();

		viewer.getRenderContext().getPickedVertexState().clear();
		viewer.getRenderContext().getPickedEdgeState().clear();

		TrieNodeADT<Integer> rootNode = this.trie.getRoot();
		java.util.ArrayDeque<TrieNodeADT<Integer>> queue = new java.util.ArrayDeque<TrieNodeADT<Integer>>();

		if (rootNode == null) {
			return;
		}

		trieVertices.add(rootNode);

		if (!tree.containsVertex(rootNode)) {
			tree.addVertex(rootNode);
			viewer.getRenderContext().getPickedVertexState().pick(rootNode, true);
		}

		queue.add(rootNode);

		while (!queue.isEmpty()) {

			TrieNodeADT<Integer> node = queue.pop();
			trieVertices.add(node);
			Iterator<TrieNodeADT<Integer>> it = node.childNodeIterator();

			while (it.hasNext()) {
				TrieNodeADT<Integer> child = it.next();
				queue.add(child);
				this.addEdge(node, child);
			}
		}

		treeVertices.removeAll(trieVertices);

		TrieNodeADT[] nodesToRemove = treeVertices.toArray(new TrieNodeADT[0]);

		for (int i = 0; i < nodesToRemove.length; i++)
			this.removeNode(nodesToRemove[i]);

	}

	private JPanel createCheckWordPrefixPanel() {

		// Check word / prefix panel
		JPanel pnlCheck = new JPanel();
		pnlCheck.setBorder(BorderFactory.createTitledBorder("Check String"));

		// Button group for the Word / Prefix radio buttons
		ButtonGroup bgWordPrefix = new ButtonGroup();

		// Word / prefix text field
		JLabel lblCheckWord = new JLabel("String");
		txtCheckWord = new JTextField();

		// Word radio button
		radWord = new JRadioButton("Word");
		radWord.setMnemonic('W');
		radWord.setSelected(true);
		radWord.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				if (!radWord.isSelected())
					btnCheck.setText("Check Prefix");
				else
					btnCheck.setText("Check Word");
			}

		});
		bgWordPrefix.add(radWord);

		// Prefix radio button
		radPrefix = new JRadioButton("Prefix");
		radPrefix.setMnemonic('P');
		bgWordPrefix.add(radPrefix);

		// Check button
		btnCheck = new JButton();
		btnCheck.setMnemonic('C');
		btnCheck.setText("Check Word");
		btnCheck.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnCheckActionPerformed(evt);
			}
		});

		// Layout for check word / prefix panel
		GroupLayout pnlCheckLayout = new GroupLayout(pnlCheck);
		pnlCheck.setLayout(pnlCheckLayout);
		pnlCheckLayout.setHorizontalGroup(pnlCheckLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlCheckLayout.createSequentialGroup().addContainerGap()
						.addGroup(pnlCheckLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(pnlCheckLayout.createSequentialGroup().addComponent(lblCheckWord)
										.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtCheckWord, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
								.addGroup(GroupLayout.Alignment.TRAILING, pnlCheckLayout.createSequentialGroup()
										.addComponent(radWord).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(radPrefix).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(btnCheck, GroupLayout.PREFERRED_SIZE, 107,
												GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		pnlCheckLayout.setVerticalGroup(pnlCheckLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlCheckLayout.createSequentialGroup()
						.addGroup(pnlCheckLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblCheckWord).addComponent(txtCheckWord, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(pnlCheckLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(btnCheck).addComponent(radPrefix).addComponent(radWord))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		return pnlCheck;
	}

	private JPanel createWordsPanel() {

		// Words panel
		JPanel pnlWords = new JPanel();
		pnlWords.setBorder(BorderFactory.createTitledBorder("Words in Trie"));
		JScrollPane scrWords = new JScrollPane();

		// Statistics label
		JLabel lblEmpty = new JLabel("Is Empty?");
		lblEmptyValue = new JLabel("Yes");
		JLabel lblSize = new JLabel("Size:");
		lblSizeValue = new JLabel("0");

		chkSortAscending = new JCheckBox("Sort Ascending", true);
		chkSortAscending.setMnemonic('S');
		chkSortAscending.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				updateWordList();
			}

		});

		// Words list
		lstWords = new JList();
		lstWords.setModel(new DefaultListModel());

		scrWords.setViewportView(lstWords);

		// Layout for words panel
		GroupLayout pnlWordsLayout = new GroupLayout(pnlWords);
		pnlWords.setLayout(pnlWordsLayout);
		pnlWordsLayout
				.setHorizontalGroup(pnlWordsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(pnlWordsLayout.createSequentialGroup().addContainerGap()
								.addGroup(pnlWordsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(scrWords, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
										.addGroup(pnlWordsLayout.createSequentialGroup().addComponent(lblSize)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(lblSizeValue).addGap(57, 57, 57).addComponent(lblEmpty)
												.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(lblEmptyValue)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32,
														Short.MAX_VALUE)
												.addComponent(chkSortAscending)))
								.addContainerGap()));
		pnlWordsLayout.setVerticalGroup(pnlWordsLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(pnlWordsLayout.createSequentialGroup()
						.addGroup(pnlWordsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(lblSize).addComponent(lblSizeValue).addComponent(lblEmpty)
								.addComponent(lblEmptyValue).addComponent(chkSortAscending))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(scrWords, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE).addContainerGap()));

		return pnlWords;
	}

	private JPanel createControlPanel() {

		// Main control panel
		JPanel pnlControl = new JPanel();

		// Create subpanels
		JPanel pnlAddRem = this.createAddRemovePanel();
		JPanel pnlCheck = this.createCheckWordPrefixPanel();
		JPanel pnlWords = this.createWordsPanel();

		// Layout for control panel
		GroupLayout pnlControlLayout = new GroupLayout(pnlControl);
		pnlControl.setLayout(pnlControlLayout);
		pnlControlLayout.setHorizontalGroup(pnlControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(GroupLayout.Alignment.TRAILING,
						pnlControlLayout.createSequentialGroup().addContainerGap()
								.addGroup(pnlControlLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addComponent(pnlWords, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(pnlAddRem, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(pnlCheck, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE))
								.addContainerGap()));
		pnlControlLayout
				.setVerticalGroup(pnlControlLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(pnlControlLayout.createSequentialGroup().addContainerGap()
								.addComponent(pnlAddRem, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(pnlCheck, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pnlWords,
										GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addContainerGap()));

		return pnlControl;

	}

	@SuppressWarnings("unchecked")
	private void initComponents() {

		// Divider
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(500);

		// Create and add the main left and right panels
		JPanel pnlVisualization = this.createVisualizationPanel();
		JPanel pnlControl = this.createControlPanel();
		splitPane.setLeftComponent(pnlVisualization);
		splitPane.setRightComponent(pnlControl);

		// Add the menu bar
		this.setJMenuBar(this.createMenuBar());
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Main layout
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(splitPane,
				GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(splitPane,
				GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE));

		pack();

		this.setExtendedState(MAXIMIZED_BOTH);
	}

	public String getStackTrace(Throwable throwable) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return result.toString();
	}

	private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {

		boolean contains = false;
		String type = "";

		try {
			if (this.radWord.isSelected()) {
				type = "word";
				contains = trie.contains(this.txtCheckWord.getText());
			} else {

				type = "prefix";
				contains = trie.containsPrefix(this.txtCheckWord.getText());
			}

			JOptionPane.showMessageDialog(this, "Trie does " + (!contains ? "NOT " : "") + "contain the " + type + " "
					+ this.txtCheckWord.getText() + ".", "Error", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\nStack trace:\n" + this.getStackTrace(ex),
					"Error", JOptionPane.ERROR_MESSAGE);
		}

		this.txtCheckWord.setText("");
	}

	private void btnAddRemActionPerformed(java.awt.event.ActionEvent evt) {

		try {
			if (this.radAdd.isSelected()) {
				trie.add(this.txtAddRemWord.getText(), (int) (100 * Math.random()));
			} else {
				trie.remove(this.txtAddRemWord.getText());
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage() + "\n\nStack trace:\n" + this.getStackTrace(ex),
					"Error", JOptionPane.ERROR_MESSAGE);

		}

		this.txtAddRemWord.setText("");
		this.updateGUI();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Ignore -- not fatal
		}

		java.awt.EventQueue.invokeLater(new Runnable() {

			public void run() {

				new TrieVisualizer().setVisible(true);
			}
		});
	}
}
