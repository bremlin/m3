package mz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import mz.xls.read.XLSReaderSource;

import java.io.File;

public class Controller {

    @FXML
    public TextField filePath;
    @FXML
    public TextField filePrimaPath;

    private File file;
    private File filePrima;

    private XLSReaderSource xlsReaderSource;

    private String directory;

    public void close() {
        System.exit(0);
    }

    public void start(ActionEvent actionEvent) {
        xlsReaderSource = new XLSReaderSource(file, filePrima);
        Node source = (Node)  actionEvent.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void chooseFile(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window theStage = source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать файл с исходными данными");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
        file = fileChooser.showOpenDialog(theStage);

        if (file != null) {
            filePath.setText(file.getAbsolutePath());
            directory = file.getParent();
        }
    }

    public void choosePrima(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Window theStage = source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать файл с данными Primavera");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
        fileChooser.setInitialDirectory(new File(directory));
        filePrima = fileChooser.showOpenDialog(theStage);

        if (filePrima != null) {
            filePrimaPath.setText(filePrima.getAbsolutePath());
        }
    }

    public XLSReaderSource getXlsReaderSource() {
        return xlsReaderSource;
    }

    public File getFile() {
        return file;
    }
}