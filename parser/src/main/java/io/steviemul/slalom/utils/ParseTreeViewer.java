package io.steviemul.slalom.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.tree.ParseTree;

@Slf4j
public class ParseTreeViewer extends TreeViewer {

  private ParseTreeViewer(String[] ruleNames, ParseTree parseTree) {
    super(Arrays.asList(ruleNames), parseTree);
  }

  public static void showTree(String[] ruleNames, ParseTree parseTree) {
    CountDownLatch latch = new CountDownLatch(1);

    SwingUtilities.invokeLater(() -> {
      showTree(ruleNames, parseTree, (e) -> {
        latch.countDown();
      });
    });

    try {
      latch.await();
    } catch (Exception e) {
      log.error("Error showing window", e);
    }
  }

  public static void showTree(String[] ruleNames, ParseTree parseTree, Consumer<WindowEvent> onClose) {

    TreeViewer viewer = new TreeViewer(Arrays.asList(ruleNames), parseTree);

    JFrame dialog = TreeViewer.showInDialog(viewer);

    dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    dialog.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        onClose.accept(e);
      }
    });

    dialog.pack();
    dialog.setVisible(true);
  }
}
