package io.steviemul.slalom.utils;

import java.util.Arrays;
import javax.swing.JFrame;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;

public class ParseTreeViewer extends TreeViewer {

  private ParseTreeViewer(String[] ruleNames, ParseTree parseTree) {
    super(Arrays.asList(ruleNames), parseTree);
  }

  public static void showTree(String[] ruleNames, ParseTree parseTree) {

    TreeViewer viewer = new TreeViewer(Arrays.asList(ruleNames), parseTree);

    JFrame dialog = TreeViewer.showInDialog(viewer);

    dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    dialog.pack();
    dialog.setVisible(true);
  }
}
