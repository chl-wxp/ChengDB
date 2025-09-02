package org.apache.chengdb.ui.controller.product;

import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.IkonResolver;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class ProductDemo extends Application {

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 确保图标库正确初始化
        try {
            IkonResolver.getInstance(); // 预加载图标库
            FontAwesomeSolid.values();  // 强制加载FontAwesome
            System.out.println("图标库初始化成功");
        } catch (Exception e) {
            System.err.println("图标库初始化失败: " + e.getMessage());
        }

        // Flow 容器
        Flow flow = new Flow(DatabaseManagerController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flowContext = new ViewFlowContext();
        flowContext.register("Stage", primaryStage);
        flow.createHandler(flowContext).start(container);

        // 场景
        Scene scene = new Scene(container.getView(), 1200, 800);

        // 样式表加载（保证路径正确）
        try {
            scene.getStylesheets().add(
                    getClass().getResource("/css/jfoenix-components.css").toExternalForm()
            );
            System.out.println("样式表加载成功");
        } catch (Exception e) {
            System.err.println("样式表加载失败: " + e.getMessage());
        }

        primaryStage.setTitle("ChengDB Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}