/*
 * To change this license header, choose License Headers in Project Properties.
 */
package androidfx;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 *
 * @author Luis Calado <luisfmcalado at gmail.com>
 */
public class Main extends Application {

    private Xform cameraXform;

    public Scene createContent() throws Exception {

        Label label = new Label();
        label.setVisible(false);

        Label lbaxisx = new Label();
        Label lbaxisy = new Label();
        Label lbaxisz = new Label();

        //Material
        PhongMaterial mate = new PhongMaterial();
        mate.setDiffuseColor(Color.GREEN);

        // Box
        Box testBox = new Box(3, 3, 3);
        testBox.setMaterial(mate);

        // Create and position camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().add(new Translate(0, 0, -10));

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(testBox);

        cameraXform = new Xform();
        cameraXform.getChildren().add(camera);
        root.getChildren().add(cameraXform);

        SubScene sbscene = new SubScene(root, 300, 200);
        sbscene.setCamera(camera);

        VBox p = new VBox();
        p.getChildren().add(sbscene);
        p.getChildren().add(label);
        p.getChildren().add(lbaxisx);
        p.getChildren().add(lbaxisy);
        p.getChildren().add(lbaxisz);

        VBox.setMargin(lbaxisx, new Insets(5, 5, 5, 15));
        VBox.setMargin(lbaxisy, new Insets(5, 5, 5, 15));
        VBox.setMargin(lbaxisz, new Insets(5, 5, 5, 15));
        
        //Scene
        Scene scene = new Scene(p);
        scene.setFill(Color.DARKCYAN);

        Androided s = new Androided(9999);
        Service<Void> ser = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return s;
            }
        };

        label.textProperty().bind(ser.messageProperty());
        label.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    String[] coordenadas = newValue.split(";");
                    double axisX = Double.parseDouble(coordenadas[0]);
                    double axisY = Double.parseDouble(coordenadas[1]);
                    double axisZ = Double.parseDouble(coordenadas[2]);

                    lbaxisx.setText("X: " + coordenadas[0]);
                    lbaxisy.setText("Y: " + coordenadas[1]);
                    lbaxisz.setText("Z: " + coordenadas[2]);

                    cameraXform.rx.setAngle(axisX);
                    cameraXform.ry.setAngle(axisY);
                    cameraXform.rz.setAngle(axisZ);
                } catch (NullPointerException e) {
                }
            }
        });
        ser.start();

        return scene;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setScene(createContent());
        primaryStage.setHeight(320);
        primaryStage.setWidth(300);
        primaryStage.show();
    }

    /**
     * Java main for when running without JavaFX launcher
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
