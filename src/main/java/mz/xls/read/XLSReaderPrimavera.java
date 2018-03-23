package mz.xls.read;

import mz.xls.objects.PrimaHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSReaderPrimavera {

    private PrimaHelper primaHelper;

    public XLSReaderPrimavera(Workbook workbook) {

        primaHelper = new PrimaHelper(workbook);
    }

    public FinPeriodHelper getFinPeriodHelper() {
        return primaHelper.getFinPeriodHelper();
    }

    public PrimaHelper getPrimaHelper() {
        return primaHelper;
    }
}