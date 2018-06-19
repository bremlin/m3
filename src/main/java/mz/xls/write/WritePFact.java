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

    public void writeAFTVRMonth(ChFactHelper chFacts,
                           HashMap<String, ArrayList<String>> projectRelationsMap,
                           HashMap<String, String> contractorRelationsMap,
                           ContractorHelper contractorHelper,
                           PrimaHelper primaHelper) throws IOException {
        sheet = workbook.createSheet("БАЗОВАЯ_МЕСЯЦ");

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

        Cell cellFOText = rowName.createCell(cellNameCount++);
        cellFOText.setCellValue("ФО");

        Cell cellPTORText = rowName.createCell(cellNameCount++);
        cellPTORText.setCellValue("ПТОР");

        Cell cellFOOText = rowName.createCell(cellNameCount);
        cellFOOText.setCellValue("ФОО");

        HashMap<String, HashMap<String, Integer>> hashFact = chFacts.getHashMonthPeriod();

        ArrayList<String> finList = new ArrayList<>(chFacts.getChFactMonthHash().keySet());
        finList.sort(Comparator.comparing(String::toString));

        for (String finPeriod : finList) {
            ArrayList<ChFact> periodChFacts = chFacts.getChFactMonthHash().get(finPeriod);
            for (ChFact chFact : periodChFacts) {

                //Добавление базовой инфы

                HashMap<String, Double> FOMap = new HashMap<>();
                HashMap<String, Double> PTVRMap = new HashMap<>();
//                HashMap<String, Double> FOOMap = new HashMap<>();
//                HashMap<String, Double> PTORMap = new HashMap<>();
                HashSet<String> pvSet = new HashSet<>();
                Double sumPTVR = 0.0;

                if (projectRelationsMap.containsKey(chFact.getKey())) {
                    for (String projectKey : projectRelationsMap.get(chFact.getKey())) {
                        System.out.println("projectKey: " + projectKey + "   |   chFactContractor: " + chFact.getContractor().getCode());
                        if (primaHelper.get(projectKey).get(contractorMap.get(chFact.getContractor().getCode())) != null) {

                            for (PrimaFO primaFO : primaHelper.get(projectKey).get(contractorMap.get(chFact.getContractor().getCode()))) {
                                if (primaFO.getFOMapMonth().size() > 0) {
                                    if (primaFO.getFOMapMonth().get(chFact.getPeriod().getMonthPeriod()) != null) {
                                        pvSet.add(primaFO.getResourceType());
                                        if (FOMap.containsKey(primaFO.getResourceType())) {
                                            Double ftvr = FOMap.get(primaFO.getResourceType()) + primaFO.getFOMapMonth().get(chFact.getPeriod().getMonthPeriod());
                                            FOMap.put(primaFO.getResourceType(), ftvr);
                                        } else {
                                            FOMap.put(primaFO.getResourceType(), primaFO.getFOMapMonth().get(chFact.getPeriod().getMonthPeriod()));
                                        }
                                    }
                                }
                                //ПТВР
                                if (primaFO.getTZMapMonth().size() > 0) {
                                    if (primaFO.getTZMapMonth().get(chFact.getPeriod().getMonthPeriod()) != null) {
                                        pvSet.add(primaFO.getResourceType());
                                        sumPTVR += primaFO.getTZMapMonth().get(chFact.getPeriod().getMonthPeriod());
                                        if (PTVRMap.containsKey(primaFO.getResourceType())) {
                                            Double ptvr = PTVRMap.get(primaFO.getResourceType()) + primaFO.getTZMapMonth().get(chFact.getPeriod().getMonthPeriod());
                                            PTVRMap.put(primaFO.getResourceType(), ptvr);
                                        } else {
                                            PTVRMap.put(primaFO.getResourceType(), primaFO.getTZMapMonth().get(chFact.getPeriod().getMonthPeriod()));
                                        }
                                    }
                                }
//                                //ПТОР
//                                if (primaFO.getTZOMapMonth().size() > 0) {
//                                    if (primaFO.getTZOMapMonth().get(chFact.getPeriod().getMonthPeriod()) != null) {
//                                        pvSet.add(primaFO.getResourceType());
//                                        if (PTORMap.containsKey(primaFO.getResourceType())) {
//                                            Double ptor = PTORMap.get(primaFO.getResourceType()) + primaFO.getTZOMapMonth().get(chFact.getPeriod().getMonthPeriod());
//                                            PTORMap.put(primaFO.getResourceType(), ptor);
//                                        } else {
//                                            PTORMap.put(primaFO.getResourceType(), primaFO.getTZOMapMonth().get(chFact.getPeriod().getMonthPeriod()));
//                                        }
//                                    }
//                                }
//                                //ФОО
//                                if (primaFO.getFOOMapMonth().size() > 0) {
//                                    if (primaFO.getFOOMapMonth().get(chFact.getPeriod().getMonthPeriod()) != null) {
//                                        pvSet.add(primaFO.getResourceType());
//                                        if (FOOMap.containsKey(primaFO.getResourceType())) {
//                                            Double foo = FOOMap.get(primaFO.getResourceType()) + primaFO.getFOOMapMonth().get(chFact.getPeriod().getMonthPeriod());
//                                            FOOMap.put(primaFO.getResourceType(), foo);
//                                        } else {
//                                            FOOMap.put(primaFO.getResourceType(), primaFO.getFOOMapMonth().get(chFact.getPeriod().getMonthPeriod()));
//                                        }
//                                    }
//                                }
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
                                createBaseRowMonth(chFact);
                                //todo поставить вариант выгрузки
                                createPVRow(pv, ftvr, PTVRMap.getOrDefault(pv, 0.0), FOMap.getOrDefault(pv, 0.0));
//                                createPVExtRow(pv, ftvr,
//                                        PTVRMap.getOrDefault(pv, 0.0),
//                                        FOMap.getOrDefault(pv, 0.0),
//                                        PTORMap.getOrDefault(pv, 0.0),
//                                        FOOMap.getOrDefault(pv, 0.0));
                                first = true;
                            } else {
                                createBaseRowMonth(chFact);
                                createPVRow(pv, ftvr, PTVRMap.getOrDefault(pv, 0.0), FOMap.getOrDefault(pv, 0.0));
//                                createPVExtRow(pv, ftvr,
//                                        PTVRMap.getOrDefault(pv, 0.0),
//                                        FOMap.getOrDefault(pv, 0.0),
//                                        PTORMap.getOrDefault(pv, 0.0),
//                                        FOOMap.getOrDefault(pv, 0.0));
                            }

                        }
                    } else {
                        createBaseRowMonth(chFact);
                        createEmptyRow((hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode())) * chFact.getContractor().getWormWH());
                    }
                } else {
                    createBaseRowMonth(chFact);
                    createEmptyRow((hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode())) * chFact.getContractor().getWormWH());
                }

            }
            System.out.println(finPeriod);
        }

        setWidth(sheet);


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

        Cell cellFOText = rowName.createCell(cellNameCount++);
        cellFOText.setCellValue("ФО");

        Cell cellPTORText = rowName.createCell(cellNameCount++);
        cellPTORText.setCellValue("ПТОР");

        Cell cellFOOText = rowName.createCell(cellNameCount);
        cellFOOText.setCellValue("ФОО");

        HashMap<FinPeriod, HashMap<String, Integer>> hashFact = chFacts.getHashPeriod();

        ArrayList<FinPeriod> finList = new ArrayList<>(chFacts.getChFactHash().keySet());
        finList.sort(Comparator.comparing(FinPeriod::getStartDate));

        for (FinPeriod finPeriod : finList) {
            ArrayList<ChFact> periodChFacts = chFacts.getChFactHash().get(finPeriod);
            for (ChFact chFact : periodChFacts) {

                //Добавление базовой инфы

                HashMap<String, Double> FOMap = new HashMap<>();
                HashMap<String, Double> PTVRMap = new HashMap<>();
//                HashMap<String, Double> FOOMap = new HashMap<>();
//                HashMap<String, Double> PTORMap = new HashMap<>();
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
//                                //ФОО
//                                if (primaFO.getFOOMap().size() > 0) {
//                                    if (primaFO.getFOOMap().get(chFact.getPeriod()) != null) {
//                                        pvSet.add(primaFO.getResourceType());
//                                        if (FOOMap.containsKey(primaFO.getResourceType())) {
//                                            Double foo = FOOMap.get(primaFO.getResourceType()) + primaFO.getFOOMap().get(chFact.getPeriod());
//                                            FOOMap.put(primaFO.getResourceType(), foo);
//                                        } else {
//                                            FOOMap.put(primaFO.getResourceType(), primaFO.getFOOMap().get(chFact.getPeriod()));
//                                        }
//                                    }
//                                }
//                                //ПТОР
//                                if (primaFO.getTZOMap().size() > 0) {
//                                    if (primaFO.getTZOMap().get(chFact.getPeriod()) != null) {
//                                        pvSet.add(primaFO.getResourceType());
//                                        if (PTORMap.containsKey(primaFO.getResourceType())) {
//                                            Double ptor = PTORMap.get(primaFO.getResourceType()) + primaFO.getTZOMap().get(chFact.getPeriod());
//                                            PTORMap.put(primaFO.getResourceType(), ptor);
//                                        } else {
//                                            PTORMap.put(primaFO.getResourceType(), primaFO.getTZOMap().get(chFact.getPeriod()));
//                                        }
//                                    }
//                                }
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
//                                createPVExtRow(pv, ftvr,
//                                        PTVRMap.getOrDefault(pv, 0.0),
//                                        FOMap.getOrDefault(pv, 0.0),
//                                        PTORMap.getOrDefault(pv, 0.0),
//                                        FOOMap.getOrDefault(pv, 0.0));
                                first = true;
                            } else {
                                createBaseRow(chFact);
                                createPVRow(pv, ftvr, PTVRMap.getOrDefault(pv, 0.0), FOMap.getOrDefault(pv, 0.0));
//                                createPVExtRow(pv, ftvr,
//                                        PTVRMap.getOrDefault(pv, 0.0),
//                                        FOMap.getOrDefault(pv, 0.0),
//                                        PTORMap.getOrDefault(pv, 0.0),
//                                        FOOMap.getOrDefault(pv, 0.0));
                            }

                        }
                    } else {
                        createBaseRow(chFact);
                        createEmptyRow((hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode())) * chFact.getContractor().getWormWH());
                    }
                } else {
                    createBaseRow(chFact);
                    createEmptyRow((hashFact.get(finPeriod).get(chFact.getKey() + chFact.getContractor().getCode())) * chFact.getContractor().getWormWH());
                }

            }
            System.out.println(finPeriod.getName());
        }

        setWidth(sheet);

        workbook.write(new FileOutputStream(file));
        System.out.println("writeDone");
    }

    private void setWidth(XSSFSheet sheet) {
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

    private void createBaseRowMonth(ChFact chFact) {
        row = sheet.createRow(rowCount++);
        cellCount = 0;

        Cell cellPeriod = row.createCell(cellCount++);
        cellPeriod.setCellValue(chFact.getPeriod().getMonthPeriod());

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

    private void createPVExtRow(String pv, Double ftvr, Double ptvr, Double fo, Double ptor, Double foo) {
        Cell cellPV = row.createCell(cellCount++);
        cellPV.setCellValue(pv);

        Cell cellFTVR = row.createCell(cellCount++);
        cellFTVR.setCellValue(ftvr);

        Cell cellPTVR = row.createCell(cellCount++);
        cellPTVR.setCellValue(ptvr);

        Cell cellFO = row.createCell(cellCount++);
        cellFO.setCellValue(fo);

        Cell cellPTOR = row.createCell(cellCount++);
        cellPTOR.setCellValue(ptor);

        Cell cellFOO = row.createCell(cellCount);
        cellFOO.setCellValue(foo);
    }

    private void createEmptyRow(double fact) {
        Cell cellPV = row.createCell(cellCount++);
        cellPV.setCellValue("---");

        Cell cellFTVR = row.createCell(cellCount++);
        cellFTVR.setCellValue(fact);
    }

}
