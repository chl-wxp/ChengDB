package org.apache.chengdb.ui.controller.product;

import com.jfoenix.controls.*;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.apache.chengdb.server.model.DatabaseConnection;
import org.apache.chengdb.server.service.DatabaseService;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@ViewController(value = "/fxml/product/DatabaseManager.fxml", title = "ChengDB Manager")
public class DatabaseManagerController implements Initializable {

    @FXMLViewFlowContext
    private ViewFlowContext context;

    @FXML private BorderPane root;
    @FXML private VBox navigationPane;
    @FXML private JFXTreeView<DatabaseConnection> connectionTree;
    @FXML private JFXTabPane mainTabPane;
    @FXML private JFXButton newConnectionBtn;
    @FXML private JFXButton queryBtn;
    @FXML private JFXButton backupBtn;
    @FXML private JFXButton restoreBtn;
    @FXML private Label statusLabel;
    @FXML private Label connectionStatusLabel;
    @FXML private JFXTextField searchField;
    @FXML private Label searchResultLabel;

    // 工具栏按钮
    @FXML private JFXButton toolbarNewConnectionBtn;
    @FXML private JFXButton toolbarExecuteBtn;
    @FXML private JFXButton toolbarStopBtn;
    @FXML private JFXButton toolbarBackupBtn;
    @FXML private JFXButton toolbarRestoreBtn;
    @FXML private JFXButton toolbarSettingsBtn;

