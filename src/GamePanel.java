import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    public static final int GAME_WIDTH = 820;
    public static final int SIDE_WIDTH = 280;
    public static final int HEIGHT = 720;

    public static final int WIDTH =
            GAME_WIDTH + SIDE_WIDTH;
    public static final int UNIT_SIZE = 25;

    Timer timer;

    GameLogic logic;

    Random random = new Random();

    ArrayList<Particle> particles = new ArrayList<>();
    ArrayList<Score> leaderboard = new ArrayList<>();

    enum GameState {
        MENU,
        RUNNING,
        PAUSED,
        GAME_OVER
    }

    GameState state = GameState.MENU;

    public GamePanel() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setBackground(new Color(5, 5, 15));

        setFocusable(true);

        addKeyListener(this);

        logic = new GameLogic(
            GAME_WIDTH,
            HEIGHT,
            UNIT_SIZE
        );

        timer = new Timer(16, this);

        timer.start();

        loadScores();
    }

    public void startGame() {

        logic.startGame();

        particles.clear();

        state = GameState.RUNNING;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        switch (state) {

            case MENU:
                drawMenu(g2);
                break;

            case RUNNING:
                drawGame(g2);
                break;

            case PAUSED:
                drawGame(g2);
                drawPause(g2);
                break;

            case GAME_OVER:
                drawGameOver(g2);
                break;
        }
    }

    private void drawMenu(Graphics2D g) {

        g.setColor(Color.CYAN);

        g.setFont(new Font("Arial", Font.BOLD, 70));

        drawCentered(g, "NEON SNAKE", 220);

        g.setFont(new Font("Arial", Font.PLAIN, 28));

        drawCentered(g, "ENTER - Start", 360);

        drawCentered(g, "P - Pause", 420);

        drawCentered(g, "ESC - Quit", 480);
    }

    private void drawLevelTheme(Graphics2D g) {

        switch (logic.level) {

            case 1 -> setBackground(
                        new Color(5, 5, 20)
                );

            case 2 -> setBackground(
                        new Color(10, 40, 10)
                );

            case 3 -> setBackground(
                        new Color(180, 220, 255)
                );

            case 4 -> setBackground(
                        new Color(50, 10, 10)
                );

            default -> setBackground(
                        new Color(15, 0, 30)
                );
        }
    }

    private void drawGame(Graphics2D g) {

        drawSidePanel(g);

        drawLevelTheme(g);
        
        drawGrid(g);

        drawParticles(g);

        drawFood(g);

        drawHurdles(g);

        drawSnake(g);

    }

    private void drawSidePanel(Graphics2D g) {

        int x = GAME_WIDTH;

        // Panel Background
        g.setColor(new Color(10, 10, 25));

        g.fillRect(
                x,
                0,
                SIDE_WIDTH,
                HEIGHT
        );

        // Neon Border
        g.setColor(Color.CYAN);

        g.drawLine(
                GAME_WIDTH,
                0,
                GAME_WIDTH,
                HEIGHT
        );

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 28));

        g.drawString(
                "NEON HUD",
                GAME_WIDTH + 60,
                50
        );

        // Score Box
        g.setFont(new Font("Arial", Font.BOLD, 22));

        g.setColor(Color.WHITE);

        g.drawString(
                "Score: " + logic.applesEaten,
                GAME_WIDTH + 30,
                120
        );

        g.drawString(
                "Level: " + logic.level,
                GAME_WIDTH + 30,
                170
        );

        g.drawString(
                "Speed: " + (int)logic.speed,
                GAME_WIDTH + 30,
                220
        );

        // Leaderboard
        g.setColor(Color.MAGENTA);

        g.setFont(new Font("Arial", Font.BOLD, 26));

        g.drawString(
                "TOP PLAYERS",
                GAME_WIDTH + 35,
                320
        );

        g.setFont(new Font("Arial", Font.PLAIN, 20));

        int y = 370;

        for (int i = 0;
            i < Math.min(5, leaderboard.size());
            i++) {

            Score s = leaderboard.get(i);

            g.drawString(
                    (i + 1) + ". " +
                    s.name +
                    " - " +
                    s.score,
                    GAME_WIDTH + 30,
                    y
            );

            y += 40;
        }
    }

    private void drawGrid(Graphics2D g) {

        g.setColor(new Color(20, 20, 35));

        for (int i = 0; i < WIDTH / UNIT_SIZE; i++) {

            g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, HEIGHT);
        }

        for (int i = 0; i < HEIGHT / UNIT_SIZE; i++) {

            g.drawLine(0, i * UNIT_SIZE, WIDTH, i * UNIT_SIZE);
        }
    }

    private void drawFood(Graphics2D g) {

        int pulse = (int)(Math.sin(System.currentTimeMillis() * 0.01) * 5);

        g.setColor(new Color(255, 50, 120));

        g.fillOval(
                logic.foodX - pulse / 2,
                logic.foodY - pulse / 2,
                UNIT_SIZE + pulse,
                UNIT_SIZE + pulse
        );
    }

    private void drawSnake(Graphics2D g) {

        for (int i = logic.bodyParts - 1; i >= 0; i--) {

            float alpha = 1f - ((float)i / logic.bodyParts);

            if (i == 0) {

                g.setColor(new Color(0, 255, 180));

            } else {

                g.setColor(new Color(
                        0,
                        (int)(200 * alpha),
                        120
                ));
            }

            int size = UNIT_SIZE;

            if (i == 0) size += 4;

            g.fillRoundRect(
                    (int)logic.x[i],
                    (int)logic.y[i],
                    size,
                    size,
                    18,
                    18
            );

            if (i == 0) {

                g.setColor(Color.WHITE);

                g.fillOval(
                        (int)logic.x[0] + 5,
                        (int)logic.y[0] + 5,
                        6,
                        6
                );

                g.fillOval(
                        (int)logic.x[0] + 15,
                        (int)logic.y[0] + 5,
                        6,
                        6
                );

                g.setColor(Color.RED);

                if (System.currentTimeMillis() % 400 < 200) {

                    g.drawLine(
                            (int)logic.x[0] + 12,
                            (int)logic.y[0] + 24,
                            (int)logic.x[0] + 12,
                            (int)logic.y[0] + 34
                    );
                }
            }
        }
    }


    private void drawHurdles(Graphics2D g) {

        g.setColor(new Color(120, 120, 120));

        for (Rectangle r : logic.hurdles) {

            g.fillRoundRect(
                    r.x,
                    r.y,
                    r.width,
                    r.height,
                    12,
                    12
            );
        }
    }

    private void drawParticles(Graphics2D g) {

        for (int i = 0; i < 4; i++) {

            particles.add(new Particle(
                    random.nextInt(WIDTH),
                    random.nextInt(HEIGHT)
            ));
        }

        for (Particle p : particles) {

            p.update();

            p.draw(g);
        }
    }

    private void drawPause(Graphics2D g) {

        g.setColor(new Color(0, 0, 0, 180));

        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.YELLOW);

        g.setFont(new Font("Arial", Font.BOLD, 70));

        drawCentered(g, "PAUSED", HEIGHT / 2);
    }

    private void drawGameOver(Graphics2D g) {

        drawGame(g);

        g.setColor(new Color(0, 0, 0, 200));

        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.RED);

        g.setFont(new Font("Arial", Font.BOLD, 80));

        drawCentered(g, "GAME OVER", 250);

        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 35));

        drawCentered(
                g,
                "Final Score: " + logic.applesEaten,
                350
        );

        drawCentered(
                g,
                "Press ENTER To Restart",
                430
        );
    }

    private void drawCentered(Graphics2D g, String text, int y) {

        FontMetrics metrics = g.getFontMetrics();

        int x = (WIDTH - metrics.stringWidth(text)) / 2;

        g.drawString(text, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (state == GameState.RUNNING) {

            logic.move();

            logic.checkFood();

            logic.checkCollisions();

            if (!logic.running) {

                String name = JOptionPane.showInputDialog(
                        this,
                        "Enter your name:"
                );

                if (name == null || name.trim().isEmpty()) {

                    name = "Anonymous";
                }

                leaderboard.add(
                        new Score(name, logic.applesEaten)
                );

                leaderboard.sort((a, b) -> b.score - a.score);

                saveScores();

                state = GameState.GAME_OVER;
            }
        }

        repaint();
    }

    private void saveScores() {

        try {

            BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter("scores.txt")
                    );

            for (Score s : leaderboard) {

                writer.write(s.name + "," + s.score);

                writer.newLine();
            }

            writer.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void loadScores() {

        try {

            BufferedReader reader =
                    new BufferedReader(
                            new FileReader("scores.txt")
                    );

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",");

                leaderboard.add(
                        new Score(
                                data[0],
                                Integer.parseInt(data[1])
                        )
                );
            }

            reader.close();

        } catch (Exception e) {

        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int key = e.getKeyCode();

        if (state == GameState.MENU) {

            if (key == KeyEvent.VK_ENTER) {

                startGame();
            }

            if (key == KeyEvent.VK_ESCAPE) {

                System.exit(0);
            }
        }

        else if (state == GameState.GAME_OVER) {

            if (key == KeyEvent.VK_ENTER) {

                startGame();
            }
        }

        else {

            switch (key) {

                case KeyEvent.VK_LEFT:

                    if (logic.direction != 'R') {
                        logic.direction = 'L';
                    }

                    break;

                case KeyEvent.VK_RIGHT:

                    if (logic.direction != 'L') {
                        logic.direction = 'R';
                    }

                    break;

                case KeyEvent.VK_UP:

                    if (logic.direction != 'D') {
                        logic.direction = 'U';
                    }

                    break;

                case KeyEvent.VK_DOWN:

                    if (logic.direction != 'U') {
                        logic.direction = 'D';
                    }

                    break;

                case KeyEvent.VK_P:

                    if (state == GameState.RUNNING) {

                        state = GameState.PAUSED;

                    } else if (state == GameState.PAUSED) {

                        state = GameState.RUNNING;
                    }

                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

class Score {

    String name;

    int score;

    public Score(String name, int score) {

        this.name = name;

        this.score = score;
    }
}

class Particle {

    float x;
    float y;

    float speed;

    int size;

    public Particle(int x, int y) {

        this.x = x;
        this.y = y;

        speed = 1 + (float)Math.random() * 2;

        size = 2 + (int)(Math.random() * 4);
    }

    public void update() {

        y += speed;

        if (y > GamePanel.HEIGHT) {

            y = 0;
        }
    }

    public void draw(Graphics2D g) {

        g.setColor(new Color(0, 255, 255, 120));

        g.fillOval((int)x, (int)y, size, size);
    }
}