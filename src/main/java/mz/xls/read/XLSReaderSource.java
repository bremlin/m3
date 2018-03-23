package mz.xls.read;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

public class XLSReaderSource {

    private XSSFWorkbook workbook;

    private ChFactHelper chFacts;

    private XLSReaderPrimavera xlsReaderPrimavera;
    private FinPeriodHelper finPeriodHelper;
    private ContractorHelper contractorHelper;

    public XLSReaderSource(File file, File prima) {

        try {
            FileInputStream excelFile = new FileInputStream(file);
            this.workbook = new XSSFWorkbook(excelFile);

            contractorHelper = new ContractorHelper(workbook.getSheet("Подрядчики"));
            this.chFacts = new ChFactHelper(workbook.getSheet("Данные"), contractorHelper);

            FileInputStream primaFile = new FileInputStream(prima);
            Workbook workbookPrima = new XSSFWorkbook(primaFile);

            xlsReaderPrimavera = new XLSReaderPrimavera(workbookPrima);

            finPeriodHelper = xlsReaderPrimavera.getFinPeriodHelper();

            chFacts.addFinPeriod(finPeriodHelper);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public ChFactHelper getChFacts() {
        return chFacts;
    }

    public XLSReaderPrimavera getXlsReaderPrimavera() {
        return xlsReaderPrimavera;
    }

    public FinPeriodHelper getFinPeriodHelper() {
        return finPeriodHelper;
    }

    public ContractorHelper getContractorHelper() {
        return contractorHelper;
    }
}