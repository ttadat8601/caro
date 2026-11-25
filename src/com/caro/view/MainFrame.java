package com.caro.view;

import com.caro.controller.GameController;
import com.caro.model.Board;
import com.caro.model.GameMode;
import com.caro.model.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Cửa sổ chính của ứng dụng Game Cờ Caro.
 * Chứa MenuBar, Bàn cờ trung tâm, Bảng điểm bên phải và Thanh điều khiển bên dưới.
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Board board;
    private final GameState gameState;
    private final GameController controller;

    private CaroBoardPanel boardPanel;
    private ScoreBoardPanel scoreBoardPanel;
    private ControlPanel controlPanel;

    public MainFrame(Board board, GameState gameState, GameController controller) {
        super("GAME CỜ CARO (GOMOKU) - ĐỒ ÁN MÔN HỌC CS311A1");
        this.board = board;
        this.gameState = gameState;
        this.controller = controller;

        initUI();
        initMenuBar();
        initKeyBindings();

        controller.setViews(this, boardPanel, scoreBoardPanel, controlPanel);
        boardPanel.setController(controller);
        controlPanel.setController(controller);

        controller.startNewGame();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1040, 780);
        setMinimumSize(new Dimension(860, 680));
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(new Color(245, 245, 245));

        // 1. Panel bàn cờ chính giữa
        boardPanel = new CaroBoardPanel(board, gameState);
        mainContainer.add(boardPanel, BorderLayout.CENTER);

        // 2. Bảng điểm và trạng thái bên phải
        scoreBoardPanel = new ScoreBoardPanel(gameState);
        mainContainer.add(scoreBoardPanel, BorderLayout.EAST);

        // 3. Thanh nút điều khiển bên dưới
        controlPanel = new ControlPanel(gameState);
        mainContainer.add(controlPanel, BorderLayout.SOUTH);

        setContentPane(mainContainer);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // 1. Menu Trò chơi
        JMenu menuGame = new JMenu("Trò chơi");
        menuGame.setMnemonic(KeyEvent.VK_T);

        JMenuItem itemNew = new JMenuItem("Ván mới");
        itemNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        itemNew.addActionListener(e -> controller.startNewGame());
        menuGame.add(itemNew);

        menuGame.addSeparator();

        JMenuItem itemSave = new JMenuItem("Lưu ván cờ...");
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        itemSave.addActionListener(e -> controller.saveGame());
        menuGame.add(itemSave);

        JMenuItem itemLoad = new JMenuItem("Mở ván cờ...");
        itemLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        itemLoad.addActionListener(e -> controller.loadGame());
        menuGame.add(itemLoad);

        menuGame.addSeparator();

        JMenuItem itemUndo = new JMenuItem("Đi lại (Undo)");
        itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        itemUndo.addActionListener(e -> controller.undoMove());
        menuGame.add(itemUndo);

        JMenuItem itemRedo = new JMenuItem("Làm lại (Redo)");
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        itemRedo.addActionListener(e -> controller.redoMove());
        menuGame.add(itemRedo);

        menuGame.addSeparator();

        JMenuItem itemExit = new JMenuItem("Thoát trò chơi");
        itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        itemExit.addActionListener(e -> System.exit(0));
        menuGame.add(itemExit);

        menuBar.add(menuGame);

        // 2. Menu Chế độ
        JMenu menuMode = new JMenu("Chế độ chơi");
        menuMode.setMnemonic(KeyEvent.VK_C);

        JMenuItem itemPVP = new JMenuItem("Đấu 2 Người (PvP)");
        itemPVP.addActionListener(e -> {
            controller.changeGameMode(GameMode.PVP);
            controlPanel.syncModeUI(GameMode.PVP);
        });
        menuMode.add(itemPVP);

        JMenuItem itemPVE = new JMenuItem("Đấu với Máy (PvE)");
        itemPVE.addActionListener(e -> {
            controller.changeGameMode(GameMode.PVE);
            controlPanel.syncModeUI(GameMode.PVE);
        });
        menuMode.add(itemPVE);

        menuBar.add(menuMode);

        // 3. Menu Cài đặt
        JMenu menuSettings = new JMenu("Cài đặt");
        menuSettings.setMnemonic(KeyEvent.VK_D);

        JMenuItem itemConfig = new JMenuItem("Tùy chọn thông số...");
        itemConfig.addActionListener(e -> controller.openSettings());
        menuSettings.add(itemConfig);

        menuBar.add(menuSettings);

        // 4. Menu Trợ giúp
        JMenu menuHelp = new JMenu("Trợ giúp");
        menuHelp.setMnemonic(KeyEvent.VK_H);

        JMenuItem itemHelp = new JMenuItem("Hướng dẫn luật chơi (F1)");
        itemHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        itemHelp.addActionListener(e -> controller.openHelp());
        menuHelp.add(itemHelp);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private void initKeyBindings() {
        JRootPane root = getRootPane();

        // F2: Ván mới
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "NEW_GAME");
        root.getActionMap().put("NEW_GAME", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.startNewGame();
            }
        });

        // F1: Hướng dẫn
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "HELP");
        root.getActionMap().put("HELP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.openHelp();
            }
        });

        // Ctrl+Z: Undo
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK), "UNDO");
        root.getActionMap().put("UNDO", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.undoMove();
            }
        });

        // Ctrl+Y: Redo
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK), "REDO");
        root.getActionMap().put("REDO", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.redoMove();
            }
        });

        // Ctrl+S: Save
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "SAVE");
        root.getActionMap().put("SAVE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.saveGame();
            }
        });

        // Ctrl+O: Load
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "LOAD");
        root.getActionMap().put("LOAD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.loadGame();
            }
        });
    }
}
