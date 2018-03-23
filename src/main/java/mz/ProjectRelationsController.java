package mz;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mz.xls.objects.PrimaHelper;
import mz.xls.read.ChFactHelper;
import mz.xls.read.ReadRelationContractor;
import mz.xls.read.ReadRelationObject;
import mz.xls.read.XLSReaderSource;
import mz.xls.write.WritePFact;
import mz.xls.write.WriteRelationContractor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ProjectRelationsController implements Initializable{
    @FXML
    public ListView listFromPrima;
    @FXML
    public ListView excelProject;
    @FXML
    public ListView excelPeredel;
    @FXML
    public ListView primaContractor;
    @FXML
    public ListView xlsContractor;
    @FXML
    public TabPane tabPane;
    @FXML
    public Tab tabObjects;
    @FXML
    public Tab tabContractors;

    private XLSReaderSource xlsReaderSource;
    private File file;

    private PrimaHelper primaHelper;

    private HashMap<String, ArrayList<String>> projectRelationsMap = new HashMap<>();
    private HashMap<String, String> contractorRelationsMap = new HashMap<>();

    private HashMap<String, ArrayList<String>> projectRelationsListMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/sample.fxml"));
            loader.load();
            Controller fileChoose = loader.getController();

            root = loader.getRoot();
            Stage stage = new Stage();
            stage.setTitle("Выберите файлы с данными");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            if (fileChoose.getXlsReaderSource()!= null) {
                fillData(fileChoose.getXlsReaderSource(), fileChoose.getFile());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        primaContractor.setCellFactory(param -> new ContractorFormatCell());
        xlsContractor.setCellFactory(param -> new ContractorFormatCell());
        listFromPrima.setCellFactory(param -> new ContractorFormatCell());
    }

    private void fillData(XLSReaderSource xlsReaderSource, File file) {
        //Заполнение листвью проектов
        this.xlsReaderSource = xlsReaderSource;
        this.file = file;
        primaHelper = xlsReaderSource.getXlsReaderPrimavera().getPrimaHelper();

        ReadRelationObject rrcObject = new ReadRelationObject(xlsReaderSource.getWorkbook(), "Связи-Объект");
        if (rrcObject.size() > 0) projectRelationsMap = rrcObject;

        ArrayList<MzListItem> primaProjectsName = new ArrayList<>();
        for (String name : primaHelper.getProjectObjectName()) {
            if (projectRelationsMap.containsKey(name)) {
                primaProjectsName.add(new MzListItem(name, true));
            } else {
                primaProjectsName.add(new MzListItem(name));
            }
        }
        primaProjectsName.sort(Comparator.comparing(MzListItem :: getName));
        listFromPrima.getItems().addAll(primaProjectsName);
        listFromPrima.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        ChFactHelper chFactHelper = xlsReaderSource.getChFacts();
        ArrayList<MzListItem> excelProjectNames = new ArrayList<>();
        for (String projectName : chFactHelper.getProjectNameSet()) {
            boolean flag = false;
            for (ArrayList<String> list : projectRelationsMap.values()) {
                for (String s : list) {
                    if (projectName.equals(s)) flag = true;
                }
            }
            if (flag) {
                excelProjectNames.add(new MzListItem(projectName, true));
            } else {
                excelProjectNames.add(new MzListItem(projectName));
            }
        }

        excelProject.getItems().addAll(excelProjectNames);

        //Заполнение листвью подрядчиков
        ReadRelationContractor rrc = new ReadRelationContractor(xlsReaderSource.getWorkbook(), "Связи-Подрядчики");
        if (rrc.size() > 0) contractorRelationsMap = rrc;

        ArrayList<MzListItem> primaContractorNames = new ArrayList<>();
        for (String contractor : xlsReaderSource.getXlsReaderPrimavera().getPrimaHelper().getPrimaContractorName()) {
            if (contractorRelationsMap.containsKey(contractor)) {
                primaContractorNames.add(new MzListItem(contractor, true));
            } else {
                primaContractorNames.add(new MzListItem(contractor));
            }
        }
        primaContractorNames.sort(Comparator.comparing(MzListItem::getName));
        primaContractor.getItems().setAll(primaContractorNames);

        ArrayList<MzListItem> xlsContractorNames = new ArrayList<>();
        for (String contractor : xlsReaderSource.getContractorHelper().getContractors()) {
            if (contractorRelationsMap.containsValue(contractor)) {
                xlsContractorNames.add(new MzListItem(contractor, true));
            } else {
                xlsContractorNames.add(new MzListItem(contractor));

            }
        }
        xlsContractorNames.sort(Comparator.comparing(MzListItem::getName));
        xlsContractor.getItems().setAll(xlsContractorNames);

    }

    public void doMagic(ActionEvent actionEvent) {
        if (tabObjects.isSelected()) {

            String xlsProjectNameSelect = (String) excelProject.getSelectionModel().getSelectedItem();
            String xlsPeredelNameSelect = (String) excelPeredel.getSelectionModel().getSelectedItem();

            if (listFromPrima.getSelectionModel().getSelectedItems().size() > 0) {
                for (Object primaProjectName : listFromPrima.getSelectionModel().getSelectedItems()) {
                    MzListItem ppn = (MzListItem) primaProjectName;
                    if (projectRelationsMap.containsKey(ppn.getName())) {
                        projectRelationsMap.get(ppn.getName()).add(xlsProjectNameSelect + xlsPeredelNameSelect);
                    } else {
                        ArrayList<String> tempList = new ArrayList<>();
                        tempList.add(xlsProjectNameSelect + xlsPeredelNameSelect);
                        projectRelationsMap.put(ppn.getName(), tempList);

                    }
                    ppn.setUseProperty(true);
                    listFromPrima.setCellFactory(param -> new ContractorFormatCell());
                }
            }
        } else if (tabContractors.isSelected()) {

            MzListItem primaContractorSelect = (MzListItem) primaContractor.getSelectionModel().getSelectedItem();
            MzListItem xlsContractorSelect = (MzListItem) xlsContractor.getSelectionModel().getSelectedItem();

            if (primaContractorSelect != null && xlsContractorSelect != null) {
                contractorRelationsMap.put(primaContractorSelect.getName(), xlsContractorSelect.getName());
                primaContractorSelect.setUseProperty(true);
                xlsContractorSelect.setUseProperty(true);

                primaContractor.setCellFactory(param -> new ContractorFormatCell());
                xlsContractor.setCellFactory(param -> new ContractorFormatCell());
            }
        }
    }

    public void choosePrimaProject(MouseEvent mouseEvent) {

    }

    private void writeXLS(File file, XLSReaderSource xlsReaderSource) {
        WritePFact writePFact = new WritePFact(file, xlsReaderSource.getWorkbook());
        try {
//            writePFact.writeChFactPeriod(xlsReaderSource.getChFacts());
            writePFact.writeAFTVR(xlsReaderSource.getChFacts(), projectRelationsListMap, contractorRelationsMap,
                    xlsReaderSource.getContractorHelper(), primaHelper);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillProjectRelationsArray() {
        for (Map.Entry<String, ArrayList<String>> entry : projectRelationsMap.entrySet()) {
            for (String val : entry.getValue()) {
                if (projectRelationsListMap.containsKey(val)) {
                    projectRelationsListMap.get(val).add(entry.getKey());
                } else {
                    ArrayList<String> tempList = new ArrayList<>();
                    tempList.add(entry.getKey());
                    projectRelationsListMap.put(val, tempList);
                }
            }
        }
    }

    public void projectClick(MouseEvent mouseEvent) {
        String selectedProject = (String) excelProject.getSelectionModel().getSelectedItem();

        ChFactHelper chFactHelper = xlsReaderSource.getChFacts();
        ArrayList<String> xlsPeredelNames = chFactHelper.getPeredelNameSet(selectedProject);
        excelPeredel.getItems().removeAll();
        excelPeredel.getItems().setAll(xlsPeredelNames);
    }

    public void done(ActionEvent actionEvent) {
        fillProjectRelationsArray();
        writeXLS(file, xlsReaderSource);
        try {
            xlsReaderSource.getWorkbook().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(ActionEvent actionEvent) {
        WriteRelationContractor writer = new WriteRelationContractor(file, xlsReaderSource.getWorkbook());
        try {
            writer.write(contractorRelationsMap, "Связи-Подрядчики");
            writer.writeObject(projectRelationsMap, "Связи-Объект");

            System.out.println("Сохранение завершено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ContractorFormatCell extends ListCell<MzListItem> {
        static final String USE_CLASS = "listCellUse";
        @Override
        protected void updateItem(MzListItem item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null && item.getName() != null) {
                setText(item.getName());
                if (item.isUse()) {
                    getStyleClass().add(USE_CLASS);
                } else {
                    getStyleClass().remove(USE_CLASS);
                }
            } else {
                setText(null);
                getStyleClass().remove(USE_CLASS);
            }
        }
    }

    class MzListItem {
        private StringProperty nameProperty;
        private BooleanProperty useProperty;

        MzListItem(String nameProperty) {
            this.nameProperty = new SimpleStringProperty(nameProperty);
            this.useProperty = new SimpleBooleanProperty(false);
        }

        MzListItem(String nameProperty, boolean use) {
            this.nameProperty = new SimpleStringProperty(nameProperty);
            this.useProperty = new SimpleBooleanProperty(use);
        }

        public void setUseProperty(boolean use) {
            this.useProperty = new SimpleBooleanProperty(use);
        }

        public String getName() {
            return nameProperty.get();
        }

        public StringProperty nameProperty() {
            return nameProperty;
        }

        public boolean isUse() {
            return useProperty.get();
        }

        public BooleanProperty useProperty() {
            return useProperty;
        }
    }


}