    // 存储原始连接列表用于搜索
    private List<DatabaseConnection> allConnections = new ArrayList<>();
    private TreeItem<DatabaseConnection> rootItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSearchFunctionality();
        loadConnectionsFromBackend();
        System.out.println("DatabaseManager初始化完成");
    }

    private void setupSearchFunctionality() {
        if (searchField != null) {
            // 监听搜索框文本变化
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterConnections(newValue);
            });

            // 回车键搜索
            searchField.setOnAction(e -> {
                String searchText = searchField.getText();
                if (searchText != null && !searchText.trim().isEmpty()) {
                    searchAndHighlight(searchText.trim());
                }
            });

            // 清空搜索框时恢复所有连接
            searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue && searchField.getText().isEmpty()) {
                    restoreAllConnections();
                }
            });
        }
    }

    private void filterConnections(String searchText) {
        if (connectionTree == null || rootItem == null) return;

        if (searchText == null || searchText.trim().isEmpty()) {
            // 搜索框为空时显示所有连接
            restoreAllConnections();
            hideSearchResult();
            return;
        }
        
        String lowerSearchText = searchText.toLowerCase();
        List<DatabaseConnection> filteredConnections = new ArrayList<>();

        // 过滤连接
        for (DatabaseConnection connection : allConnections) {
            if (connection.getName().toLowerCase().contains(lowerSearchText) ||
                connection.getType().toLowerCase().contains(lowerSearchText) ||
                connection.getHost().toLowerCase().contains(lowerSearchText)) {
                filteredConnections.add(connection);
            }
        }

        // 更新树显示
        updateConnectionTree(filteredConnections);

        // 显示搜索结果
        showSearchResult(filteredConnections.size(), searchText);
    }

    private void searchAndHighlight(String searchText) {
        if (connectionTree == null || rootItem == null) return;

        String lowerSearchText = searchText.toLowerCase();

        // 查找匹配的连接
        for (TreeItem<DatabaseConnection> item : rootItem.getChildren()) {
            DatabaseConnection connection = item.getValue();
            if (connection.getName().toLowerCase().contains(lowerSearchText)) {
                // 选中并展开到匹配项
                connectionTree.getSelectionModel().select(item);
                connectionTree.scrollTo(connectionTree.getRow(item));

                // 更新状态
                if (statusLabel != null) {
                    statusLabel.setText("找到连接: " + connection.getName());
                }
                return;
            }
        }

        // 没找到匹配项
        if (statusLabel != null) {
            statusLabel.setText("未找到匹配的连接: " + searchText);
        }
    }

    private void restoreAllConnections() {
        updateConnectionTree(allConnections);
        hideSearchResult();
    }

    private void updateConnectionTree(List<DatabaseConnection> connections) {
        if (connectionTree == null) return;

        // 清空现有子节点
        rootItem.getChildren().clear();

        // 添加过滤后的连接
        for (DatabaseConnection connection : connections) {
            TreeItem<DatabaseConnection> connectionItem = new TreeItem<>(connection);

            // 根据数据库类型设置图标
            ImageView icon = createDatabaseIcon(connection.getType());
            if (icon != null) {
                connectionItem.setGraphic(icon);
            }

            rootItem.getChildren().add(connectionItem);
        }

        // 根节点已经隐藏，不需要手动展开
    }

    private void showSearchResult(int count, String searchText) {
        if (searchResultLabel != null) {
            searchResultLabel.setText("找到 " + count + " 个匹配 \"" + searchText + "\" 的连接");
            searchResultLabel.setVisible(true);
        }
    }

    private void hideSearchResult() {
        if (searchResultLabel != null) {
            searchResultLabel.setVisible(false);
        }
    }

    private void loadConnectionsFromBackend() {
        // 异步加载连接列表
        Platform.runLater(() -> {
            try {
                allConnections = DatabaseService.getConnectionList();
                buildConnectionTree(allConnections);
                if (statusLabel != null) {
                    statusLabel.setText("已加载 " + allConnections.size() + " 个数据库连接");
                }
            } catch (Exception e) {
                System.err.println("加载连接列表失败: " + e.getMessage());
                if (statusLabel != null) {
                    statusLabel.setText("加载连接列表失败");
                }
            }
        });
    }

    private void buildConnectionTree(List<DatabaseConnection> connections) {
        if (connectionTree == null) return;

        // 创建隐藏的根节点
        rootItem = new TreeItem<>();
        rootItem.setExpanded(true);

        // 为每个连接创建树节点
        for (DatabaseConnection connection : connections) {
            TreeItem<DatabaseConnection> connectionItem = new TreeItem<>(connection);

            // 根据数据库类型设置图标
            ImageView icon = createDatabaseIcon(connection.getType());
            if (icon != null) {
                connectionItem.setGraphic(icon);
            }

            rootItem.getChildren().add(connectionItem);
        }

        connectionTree.setRoot(rootItem);
        connectionTree.setShowRoot(false); // 隐藏根节点
    }

    private ImageView createDatabaseIcon(String dbType) {
        try {
            String iconPath = null;
            switch (dbType.toLowerCase()) {
                case "mysql":
                    iconPath = "/fonts/mysql.png";
                    break;
                case "postgresql":
                    iconPath = "/fonts/pgsql.png";
                    break;
                case "oracle":
                    // 如果有oracle图标的话
                    iconPath = "/fonts/oracle.png";
                    break;
                default:
                    // 默认数据库图标
                    return createFontIcon();
            }
            if (iconPath != null && getClass().getResource(iconPath) != null) {
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                imageView.setPreserveRatio(true);
                return imageView;
            }
        } catch (Exception e) {
            System.err.println("加载数据库图标失败: " + e.getMessage());
        }
        // 如果图片加载失败，使用FontIcon作为备选
        return createFontIcon();
    }

    private ImageView createFontIcon() {
        // 创建一个包含FontIcon的ImageView作为备选方案
        try {
            FontIcon fontIcon = new FontIcon(FontAwesomeSolid.DATABASE);
            fontIcon.setIconSize(16);
            fontIcon.setStyle("-fx-icon-color: #007bff;");

            // 这里简化处理，直接返回null，让系统使用默认图标
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void openNewConnectionDialog() {
        if (statusLabel != null) {
            statusLabel.setText("打开新建连接对话框...");
        }
        // TODO: 实现新建连接对话框
    }

    private void openQueryTab() {
        Tab queryTab = new Tab("查询");
        FontIcon queryIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        queryIcon.setStyle("-fx-icon-color: #007bff;");
        queryTab.setGraphic(queryIcon);
        // TODO: 添加查询界面内容
        if (mainTabPane != null) {
            mainTabPane.getTabs().add(queryTab);
            mainTabPane.getSelectionModel().select(queryTab);
        }
        if (statusLabel != null) {
            statusLabel.setText("打开查询标签页");
        }
    }

    private void openBackupDialog() {
        if (statusLabel != null) {
            statusLabel.setText("打开备份对话框...");
        }
        // TODO: 实现备份对话框
    }

    private void openRestoreDialog() {
        if (statusLabel != null) {
            statusLabel.setText("打开恢复对话框...");
        }
        // TODO: 实现恢复对话框
    }

    private void openDatabaseTab(String connectionName) {
        Tab dbTab = new Tab(connectionName);
        FontIcon dbIcon = new FontIcon(FontAwesomeSolid.TABLE);
        dbIcon.setStyle("-fx-icon-color: #28a745;");
        dbTab.setGraphic(dbIcon);
        // TODO: 添加数据库浏览界面内容
        if (mainTabPane != null) {
            mainTabPane.getTabs().add(dbTab);
            mainTabPane.getSelectionModel().select(dbTab);
        }
        if (statusLabel != null) {
            statusLabel.setText("连接到: " + connectionName);
        }
        if (connectionStatusLabel != null) {
            connectionStatusLabel.setText("已连接");
        }
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
