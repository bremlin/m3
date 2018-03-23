package mz.xls.objects;

import mz.xls.read.FinPeriodHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PrimaHelper extends HashMap<String, HashMap<String, ArrayList<PrimaFO>>> {    //Project, FinPeriod, PrimaFO

    private FinPeriodHelper finPeriodHelper;

    private HashMap<String, PrimaFO> primaFoMap = new HashMap<>();

    private HashSet<String> projectObjectName = new HashSet<>();
    private HashSet<String> resourceName = new HashSet<>();
    private HashSet<String> primaContractorName = new HashSet<>();

    public PrimaHelper(Workbook workbook) {

        Sheet sheet = workbook.getSheet("ПТВР");

        boolean flag = false;

        int i = 0;
        for (Row row : sheet) {
            if (flag) {
                PrimaFO primaFO = new PrimaFO(row, sheet.getSheetName(), getFinPeriodHelper(), projectObjectName,
                        resourceName, primaContractorName, i);
                String key = primaFO.getProject();
                primaFoMap.put(primaFO.getProject() + "|" + primaFO.getActivityCode(), primaFO);

                if (this.containsKey(key)) {
                    if (this.get(key).containsKey(primaFO.getContractor())) {
                        this.get(key).get(primaFO.getContractor()).add(primaFO);
                    } else {
                        ArrayList<PrimaFO> tempList = new ArrayList<>();
                        tempList.add(primaFO);

                        this.get(key).put(primaFO.getContractor(), tempList);
                    }
                } else {
                    HashMap<String, ArrayList<PrimaFO>> tempFinPeriodMap = new HashMap<>();
                    ArrayList<PrimaFO> tempList = new ArrayList<>();
                    tempList.add(primaFO);

                    tempFinPeriodMap.put(primaFO.getContractor(), tempList);
                    this.put(primaFO.getProject(), tempFinPeriodMap);
                }
            } else {
                finPeriodHelper = new FinPeriodHelper(row);
                flag = true;
            }
        }

        readFO(workbook);

    }

    private void readFO(Workbook workbook) {
        Sheet sheetFO = workbook.getSheet("ФО");
        boolean flag = false;

        int i = 0;
        for (Row row : sheetFO) {
            if (flag) {
                PrimaFO primaFO = new PrimaFO(row, sheetFO.getSheetName(), getFinPeriodHelper(), projectObjectName,
                        resourceName, primaContractorName, i);
                String key = primaFO.getProject();

                if (primaFoMap.containsKey(primaFO.getProject() + "|" + primaFO.getActivityCode())) {
                    primaFoMap.get(primaFO.getProject() + "|" + primaFO.getActivityCode()).setFOMap(primaFO.getFOMap());
                } else {
                    if (this.containsKey(key)) {
                        if (this.get(key).containsKey(primaFO.getContractor())) {
                            this.get(key).get(primaFO.getContractor()).add(primaFO);
                        } else {
                            ArrayList<PrimaFO> tempList = new ArrayList<>();
                            tempList.add(primaFO);

                            this.get(key).put(primaFO.getContractor(), tempList);
                        }
                    } else {
                        HashMap<String, ArrayList<PrimaFO>> tempFinPeriodMap = new HashMap<>();
                        ArrayList<PrimaFO> tempList = new ArrayList<>();
                        tempList.add(primaFO);

                        tempFinPeriodMap.put(primaFO.getContractor(), tempList);
                        this.put(primaFO.getProject(), tempFinPeriodMap);
                    }
                }
            } else {
                flag = true;
            }
        }
    }

    public FinPeriodHelper getFinPeriodHelper() {
        return finPeriodHelper;
    }

    public ArrayList<String> getProjectObjectName() {
        return new ArrayList<>(projectObjectName);
    }

    public HashSet<String> getResourceName() {
        return resourceName;
    }

    public ArrayList<String> getPrimaContractorName() {
        return new ArrayList<>(primaContractorName);
    }
}
