package mz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;
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

    @FXML
    public RadioMenuItem radioPvM3;
    @FXML
    public RadioMenuItem radioPvStandart;

    private File file;
    private File filePrima;

    private XLSReaderSource xlsReaderSource;

    private String directory;

    private boolean pvStandart = false;

    public void close() {
        System.exit(0);
    }

    public void start(ActionEvent actionEvent) {
        xlsReaderSource = new XLSReaderSource(file, filePrima, pvStandart);
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

    public void pvTypeChange(ActionEvent actionEvent) {
        RadioMenuItem tempItem = (RadioMenuItem) actionEvent.getSource();
        if (tempItem.getId().equals("radioPvStandart")) {
            if (radioPvStandart.isSelected()) {
                radioPvM3.setSelected(false);
            } else {
                radioPvStandart.setSelected(true);
            }
            pvStandart = true;
        } else {
            if (radioPvM3.isSelected()) {
                radioPvStandart.setSelected(false);
            } else {
                radioPvM3.setSelected(true);
            }
            pvStandart = false;
        }
    }
}