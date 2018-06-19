package mz.xls.read;

import mz.xls.objects.ChFact;
import mz.xls.objects.FinPeriod;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ChFactHelper extends ArrayList<ChFact> {

    private HashMap<FinPeriod, HashMap<String, Integer>> hashPeriod = new HashMap<>();
    private HashMap<String, HashMap<String, Integer>> hashMonthPeriod = new HashMap<>();
    private HashMap<FinPeriod, ArrayList<ChFact>> chFactHash = new HashMap<>();
    private HashMap<String, ArrayList<ChFact>> chFactMonthHash = new HashMap<>();

    private HashSet<String> projectNameSet = new HashSet<>();
    private HashMap<String, HashSet<String>> peredelNameHashMap = new HashMap<>();

    public ChFactHelper(Sheet sheet, ContractorHelper contractorHelper) {
        boolean flag = false;

        for (Row row : sheet) {
            if (flag && row.getCell(0).getDateCellValue() != null) {
                ChFact chFact = new ChFact(row, contractorHelper);
                if (chFact.getContractor() == null) {
                    System.out.println(row.getCell(3).getStringCellValue());
                }
                this.add(chFact);
                projectNameSet.add(chFact.getProject());
                if (peredelNameHashMap.containsKey(chFact.getProject())) {
                    peredelNameHashMap.get(chFact.getProject()).add(chFact.getPeredel());
                } else {
                    HashSet<String> tempSet = new HashSet<>();
                    tempSet.add(chFact.getPeredel());
                    peredelNameHashMap.put(chFact.getProject(), tempSet);
                }
            } else {
                String cellDateName = row.getCell(0).getStringCellValue();
                if (cellDateName.equals("Дата")) flag = true;
            }
        }
    }

    public void addFinPeriod(FinPeriodHelper finPeriodHelper) throws ParseException {
        ArrayList<Long> finPeriods = finPeriodHelper.getStartTimeList();
        Long periodHashTime = 0L;

        FinPeriod periodHash = null;

        for (ChFact chFact : this) {
            Long date = chFact.getDate().getTime();
            if (finPeriodHelper.containsKey(date)) {
                chFact.setPeriod(finPeriodHelper.get(date));
            } else if (date.equals(periodHashTime)) {
                chFact.setPeriod(periodHash);
            } else {
                for (int i = 0; i < finPeriods.size(); i++) {
                    if (i < finPeriods.size() - 1 &&
                            chFact.getDate().getTime() >= finPeriods.get(i) &&
                            chFact.getDate().getTime() < finPeriods.get(i + 1)) {
                        periodHash = finPeriodHelper.get(finPeriods.get(i));
                        periodHashTime = finPeriods.get(i);
                        chFact.setPeriod(periodHash);
                    }
                }
            }
            if (chFact.getContractor() == null) {
                System.out.println("contractor is null");
            }
            String chFactKeyFull = chFact.getKey() + chFact.getContractor().getCode();
            if (hashPeriod.containsKey(chFact.getPeriod())) {
                if (hashPeriod.get(chFact.getPeriod()).containsKey(chFactKeyFull)) {
                    Integer fact = hashPeriod.get(chFact.getPeriod()).get(chFactKeyFull) + chFact.getFact();
                    hashPeriod.get(chFact.getPeriod()).put(chFactKeyFull, fact);
                } else {
                    chFactHash.get(chFact.getPeriod()).add(chFact);
                    hashPeriod.get(chFact.getPeriod()).put(chFactKeyFull, chFact.getFact());
                }
            } else {
                HashMap<String, Integer> hashFacts = new HashMap<>();
                hashFacts.put(chFactKeyFull, chFact.getFact());
                hashPeriod.put(chFact.getPeriod(), hashFacts);

                ArrayList<ChFact> factArrayList = new ArrayList<>();
                factArrayList.add(chFact);
                chFactHash.put(chFact.getPeriod(), factArrayList);
            }

            if (hashMonthPeriod.containsKey(chFact.getPeriod().getMonthPeriod())) {
                if (hashMonthPeriod.get(chFact.getPeriod().getMonthPeriod()).containsKey(chFactKeyFull)) {
                    Integer fact = hashMonthPeriod.get(chFact.getPeriod().getMonthPeriod()).get(chFactKeyFull) + chFact.getFact();
                    hashMonthPeriod.get(chFact.getPeriod().getMonthPeriod()).put(chFactKeyFull, fact);
                } else {
                    chFactMonthHash.get(chFact.getPeriod().getMonthPeriod()).add(chFact);
                    hashMonthPeriod.get(chFact.getPeriod().getMonthPeriod()).put(chFactKeyFull, chFact.getFact());
                }
            } else {
                HashMap<String, Integer> hashFacts = new HashMap<>();
                hashFacts.put(chFactKeyFull, chFact.getFact());
                hashMonthPeriod.put(chFact.getPeriod().getMonthPeriod(), hashFacts);

                ArrayList<ChFact> factArrayList = new ArrayList<>();
                factArrayList.add(chFact);
                chFactMonthHash.put(chFact.getPeriod().getMonthPeriod(), factArrayList);
            }
        }
    }

    public HashMap<FinPeriod, HashMap<String, Integer>> getHashPeriod() {
        return hashPeriod;
    }

    public HashMap<FinPeriod, ArrayList<ChFact>> getChFactHash() {
        return chFactHash;
    }

    public HashMap<String, HashMap<String, Integer>> getHashMonthPeriod() {
        return hashMonthPeriod;
    }

    public HashMap<String, ArrayList<ChFact>> getChFactMonthHash() {
        return chFactMonthHash;
    }

    public ArrayList<String> getProjectNameSet() {
        return new ArrayList<>(projectNameSet);
    }

    public ArrayList<String> getPeredelNameSet(String projectName) {
        return new ArrayList<>(peredelNameHashMap.get(projectName));
    }
}
