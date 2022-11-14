package com.example.pong_proj;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class App extends GameApplication {

    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setTitle("Pong");
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static final int P_W = 10; //paddle width
    private static final int P_H = 100; //height
    private static final int B_SIZE= 10; //ball size
    private static final int P_SPD = 5; //paddle speed
    private static final int B_SPD = 1; //ball speed

    private Entity paddle1, paddle2, ball; //FXGL Game Objects

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> paddle1.translateY(-P_SPD));
        onKey(KeyCode.S, () -> paddle1.translateY(P_SPD));

        onKey(KeyCode.UP, () -> paddle2.translateY(-P_SPD));
        onKey(KeyCode.DOWN, () -> paddle2.translateY(P_SPD));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        //
        vars.put("score1", 0);
        vars.put("score2", 0);
    }

    @Override
    protected void initGame() {
        //spawns paddles and ball into game world
        paddle1 = spawnPaddle(0, getAppHeight() / 2 - P_H / 2);
        paddle2 = spawnPaddle(getAppWidth() - P_W, getAppHeight() / 2 - P_H / 2);

        ball = spawnBall(getAppWidth() / 2 - B_SIZE / 2, getAppHeight() / 2 - B_SIZE / 2);
    }

    private Entity spawnPaddle(double x, double y) {
        return entityBuilder()
                .at(x, y) //create paddles at x, y
                .viewWithBBox(new Rectangle(P_W, P_H)) //create the viewable object [rect]
                .buildAndAttach(); //add paddles to game world
    }

    private Entity spawnBall(double x, double y) {
        return entityBuilder()
                .at(x, y) //create ball at x, y
                .viewWithBBox(new Rectangle(B_SIZE, B_SIZE)) //create viewable object
                .with("velocity", new Point2D(B_SPD, B_SPD)) //new entity type "velocity"
                .buildAndAttach(); //add ball to game world
    }

    @Override
    protected void initUI() {
        Text textScore1 = getUIFactoryService().newText("", Color.BLACK, 22);
        Text textScore2 = getUIFactoryService().newText("", Color.BLACK, 22);

        textScore1.textProperty().bind(getip("score1").asString());
        textScore2.textProperty().bind(getip("score2").asString());

        addUINode(textScore1, 10, 50);
        addUINode(textScore2, getAppWidth() - 30, 50);
    }

    @Override
    protected void onUpdate(double tpf) {
        Point2D velocity = ball.getObject("velocity");
        ball.translate(velocity);

        if (ball.getX() == paddle1.getRightX()
            && ball.getY() < paddle1.getBottomY()
            && ball.getBottomY() > paddle1.getY()
        ) ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));

        if (ball.getRightX() == paddle2.getX()
            && ball.getY() < paddle2.getBottomY()
            && ball.getBottomY() > paddle2.getY()
        ) ball.setProperty("velocity", new Point2D(-velocity.getX(), velocity.getY()));

        if (ball.getX() <= 0) {
            inc("score2", +1);
            resetBall();
        }

        if (ball.getRightX() >= getAppWidth()) {
            inc("score1", +1);
            resetBall();
        }

        if (ball.getY() <= 0) {
            ball.setY(0);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

        if (ball.getBottomY() >= getAppHeight()) {
            ball.setY(getAppHeight() - B_SIZE);
            ball.setProperty("velocity", new Point2D(velocity.getX(), -velocity.getY()));
        }

    }

    private void resetBall() {
        ball.setPosition(getAppWidth() / 2 - B_SIZE / 2, getAppHeight() / 2 - B_SIZE / 2);
        ball.setProperty("velocity", new Point2D(B_SPD, B_SPD));
    }
}
