package mz.xls.write;

import mz.xls.objects.ChFact;
import mz.xls.objects.FinPeriod;
import mz.xls.objects.PrimaFO;
import mz.xls.objects.PrimaHelper;
import mz.xls.read.ChFactHelper;
import mz.xls.read.ContractorHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class WritePFact {

    private File file;
    private XSSFWorkbook workbook;

    private XSSFSheet sheet;

    private int rowCount;
    private int cellCount;
    private XSSFRow row;

    public WritePFact(File file, XSSFWorkbook workbook) {
        this.file = file;
        this.workbook = workbook;
    }

    public void writeChFactPeriod(ChFactHelper chFacts) throws IOException {
        XSSFSheet sheet = workbook.createSheet("ПФАКТ");

        int rowCount = 0;
        XSSFRow rowName = sheet.createRow(rowCount++);
        int cellNameCount = 0;
        Cell cellFinText = rowName.createCell(cellNameCount++);
        cellFinText.setCellValue("ФинПериод");

        Cell cellProjectText = rowName.createCell(cellNameCount++);
        cellProjectText.setCellValue("Проект");

        Cell cellObjectText = rowName.createCell(cellNameCount++);
        cellObjectText.setCellValue("Обьект");

        Cell cellContractorText = rowName.createCell(cellNameCount++);
        cellContractorText.setCellValue("Подрядчик");

        Cell cellFactText = rowName.createCell(cellNameCount);
        cellFactText.setCellValue("Сумм. к-во раб.");

        HashMap<FinPeriod, HashMap<String, Integer>> hashFact = chFacts.getHashPeriod();

        ArrayList<FinPeriod> finList = new ArrayList<>(chFacts.getChFactHash().keySet());
        finList.sort(Comparator.comparing(FinPeriod::getStartDate));

        for (FinPeriod finPeriod : finList) {
            ArrayList<ChFact> periodChFacts = chFacts.getChFactHash().get(finPeriod);
            for (ChFact chFact : periodChFacts) {
                XSSFRow row = sheet.createRow(rowCount++);
                int cellCount = 0;

//                XSSFCell cellDate = row.createCell(cellCount++);
//                cellDate.setCellValue(chFact.getDate());
//                XSSFCreationHelper createHelper = workbook.getCreationHelper();
//                XSSFCellStyle cellStyle         = workbook.createCellStyle();
//                cellStyle.setDataFormat(
//                        createHelper.createDataFormat().getFormat("dd.mm.yyyy"));
//                cellDate.setCellStyle(cellStyle);

                Cell cellPeriod = row.createCell(cellCount++);
                cellPeriod.setCellValue(chFact.getPeriod().getName());

                Cell cellProject = row.createCell(cellCount++);
                cellProject.setCellValue(chFact.getProject());

                Cell cellPeredel = row.createCell(cellCount++);
                cellPeredel.setCellValue(chFact.getPeredel());

                Cell cellContractor = row.createCell(cellCount++);
                cellContractor.setCellValue(chFact.getContractor() != null ? chFact.getContractor().getName() : "пусто");

                Cell cellFact = row.createCell(cellCount++);
                cellFact.setCellValue(hashFact.get(finPeriod).get(chFact.getKey()));
            }
            System.out.println(finPeriod.getName());
        }

        workbook.write(new FileOutputStream(file));
        System.out.println("writeDone");
    }


    public void writeAFTVR(ChFactHelper chFacts,
                           HashMap<String, ArrayList<String>> projectRelationsMap,
                           HashMap<String, String> contractorRelationsMap,
                           ContractorHelper contractorHelper,
                           PrimaHelper primaHelper) throws IOException {
        sheet = workbook.createSheet("БАЗОВАЯ");

        HashMap<String, String> contractorMap = new HashMap<>();
        for (Map.Entry entry : contractorRelationsMap.entrySet()) {
            contractorMap.put(entry.getValue().toString(), entry.getKey().toString());
        }

        rowCount = 0;
        XSSFRow rowName = sheet.createRow(rowCount++);
        int cellNameCount = 0;
        Cell cellFinText = rowName.createCell(cellNameCount++);
        cellFinText.setCellValue("ФинПериод");

        Cell cellProjectText = rowName.createCell(cellNameCount++);
        cellProjectText.setCellValue("Проект");

        Cell cellObjectText = rowName.createCell(cellNameCount++);
        cellObjectText.setCellValue("Обьект");

        Cell cellContractorText = rowName.createCell(cellNameCount++);
        cellContractorText.setCellValue("Подрядчик");

        Cell cellPvText = rowName.createCell(cellNameCount++);
        cellPvText.setCellValue("PV");

        Cell cellFactText = rowName.createCell(cellNameCount++);
        cellFactText.setCellValue("ФТВР");

        Cell cellPlanText = rowName.createCell(cellNameCount++);
        cellPlanText.setCellValue("ПТВР");

        Cell cellFOText = rowName.createCell(cellNameCount);
        cellFOText.setCellValue("ФО");

        HashMap<FinPeriod, HashMap<String, Integer>> hashFact = chFacts.getHashPeriod();

        ArrayList<FinPeriod> finList = new ArrayList<>(chFacts.getChFactHash().keySet());
        finList.sort(Comparator.comparing(FinPeriod::getStartDate));

        for (FinPeriod finPeriod : finList) {
            ArrayList<ChFact> periodChFacts = chFacts.getChFactHash().get(finPeriod);
            for (ChFact chFact : periodChFacts) {

                //Добавление базовой инфы

                HashMap<String, Double> FOMap = new HashMap<>();
                HashMap<String, Double> PTVRMap = new HashMap<>();
                HashSet<String> pvSet = new HashSet<>();
                Double sumPTVR = 0.0;

                if (projectRelationsMap.containsKey(chFact.getKey())) {
                    for (String projectKey : projectRelationsMap.get(chFact.getKey())) {
                        if (primaHelper.get(projectKey).get(contractorMap.get(chFact.getContractor().getCode())) != null) {

                            for (PrimaFO primaFO : primaHelper.get(projectKey).get(contractorMap.get(chFact.getContractor().getCode()))) {
                                if (primaFO.getFOMap().size() > 0) {
                                    if (primaFO.getFOMap().get(chFact.getPeriod()) != null) {
                                        pvSet.add(primaFO.getResourceType());
                                        if (FOMap.containsKey(primaFO.getResourceType())) {
                                            Double ftvr = FOMap.get(primaFO.getResourceType()) + primaFO.getFOMap().get(chFact.getPeriod());
                                            FOMap.put(primaFO.getResourceType(), ftvr);
                                        } else {
                                            FOMap.put(primaFO.getResourceType(), primaFO.getFOMap().get(chFact.getPeriod()));
                                        }
                                    }
                                }

                                if (primaFO.getTZMap().size() > 0) {
                                    if (primaFO.getTZMap().get(chFact.getPeriod()) != null) {
                                        pvSet.add(primaFO.getResourceType());
                                        sumPTVR += primaFO.getTZMap().get(chFact.getPeriod());
                                        if (PTVRMap.containsKey(primaFO.getResourceType())) {
                                            Double ptvr = PTVRMap.get(primaFO.getResourceType()) + primaFO.getTZMap().get(chFact.getPeriod());
                                            PTVRMap.put(primaFO.getResourceType(), ptvr);
                                        } else {
                                            PTVRMap.put(primaFO.getResourceType(), primaFO.getTZMap().get(chFact.getPeriod()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //Если есть данные из примы, то начинаем цикл с PV, если нет, то выдаем базовые значения
                    if (pvSet.size() > 0) {
                        boolean first = false;
                        ArrayList<String> sortList = new ArrayList<>(pvSet);
                        sortList.sort(Comparator.comparing(String::toLowerCase));
                        for (String pv : sortList) {
                            Double ftvr = hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode()) * (PTVRMap.getOrDefault(pv, 0.0) / sumPTVR) * chFact.getContractor().getWormWH();
                            if (!first) {
                                createBaseRow(chFact);
                                createPVRow(pv, ftvr, PTVRMap.getOrDefault(pv, 0.0), FOMap.getOrDefault(pv, 0.0));
                                first = true;
                            } else {
                                createBaseRow(chFact);
                                createPVRow(pv, ftvr, PTVRMap.getOrDefault(pv, 0.0), FOMap.getOrDefault(pv, 0.0));
                            }

                        }
                    } else {
                        createBaseRow(chFact);
                        createEmptyRow(hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode()));
                    }
                } else {
                    createBaseRow(chFact);
                    createEmptyRow(hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode()));
                }

            }
            System.out.println(finPeriod.getName());
        }

        for (int i = 0; i < 8; i++) {
            if (i == 0) {
                sheet.setColumnWidth(i, 256 * 25);
            } else if (i == 1) {
                sheet.setColumnWidth(i, 256 * 20);
            } else if (i < 4) {
                sheet.setColumnWidth(i, 256 * 55);
            } else if (i == 4) {
                sheet.setColumnWidth(i, 256 * 8);
            } else {
                sheet.setColumnWidth(i, 256 * 15);
            }
        }

        workbook.write(new FileOutputStream(file));
        System.out.println("writeDone");
    }

    private void createBaseRow(ChFact chFact) {
        row = sheet.createRow(rowCount++);
        cellCount = 0;

        Cell cellPeriod = row.createCell(cellCount++);
        cellPeriod.setCellValue(chFact.getPeriod().getName());

        Cell cellProject = row.createCell(cellCount++);
        cellProject.setCellValue(chFact.getProject());

        Cell cellPeredel = row.createCell(cellCount++);
        cellPeredel.setCellValue(chFact.getPeredel());

        Cell cellContractor = row.createCell(cellCount++);
        cellContractor.setCellValue(chFact.getContractor() != null ? chFact.getContractor().getName() : "пусто");
    }

    private void createPVRow(String pv, Double ftvr, Double ptvr, Double fo) {
        Cell cellPV = row.createCell(cellCount++);
        cellPV.setCellValue(pv);

        Cell cellFTVR = row.createCell(cellCount++);
        cellFTVR.setCellValue(ftvr);

        Cell cellPTVR = row.createCell(cellCount++);
        cellPTVR.setCellValue(ptvr);

        Cell cellFO = row.createCell(cellCount);
        cellFO.setCellValue(fo);
    }

    private void createEmptyRow(Integer fact) {
        Cell cellPV = row.createCell(cellCount++);
        cellPV.setCellValue("---");

        Cell cellFTVR = row.createCell(cellCount++);
        cellFTVR.setCellValue(fact);
    }

}
