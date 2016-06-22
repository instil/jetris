package jetris.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jetris.model.Block;
import jetris.model.GameController;
import jetris.model.GameModel;
import jetris.model.GameModel.GameState;
import jetris.model.GameModelListener;

public class GameView implements GameModelListener {

    private final GamePanel panel;

    public GameView(GameController controller) {
        this.panel = new GamePanel(controller);
        JFrame frame = new JFrame("Tetris");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                panel.processKeyCode(event.getKeyCode());
            }
        });
    }

    @Override
    public void modelChanged(GameModel context) {
        panel.modelChanged(context);
    }

    private static class GamePanel extends JPanel {

        private static int BLOCK_SIZE = 25;

        private static final long serialVersionUID = 494778980390601354L;
        private GameModel model;
        private final GameController controller;

        public GamePanel(GameController controller) {
            this.controller = controller;
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            setBackground(Color.BLACK);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(550, 600);
        }


        public void modelChanged(GameModel model) {
            this.model = model;
            repaint();
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (isGameInInitialState()) {
                paintInitialScreen(graphics);
            }
            else if (isGamePaused()) {
                paintPausedScreen(graphics);
            }
            else if (isGameEnded()) {
                paintEndScreen(graphics);
            }
            else {
                assert isGameActive();
                paintActiveScreen(graphics);
            }
        }

        void processKeyCode(int keyCode) {
            if (isGameInInitialState() || isGameEnded()) {
                if (keyCode == KeyEvent.VK_Y) {
                    controller.startGame();
                }
            }
            else if (isGamePaused()) {
                if (keyCode == KeyEvent.VK_P) {
                    controller.resumeGame();
                }
            }
            else {
                assert isGameActive();
                processActiveGameKeyCode(keyCode);
            }
        }

        private void processActiveGameKeyCode(int keyCode) {
            if (keyCode == KeyEvent.VK_P) {
                controller.pauseGame();
            }
            else if (keyCode == KeyEvent.VK_LEFT) {
                controller.moveLeft();
            }
            else if (keyCode == KeyEvent.VK_RIGHT) {
                controller.moveRight();
            }
            else if (keyCode == KeyEvent.VK_DOWN) {
                controller.moveDown();
            }
            else if ((keyCode == KeyEvent.VK_UP) || (keyCode == KeyEvent.VK_X)) {
                controller.rotateRight();
            }
            else if (keyCode == KeyEvent.VK_Z) {
                controller.rotateLeft();
            }
            else if (keyCode == KeyEvent.VK_Q) {
                controller.endGame();
            }
            else if (keyCode == KeyEvent.VK_SPACE) {
                controller.dropDown();
            }
        }

        private boolean isGameInInitialState() {
            return (model == null) || (model.state() == GameState.INITIAL);
        }

        private boolean isGameActive() {
            return (model != null) && (model.state() == GameState.ACTIVE);
        }

        private boolean isGamePaused() {
            return (model != null) && (model.state() == GameState.PAUSED);
        }

        private boolean isGameEnded() {
            return (model != null) && (model.state() == GameState.ENDED);
        }

        private static void paintInitialScreen(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD, 30));
            graphics.setColor(Color.WHITE);
            graphics.drawString("Start Game Y/N?", 150, 150);
        }

        private static void paintPausedScreen(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
            graphics.setColor(Color.WHITE);
            graphics.drawString("PAUSED", 210, 150);

            graphics.setFont(new Font("Arial", Font.PLAIN, 30));
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawString("CONTROL KEYS", 160, 200);

            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            graphics.setColor(Color.WHITE);
            graphics.drawString("Left Arrow", 130, 240);
            graphics.drawString("Right Arrow", 130, 265);
            graphics.drawString("Down Arrow", 130, 290);
            graphics.drawString("Space", 130, 315);
            graphics.drawString("X or Up Arrow", 130, 340);
            graphics.drawString("Z", 130, 365);
            graphics.drawString("P", 130, 415);
            graphics.drawString("Q", 130, 440);

            graphics.setFont(new Font("Arial", Font.PLAIN, 20));
            graphics.setColor(Color.WHITE);
            graphics.drawString("- Move left", 310, 240);
            graphics.drawString("- Move right", 310, 265);
            graphics.drawString("- Move down", 310, 290);
            graphics.drawString("- Drop down", 310, 315);
            graphics.drawString("- Rotate right", 310, 340);
            graphics.drawString("- Rotate left", 310, 365);
            graphics.drawString("- Pause/Resume", 310, 415);
            graphics.drawString("- Quit", 310, 440);
        }

        private static void paintEndScreen(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD, 30));
            graphics.setColor(Color.CYAN);
            graphics.drawString("Play Again Y/N?", 150, 150);
        }


        private void paintActiveScreen(Graphics graphics) {
            drawBoard(graphics);
            drawScore(graphics);
            drawLevel(graphics);
            drawLines(graphics);
            drawNextTetromino(graphics);
            drawBlocks(graphics);
            drawActiveText(graphics);
        }

        private static void drawBoard(Graphics graphics) {
            graphics.setColor(Color.GRAY);
            int depth = 17 * BLOCK_SIZE;
            int width = 10 * BLOCK_SIZE;
            graphics.fillRect(40, 60, 6, depth);
            graphics.fillRect(40, depth + 60, width + 12, 6);
            graphics.fillRect(width + 46, 60, 6, depth);
        }

        private void drawScore(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            drawText("Score", graphics, 330, 180);
            drawNumeric(model.score(), graphics, 390, 180);
        }

        private void drawLevel(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            drawText("Level", graphics, 330, 210);
            drawNumeric(model.level(), graphics, 390, 210);
        }

        private void drawLines(Graphics graphics) {
            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            drawText("Lines", graphics, 330, 240);
            drawNumeric(model.lines(), graphics, 390, 240);
        }

        private static void drawActiveText(Graphics graphics) {
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 50));
            graphics.drawString("Jetris", 200, 40);
            graphics.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 13));
            graphics.drawString("Press P to pause game", 40, 510);
        }

        private void drawNextTetromino(Graphics graphics) {
            for (Block block : model.nextTetromino()) {
                drawNextBlock(block, graphics);
            }
        }

        private static void drawNextBlock(Block block, Graphics graphics) {
            graphics.setColor(colorOf(block));
            int x = 330 + (block.x() * BLOCK_SIZE);
            int y = 100 + (block.y() * BLOCK_SIZE);
            graphics.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        }

        private void drawBlocks(Graphics graphics) {
            for (Block block : model.blocks()) {
                if (block.y() >= 0) {
                    drawBoardBlock(block, graphics);
                }
            }
        }

        private static void drawBoardBlock(Block block, Graphics graphics) {
            graphics.setColor(colorOf(block));
            int x = 46 + (block.x() * BLOCK_SIZE);
            int y = 60 + (block.y() * BLOCK_SIZE);
            graphics.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
        }

        private static Color colorOf(Block block) {
            switch (block.color()) {
                case BLUE:
                    return Color.BLUE;
                case CYAN:
                    return Color.CYAN;
                case GREEN:
                    return Color.GREEN;
                case MAGENTA:
                    return Color.MAGENTA;
                case ORANGE:
                    return Color.ORANGE;
                case RED:
                    return Color.RED;
                default:
                    assert block.color() == Block.Color.YELLOW;
                    return Color.YELLOW;
            }
        }

        private static void drawText(String text, Graphics graphics, int x, int y) {
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawString(text, x, y);
        }

        private static void drawNumeric(int value, Graphics graphics, int x, int y) {
            graphics.setColor(Color.GREEN);
            graphics.drawString(String.valueOf(value), x, y);
        }
    }



}
