package org.apache.chengdb.ui.controller.product;

import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.util.ResourceBundle;

@ViewController(value = "/fxml/product/DatabaseManager.fxml", title = "ChengDB Manager")
public class DatabaseManagerController implements Initializable {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML private BorderPane root;
    @FXML private VBox navigationPane;
    @FXML private JFXTreeView<String> connectionTree;
    @FXML private JFXTabPane mainTabPane;
    @FXML private JFXButton newConnectionBtn;
    @FXML private JFXButton queryBtn;
    @FXML private JFXButton backupBtn;
    @FXML private JFXButton restoreBtn;
    @FXML private Label statusLabel;
    @FXML private Label connectionStatusLabel;

    // 工具栏按钮
    @FXML private JFXButton toolbarNewConnectionBtn;
    @FXML private JFXButton toolbarExecuteBtn;
    @FXML private JFXButton toolbarStopBtn;
    @FXML private JFXButton toolbarBackupBtn;
    @FXML private JFXButton toolbarRestoreBtn;
    @FXML private JFXButton toolbarSettingsBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupConnectionTree();
        setupEventHandlers();
        // 延迟设置图标，确保FXML完全加载
        javafx.application.Platform.runLater(() -> {
            setupIcons();
            setupToolbarIcons();
        });
    }

    public void init() {
        // 初始化完成后的操作
    }

    private void setupIcons() {
        // 手动设置图标，确保正确加载
        try {
            // 为导航栏按钮设置图标
            setupNavigationIcons();

            // 为按钮设置图标
            if (newConnectionBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) newConnectionBtn.getGraphic()).setIconCode(FontAwesomeSolid.DATABASE);
            }
            if (queryBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) queryBtn.getGraphic()).setIconCode(FontAwesomeSolid.TABLE);
            }
            if (backupBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) backupBtn.getGraphic()).setIconCode(FontAwesomeSolid.FILE_EXCEL);
            }
            if (restoreBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) restoreBtn.getGraphic()).setIconCode(FontAwesomeSolid.SYNC);
            }
        } catch (Exception e) {
            System.err.println("图标加载失败: " + e.getMessage());
        }
    }

    private void setupNavigationIcons() {
        // 为导航栏中的所有按钮重新设置图标
        try {
            // 查找导航栏中的所有按钮并设置图标
            navigationPane.lookupAll(".jfx-button").forEach(node -> {
                if (node instanceof JFXButton) {
                    JFXButton button = (JFXButton) node;
                    String buttonText = button.getText();

                    if (buttonText != null) {
                        FontIcon icon = null;
                        switch (buttonText.trim()) {
                            case "新建查询":
                                icon = new FontIcon(FontAwesomeSolid.DATABASE);
                                icon.setStyle("-fx-icon-color: #007bff;");
                                break;
                            case "表设计器":
                                icon = new FontIcon(FontAwesomeSolid.TABLE);
                                icon.setStyle("-fx-icon-color: #6f42c1;");
                                break;
                            case "数据传输":
                                icon = new FontIcon(FontAwesomeSolid.FILE_EXCEL);
                                icon.setStyle("-fx-icon-color: #fd7e14;");
                                break;
                            case "同步结构":
                                icon = new FontIcon(FontAwesomeSolid.SYNC);
                                icon.setStyle("-fx-icon-color: #20c997;");
                                break;
                            case "监控":
                                icon = new FontIcon(FontAwesomeSolid.CHART_LINE);
                                icon.setStyle("-fx-icon-color: #e83e8c;");
                                break;
                        }

                        if (icon != null) {
                            icon.setIconSize(14);
                            button.setGraphic(icon);
                        }
                    }
                }
            });

            // 设置标题区域的图标
            setupTitleIcons();

        } catch (Exception e) {
            System.err.println("导航栏图标设置失败: " + e.getMessage());
        }
    }

    private void setupTitleIcons() {
        // 为标题区域设置图标
        try {
            navigationPane.lookupAll(".label").forEach(node -> {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    String labelText = label.getText();

                    if ("  连接".equals(labelText)) {
                        FontIcon serverIcon = new FontIcon(FontAwesomeSolid.SERVER);
                        serverIcon.setStyle("-fx-icon-color: #495057;");
                        serverIcon.setIconSize(14);
                        // 将图标添加到HBox的第一个位置
                        if (label.getParent() instanceof HBox) {
                            HBox parent = (HBox) label.getParent();
                            if (parent.getChildren().size() > 0 && parent.getChildren().get(0) instanceof FontIcon) {
                                ((FontIcon) parent.getChildren().get(0)).setIconCode(FontAwesomeSolid.SERVER);
                            }
                        }
                    } else if ("  快速操作".equals(labelText)) {
                        if (label.getParent() instanceof HBox) {
                            HBox parent = (HBox) label.getParent();
                            if (parent.getChildren().size() > 0 && parent.getChildren().get(0) instanceof FontIcon) {
                                ((FontIcon) parent.getChildren().get(0)).setIconCode(FontAwesomeSolid.BOLT);
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("标题图标设置失败: " + e.getMessage());
        }
    }

    private void setupConnectionTree() {
        // 创建根节点
        TreeItem<String> rootItem = new TreeItem<>("数据库连接");
        rootItem.setExpanded(true);

        // 添加示例连接，使用图片方式设置图标
        TreeItem<String> mysqlConnection = new TreeItem<>("MySQL - 本地");
        // 加载图片
        Image mysqlImg = new Image(getClass().getResourceAsStream("/fonts/mysql.png"));
        ImageView mysqlIcon = new ImageView(mysqlImg);
        mysqlIcon.setFitWidth(16); // 调整大小
        mysqlIcon.setFitHeight(16);
        mysqlConnection.setGraphic(mysqlIcon);

        // 添加 PostgreSQL 示例连接
        TreeItem<String> postgresConnection = new TreeItem<>("PostgreSQL - 测试");
        Image pgImg = new Image(getClass().getResourceAsStream("/fonts/pgsql.png"));
        ImageView pgIcon = new ImageView(pgImg);
        pgIcon.setFitWidth(16); // 调整图标大小
        pgIcon.setFitHeight(16);
        postgresConnection.setGraphic(pgIcon);

        rootItem.getChildren().addAll(mysqlConnection, postgresConnection);
        connectionTree.setRoot(rootItem);
        connectionTree.setShowRoot(false);
    }

    private void setupEventHandlers() {
        // 导航栏按钮事件
        newConnectionBtn.setOnAction(e -> openNewConnectionDialog());
        queryBtn.setOnAction(e -> openQueryTab());
        backupBtn.setOnAction(e -> openBackupDialog());
        restoreBtn.setOnAction(e -> openRestoreDialog());
        
        // 工具栏按钮事件
        if (toolbarNewConnectionBtn != null) {
            toolbarNewConnectionBtn.setOnAction(e -> {
                statusLabel.setText("新建数据库连接...");
                openNewConnectionDialog();
            });
        }

        if (toolbarExecuteBtn != null) {
            toolbarExecuteBtn.setOnAction(e -> {
                statusLabel.setText("执行SQL查询...");
                // TODO: 实现SQL执行功能
            });
        }

        if (toolbarStopBtn != null) {
            toolbarStopBtn.setOnAction(e -> {
                statusLabel.setText("停止当前操作...");
                // TODO: 实现停止功能
            });
        }

        if (toolbarBackupBtn != null) {
            toolbarBackupBtn.setOnAction(e -> {
                statusLabel.setText("开始数据库备份...");
                openBackupDialog();
            });
        }

        if (toolbarRestoreBtn != null) {
            toolbarRestoreBtn.setOnAction(e -> {
                statusLabel.setText("开始数据库恢复...");
                openRestoreDialog();
            });
        }

        if (toolbarSettingsBtn != null) {
            toolbarSettingsBtn.setOnAction(e -> {
                statusLabel.setText("打开设置...");
                // TODO: 实现设置功能
            });
        }

        connectionTree.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                TreeItem<String> selectedItem = connectionTree.getSelectionModel().getSelectedItem();
                if (selectedItem != null && selectedItem.getParent() != null) {
                    openDatabaseTab(selectedItem.getValue());
                }
            }
        });
    }

    private void openNewConnectionDialog() {
        statusLabel.setText("打开新建连接对话框...");
        // TODO: 实现新建连接对话框
    }

    private void openQueryTab() {
        Tab queryTab = new Tab("查询");
        FontIcon queryIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        queryIcon.setStyle("-fx-icon-color: #007bff;");
        queryTab.setGraphic(queryIcon);
        // TODO: 添加查询界面内容
        mainTabPane.getTabs().add(queryTab);
        mainTabPane.getSelectionModel().select(queryTab);
        statusLabel.setText("打开查询标签页");
    }

    private void openBackupDialog() {
        statusLabel.setText("打开备份对话框...");
        // TODO: 实现备份对话框
    }

    private void openRestoreDialog() {
        statusLabel.setText("打开恢复对话框...");
        // TODO: 实现恢复对话框
    }

    private void openDatabaseTab(String connectionName) {
        Tab dbTab = new Tab(connectionName);
        FontIcon dbIcon = new FontIcon(FontAwesomeSolid.TABLE);
        dbIcon.setStyle("-fx-icon-color: #28a745;");
        dbTab.setGraphic(dbIcon);
        // TODO: 添加数据库浏览界面内容
        mainTabPane.getTabs().add(dbTab);
        mainTabPane.getSelectionModel().select(dbTab);
        statusLabel.setText("连接到: " + connectionName);
        connectionStatusLabel.setText("已连接");
    }

    private void createNavigationButtons() {
        // 如果FXML图标不工作，用代码重新创建按钮
        try {
            // 清空现有的快速操作按钮
            VBox quickActionsContainer = (VBox) navigationPane.lookup(".vbox");
            if (quickActionsContainer != null) {
                // 重新创建按钮
                recreateQuickActionButtons(quickActionsContainer);
            }
        } catch (Exception e) {
            System.err.println("重新创建导航按钮失败: " + e.getMessage());
        }
    }

    private void recreateQuickActionButtons(VBox container) {
        // 创建新的按钮
        JFXButton newQueryBtn = createNavigationButton("新建查询", FontAwesomeSolid.DATABASE, "#007bff");
        JFXButton tableDesignerBtn = createNavigationButton("表设计器", FontAwesomeSolid.TABLE, "#6f42c1");
        JFXButton dataTransferBtn = createNavigationButton("数据传输", FontAwesomeSolid.TRANSGENDER, "#fd7e14");
        JFXButton syncBtn = createNavigationButton("同步结构", FontAwesomeSolid.SYNC, "#20c997");
        JFXButton monitorBtn = createNavigationButton("监控", FontAwesomeSolid.CHART_LINE, "#e83e8c");

        // 设置事件处理器
        newQueryBtn.setOnAction(e -> openNewConnectionDialog());
        tableDesignerBtn.setOnAction(e -> openQueryTab());
        dataTransferBtn.setOnAction(e -> openBackupDialog());
        syncBtn.setOnAction(e -> openRestoreDialog());
        monitorBtn.setOnAction(e -> statusLabel.setText("打开监控面板..."));

        // 查找并替换现有按钮
        container.getChildren().removeIf(node -> node instanceof JFXButton);
        container.getChildren().addAll(newQueryBtn, tableDesignerBtn, dataTransferBtn, syncBtn,
                                     new Separator(), monitorBtn);
    }

    private JFXButton createNavigationButton(String text, FontAwesomeSolid iconCode, String iconColor) {
        JFXButton button = new JFXButton("  " + text);
        button.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        button.setPrefWidth(256);
        button.setStyle("-fx-text-fill: #495057; -fx-background-color: transparent; -fx-background-radius: 4; -fx-padding: 8 12;");

        FontIcon icon = new FontIcon(iconCode);
        icon.setIconSize(14);
        icon.setStyle("-fx-icon-color: " + iconColor + ";");
        button.setGraphic(icon);

        return button;
    }

    private void setupToolbarIcons() {
        try {
            // 设置工具栏按钮图标
            if (toolbarNewConnectionBtn != null && toolbarNewConnectionBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarNewConnectionBtn.getGraphic()).setIconCode(FontAwesomeSolid.LINK);
            }
            if (toolbarExecuteBtn != null && toolbarExecuteBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarExecuteBtn.getGraphic()).setIconCode(FontAwesomeSolid.PLAY);
            }
            if (toolbarStopBtn != null && toolbarStopBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarStopBtn.getGraphic()).setIconCode(FontAwesomeSolid.STOP);
            }
            if (toolbarBackupBtn != null && toolbarBackupBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarBackupBtn.getGraphic()).setIconCode(FontAwesomeSolid.DOWNLOAD);
            }
            if (toolbarRestoreBtn != null && toolbarRestoreBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarRestoreBtn.getGraphic()).setIconCode(FontAwesomeSolid.UPLOAD);
            }
            if (toolbarSettingsBtn != null && toolbarSettingsBtn.getGraphic() instanceof FontIcon) {
                ((FontIcon) toolbarSettingsBtn.getGraphic()).setIconCode(FontAwesomeSolid.COG);
            }

            System.out.println("工具栏图标设置完成");
        } catch (Exception e) {
            System.err.println("工具栏图标设置失败: " + e.getMessage());
            recreateToolbarIcons();
        }
    }

    private void recreateToolbarIcons() {
        try {
            if (toolbarNewConnectionBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.LINK);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #28a745;");
                toolbarNewConnectionBtn.setGraphic(icon);
            }

            if (toolbarExecuteBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.PLAY);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #007bff;");
                toolbarExecuteBtn.setGraphic(icon);
            }

            if (toolbarStopBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.STOP);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #dc3545;");
                toolbarStopBtn.setGraphic(icon);
            }

            if (toolbarBackupBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.DOWNLOAD);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #fd7e14;");
                toolbarBackupBtn.setGraphic(icon);
            }

            if (toolbarRestoreBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.UPLOAD);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #20c997;");
                toolbarRestoreBtn.setGraphic(icon);
            }

            if (toolbarSettingsBtn != null) {
                FontIcon icon = new FontIcon(FontAwesomeSolid.COG);
                icon.setIconSize(16);
                icon.setStyle("-fx-icon-color: #6c757d;");
                toolbarSettingsBtn.setGraphic(icon);
            }

            System.out.println("工具栏图标重新创建完成");
        } catch (Exception e) {
            System.err.println("工具栏图标重新创建失败: " + e.getMessage());
        }
    }
}
