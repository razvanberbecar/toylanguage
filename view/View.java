package view;

import controller.Controller;
import model.exception.MyException;
import model.expression.*;
import model.state.*;
import model.statement.*;
import model.type.*;
import model.value.*;
import repository.Repository;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.stream.Collectors;


public class View extends Application {

    private static class ProgramExample {
        final Statement stmt;
        final String display;
        ProgramExample(Statement s) { this.stmt = s; this.display = s.toString(); }
        @Override public String toString() { return display; }
    }

    public static class HeapRow {
        private final SimpleIntegerProperty address;
        private final SimpleStringProperty value;
        public HeapRow(int address, String value) { this.address = new SimpleIntegerProperty(address); this.value = new SimpleStringProperty(value); }
        public int getAddress() { return address.get(); }
        public String getValue() { return value.get(); }
    }

    public static class SymRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty value;
        public SymRow(String name, String value) { this.name = new SimpleStringProperty(name); this.value = new SimpleStringProperty(value); }
        public String getName() { return name.get(); }
        public String getValue() { return value.get(); }
    }

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws MyException {
        var examples = buildExamples();
        Stage selectStage = buildSelectionStage(examples);
        selectStage.show();
    }

    private Stage buildSelectionStage(List<ProgramExample> examples) {
        Stage stage = new Stage();
        stage.setTitle("Select a program");

        ListView<ProgramExample> listView = new ListView<>();
        listView.setItems(FXCollections.observableArrayList(examples));
        listView.getSelectionModel().selectFirst();

        Button openBtn = new Button("Open selected program");
        openBtn.setOnAction(e -> {
            ProgramExample ex = listView.getSelectionModel().getSelectedItem();
            if (ex == null) return;
            try {
                openProgram(ex);
            } catch (Exception ex1) {
                showError("Failed to open program: " + ex1.getMessage());
            }
        });

        VBox root = new VBox(10, new Label("Programs:"), listView, openBtn);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 800, 500));
        return stage;
    }

    private void openProgram(ProgramExample example) {
        ProgramState prg = new ProgramState(
                new StackExecutionStack(),
                new MapSymbolTable(),
                new ListOut(),
                example.stmt,
                new MapFileTable(),
                new MapHeap()
        );
        Repository repo = new Repository("log_gui.txt");
        controller = new Controller(repo);
        controller.add(prg);

        Stage main = buildMainWindow();
        main.show();
        refreshAllViews();
    }

    private TextField nrPrgStatesField;
    private TableView<HeapRow> heapTable;
    private ListView<String> outList;
    private ListView<String> fileTableList;
    private ListView<Integer> prgIdList;
    private TableView<SymRow> symTable;
    private ListView<String> exeStackList;

    private Stage buildMainWindow() {
        Stage stage = new Stage();
        stage.setTitle("Toy Language - GUI");

        nrPrgStatesField = new TextField();
        nrPrgStatesField.setEditable(false);
        nrPrgStatesField.setPrefWidth(80);

        heapTable = new TableView<>();
        TableColumn<HeapRow, Integer> addrCol = new TableColumn<>("Address");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<HeapRow, String> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        heapTable.getColumns().addAll(addrCol, valCol);
        heapTable.setPrefHeight(200);

        outList = new ListView<>();

        fileTableList = new ListView<>();

        prgIdList = new ListView<>();
        prgIdList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> refreshSymAndStack());

        symTable = new TableView<>();
        TableColumn<SymRow, String> varCol = new TableColumn<>("Variable");
        varCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<SymRow, String> symValCol = new TableColumn<>("Value");
        symValCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        symTable.getColumns().addAll(varCol, symValCol);

        exeStackList = new ListView<>();

        Button runBtn = new Button("Run one step");
        runBtn.setOnAction(e -> {
            try {
                controller.runOneStepForAll();
                refreshAllViews();
                // Consider finished only if the repository has no more active program states
                List<ProgramState> current = controller.getAll();
                if (current.isEmpty()) {
                    showInfo("Program finished");
                }
            } catch (MyException ex) {
                showInfo(ex.getMessage());
                refreshAllViews();
            } catch (Exception ex) {
                showError("Runtime error: " + ex.getMessage());
            }
        });

        GridPane top = new GridPane();
        top.setHgap(10); top.setVgap(5); top.setPadding(new Insets(10));
        top.add(new Label("No. of PrgStates:"), 0, 0);
        top.add(nrPrgStatesField, 1, 0);

        VBox left = new VBox(10,
                new Label("Heap"), heapTable,
                new Label("Out"), outList,
                new Label("FileTable"), fileTableList
        );
        left.setPadding(new Insets(10));

        VBox right = new VBox(10,
                new Label("Program IDs"), prgIdList,
                new Label("Symbol Table"), symTable,
                new Label("Execution Stack"), exeStackList,
                runBtn
        );
        right.setPadding(new Insets(10));

        HBox body = new HBox(10, left, right);
        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(body);

        stage.setScene(new Scene(root, 1000, 700));
        return stage;
    }

    private void refreshAllViews() {
        if (controller == null) return;
        List<ProgramState> states = controller.getAll();
        nrPrgStatesField.setText(String.valueOf(states.size()));

        // Heap
        ObservableList<HeapRow> heapRows = FXCollections.observableArrayList();
        if (!states.isEmpty()) {
            var heap = states.get(0).getHeap();
            for (Map.Entry<Integer, model.value.Value> e : heap.getContent().entrySet()) {
                heapRows.add(new HeapRow(e.getKey(), e.getValue().toString()));
            }
        }
        heapTable.setItems(heapRows);

        ObservableList<String> outItems = FXCollections.observableArrayList(parseListToStringItems(states));
        outList.setItems(outItems);

        ObservableList<String> fileItems = FXCollections.observableArrayList(parseFileTableItems(states));
        fileTableList.setItems(fileItems);

        ObservableList<Integer> ids = FXCollections.observableArrayList(states.stream().map(ProgramState::getId).collect(Collectors.toList()));
        prgIdList.setItems(ids);
        if (!ids.isEmpty() && prgIdList.getSelectionModel().getSelectedItem() == null) {
            prgIdList.getSelectionModel().selectFirst();
        }

        refreshSymAndStack();
    }

    private void refreshSymAndStack() {
        List<ProgramState> states = controller.getAll();
        Integer selId = prgIdList.getSelectionModel().getSelectedItem();
        ProgramState selected = null;
        if (selId != null) {
            for (ProgramState s : states) if (s.getId() == selId) { selected = s; break; }
        }
        if (selected == null && !states.isEmpty()) selected = states.get(0);

        ObservableList<SymRow> symRows = FXCollections.observableArrayList();
        if (selected != null) {
            for (Map.Entry<String, model.value.Value> e : selected.getSymbolTable().getContent().entrySet()) {
                symRows.add(new SymRow(e.getKey(), e.getValue().toString()));
            }
        }
        symTable.setItems(symRows);

        ObservableList<String> stackItems = FXCollections.observableArrayList();
        if (selected != null) {
            String stackStr = selected.getExecutionStack().toString();
            // Expected format: {s1 | s2 | ...}
            if (stackStr.startsWith("{")) stackStr = stackStr.substring(1);
            if (stackStr.endsWith("}")) stackStr = stackStr.substring(0, stackStr.length()-1);
            if (!stackStr.isBlank()) {
                String[] parts = stackStr.split(" \\| ");
                // show top first (already in order in our toString)
                stackItems.addAll(Arrays.stream(parts).map(String::trim).collect(Collectors.toList()));
            }
        }
        exeStackList.setItems(stackItems);
    }

    private List<String> parseListToStringItems(List<ProgramState> states) {
        if (states.isEmpty()) return List.of();
        String outS = states.get(0).getListOut().toString(); // e.g., [1, 2]
        String content = outS;
        if (outS.startsWith("[")) content = content.substring(1);
        if (content.endsWith("]")) content = content.substring(0, content.length()-1);
        if (content.isBlank()) return List.of();
        return Arrays.stream(content.split(", ")).map(String::trim).collect(Collectors.toList());
    }

    private List<String> parseFileTableItems(List<ProgramState> states) {
        if (states.isEmpty()) return List.of();
        String ft = states.get(0).getFileTable().toString(); // file names on separate lines
        if (ft.isBlank()) return List.of();
        return Arrays.stream(ft.split("\n")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.showAndWait();
    }

    private List<ProgramExample> buildExamples() throws MyException {
        List<ProgramExample> list = new ArrayList<>();

        // Example 1: int v; v = 2; print(v)
        Statement ex1 = new CompoundStatement(
                new VariableDeclarationStatement(new IntType(), "v"),
                new CompoundStatement(
                        new AssignmentStatement(new ConstantExpression(new IntegerValue(2)), "v"),
                        new PrintStatement(new VariableExpression("v"))
                )
        );
        ex1.typecheck(new MapTypeEnvironment());
        list.add(new ProgramExample(ex1));

        // Example 2: int a; int b; a=2+3*5; b=a+1; print(b)
        Statement ex2 = new CompoundStatement(
                new VariableDeclarationStatement(new IntType(), "a"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new IntType(), "b"),
                        new CompoundStatement(
                                new AssignmentStatement(
                                        new ArithmeticalExpression("+",
                                                new ConstantExpression(new IntegerValue(2)),
                                                new ArithmeticalExpression(
                                                        "*",
                                                        new ConstantExpression(new IntegerValue(3)),
                                                        new ConstantExpression(new IntegerValue(5))
                                                )
                                        ), "a"
                                ),
                                new CompoundStatement(
                                        new AssignmentStatement(
                                                new ArithmeticalExpression("+",
                                                        new VariableExpression("a"),
                                                        new ConstantExpression(new IntegerValue(1))
                                                ), "b"
                                        ),
                                        new PrintStatement(new VariableExpression("b"))
                                )
                        )
                )
        );
        ex2.typecheck(new MapTypeEnvironment());
        list.add(new ProgramExample(ex2));

        Statement ex9 = new CompoundStatement(
                new VariableDeclarationStatement(new IntType(), "v"),
                new CompoundStatement(
                        new VariableDeclarationStatement(new RefType(new IntType()), "a"),
                        new CompoundStatement(
                                new AssignmentStatement(new ConstantExpression(new IntegerValue(10)), "v"),
                                new CompoundStatement(
                                        new HeapAllocationStatement("a", new ConstantExpression(new IntegerValue(22))),
                                        new CompoundStatement(
                                                new ForkStatement(
                                                        new CompoundStatement(
                                                                new WriteHeapStatement("a", new ConstantExpression(new IntegerValue(30))),
                                                                new CompoundStatement(
                                                                        new AssignmentStatement(new ConstantExpression(new IntegerValue(32)), "v"),
                                                                        new CompoundStatement(
                                                                                new PrintStatement(new VariableExpression("v")),
                                                                                new PrintStatement(new ReadHeapExpression(new VariableExpression("a")))
                                                                        )
                                                                )
                                                        )
                                                ),
                                                new CompoundStatement(
                                                        new PrintStatement(new VariableExpression("v")),
                                                          new PrintStatement(new ReadHeapExpression(new VariableExpression("a")))
                                                )
                                        )
                                )
                        )
                )
        );

        ex9.typecheck(new MapTypeEnvironment());
        list.add(new ProgramExample(ex9));


        return list;
    }
}
