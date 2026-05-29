import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

public class GameLogic {

    int width;
    int height;
    int unitSize;

    Random random = new Random();

    public float[] x;
    public float[] y;

    public int bodyParts;
    public int applesEaten;

    public int foodX;
    public int foodY;

    public int level;

    public boolean running;

    public char direction;

    public float speed = 4f;

    public ArrayList<Rectangle> hurdles = new ArrayList<>();

    public GameLogic(int width, int height, int unitSize) {

        this.width = width;
        this.height = height;
        this.unitSize = unitSize;

        int gameUnits = (width * height) / unitSize;

        x = new float[gameUnits];
        y = new float[gameUnits];
    }

    public void startGame() {

        bodyParts = 8;
        applesEaten = 0;
        level = 1;

        direction = 'R';

        running = true;

        for (int i = 0; i < bodyParts; i++) {

            x[i] = 200 - (i * unitSize);
            y[i] = 200;
        }

        generateHurdles();

        createFood();
    }

    public void move() {

        for (int i = bodyParts; i > 0; i--) {

            x[i] += (x[i - 1] - x[i]) * 0.35f;
            y[i] += (y[i - 1] - y[i]) * 0.35f;
        }

        switch (direction) {

            case 'U' -> y[0] -= speed;

            case 'D' -> y[0] += speed;

            case 'L' -> x[0] -= speed;

            case 'R' -> x[0] += speed;
        }
    }

    public void createFood() {

        while (true) {

            foodX = random.nextInt((width - 1) / unitSize) * unitSize;
            foodY = random.nextInt(height / unitSize) * unitSize;

            boolean valid = true;

            for (Rectangle r : hurdles) {

                if (r.contains(foodX, foodY)) {
                    valid = false;
                    break;
                }
            }

            if (valid) break;
        }
    }

    public void checkFood() {

        Rectangle head = new Rectangle(
                (int)x[0],
                (int)y[0],
                unitSize,
                unitSize
        );

        Rectangle food = new Rectangle(
                foodX,
                foodY,
                unitSize,
                unitSize
        );

        if (head.intersects(food)) {

            bodyParts++;

            applesEaten++;

            if (applesEaten % 5 == 0 && level < 15) {

                level++;

                speed += 0.3f;

                generateHurdles();
            }

            createFood();
        }
    }

    public void generateHurdles() {

        hurdles.clear();

        for (int i = 0; i < level; i++) {

            boolean valid = false;

            while (!valid) {

                int hx =
                        random.nextInt(
                                (width - 200) / unitSize
                        ) * unitSize;

                int hy =
                        random.nextInt(
                                (height - 200) / unitSize
                        ) * unitSize;

                Rectangle hurdle =
                        new Rectangle(
                                hx,
                                hy,
                                unitSize * 3,
                                unitSize
                        );

                valid = true;

                // Snake collision
                for (int j = 0; j < bodyParts; j++) {

                    Rectangle snakePart =
                            new Rectangle(
                                    (int)x[j],
                                    (int)y[j],
                                    unitSize,
                                    unitSize
                            );

                    if (hurdle.intersects(snakePart)) {

                        valid = false;
                        break;
                    }
                }

                // Food collision
                Rectangle food =
                        new Rectangle(
                                foodX,
                                foodY,
                                unitSize,
                                unitSize
                        );

                if (hurdle.intersects(food)) {

                    valid = false;
                }

                if (valid) {

                    hurdles.add(hurdle);
                }
            }
        }
    }

    public void checkCollisions() {

        for (int i = 12; i < bodyParts; i++) {

            float dx = x[0] - x[i];
            float dy = y[0] - y[i];

            float distance =
                    (float)Math.sqrt(dx * dx + dy * dy);

            if (distance < unitSize * 0.55f) {

                running = false;
            }
        }

        if (x[0] < 0) running = false;
        if (x[0] > width - unitSize) running = false;
        if (y[0] < 0) running = false;
        if (y[0] > height - unitSize) running = false;

        Rectangle head = new Rectangle(
                (int)x[0],
                (int)y[0],
                unitSize,
                unitSize
        );

        for (Rectangle r : hurdles) {

            if (head.intersects(r)) {
                running = false;
            }
        }
    }
}