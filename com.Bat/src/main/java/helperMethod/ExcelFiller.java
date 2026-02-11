package helperMethod;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.List;
import java.util.Random;

public class ExcelFiller {

    public enum ValueType {
        STRING, INTEGER, DOUBLE, DROPDOWN
    }

    public static void setCellValue(
            String filePath,
            int sheetIndex,
            int rowIndex,
            int columnIndex,
            ValueType valueType,
            List<String> dropdownOptionsOrSingleValue
    ) {
        try {
            Workbook workbook = WorkbookFactory.create(new File(filePath));
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Random random = new Random();

            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);

            Cell cell = row.getCell(columnIndex);
            if (cell == null) cell = row.createCell(columnIndex);

            boolean hasStatic = dropdownOptionsOrSingleValue != null && dropdownOptionsOrSingleValue.size() == 1;
            String staticVal = hasStatic ? dropdownOptionsOrSingleValue.get(0) : null;

            switch (valueType) {
                case STRING:
                    cell.setCellValue(staticVal != null ? staticVal : "Text-" + random.nextInt(1000));
                    break;

                case INTEGER:
                    cell.setCellValue(staticVal != null ? Integer.parseInt(staticVal) : random.nextInt(101));
                    break;

                case DOUBLE:
                    cell.setCellValue(staticVal != null ? Double.parseDouble(staticVal) : Math.round(random.nextDouble() * 100.0) / 100.0);
                    break;

                case DROPDOWN:
                    if (staticVal != null) {
                        cell.setCellValue(staticVal.trim());
                        System.out.println("Dropdown Value Set: " + staticVal.trim());
                    } else if (dropdownOptionsOrSingleValue != null && !dropdownOptionsOrSingleValue.isEmpty()) {
                        String selected = dropdownOptionsOrSingleValue.get(random.nextInt(dropdownOptionsOrSingleValue.size())).trim();
                        cell.setCellValue(selected);
                        System.out.println("Dropdown Value Randomly Picked: " + selected);
                    } else {
                        System.out.println("Dropdown values missing!");
                    }
                    break;
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            workbook.close();
            System.out.println("Cell value set successfully at row " + (rowIndex + 1) + ", column " + (columnIndex + 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyCellValue(String filePath, int sheetIndex, int row, int col, String expectedValue) {
        try {
            Workbook wb = WorkbookFactory.create(new File(filePath));
            Sheet sheet = wb.getSheetAt(sheetIndex);
            Row targetRow = sheet.getRow(row);

            if (targetRow == null) {
                System.out.println("Row is null at index: " + row);
                return false;
            }

            Cell cell = targetRow.getCell(col);
            if (cell == null) {
                System.out.println("Cell is null at row " + row + ", col " + col);
                return false;
            }

            String actualValue;

            switch (cell.getCellType()) {
                case STRING:
                    actualValue = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    actualValue = String.valueOf(Math.round(cell.getNumericCellValue() * 100.0) / 100.0);
                    break;
                case BOOLEAN:
                    actualValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    actualValue = cell.getCellFormula();
                    break;
                case BLANK:
                    actualValue = "";
                    break;
                default:
                    actualValue = "";
            }

            expectedValue = expectedValue.trim();
            actualValue = actualValue.trim();

            System.out.println("Expected: '" + expectedValue + "', Actual: '" + actualValue + "'");

            return actualValue.equalsIgnoreCase(expectedValue);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void writeExactCellValue(String filePath, int sheetIndex, int rowIndex, int columnIndex, String value) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            Row row = sheet.getRow(rowIndex);
            if (row == null) row = sheet.createRow(rowIndex);

            Cell cell = row.getCell(columnIndex);
            if (cell == null) cell = row.createCell(columnIndex);

            cell.setCellValue(value);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Exact cell value '" + value + "' written at row " + (rowIndex + 1) + ", column " + (columnIndex + 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void roboticSelectPeriodAndSave(String filePath) {
        try {
            Runtime.getRuntime().exec("taskkill /F /IM excel.exe");
            Thread.sleep(3000);

            Process p = Runtime.getRuntime().exec("cmd /c start excel /e \"" + filePath + "\"");
            Thread.sleep(5000);

            Robot robot = new Robot();

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_G);
            robot.keyRelease(KeyEvent.VK_G);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(1000);

            robot.keyPress(KeyEvent.VK_F);
            robot.keyRelease(KeyEvent.VK_F);
            Thread.sleep(200);

            robot.keyPress(KeyEvent.VK_5);
            robot.keyRelease(KeyEvent.VK_5);
            Thread.sleep(200);

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(1000);

            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_ALT);
            Thread.sleep(1000);

            robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_A);
            Thread.sleep(500);

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(1000);

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_S);
            robot.keyRelease(KeyEvent.VK_S);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(2000);

            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_F4);
            robot.keyRelease(KeyEvent.VK_F4);
            robot.keyRelease(KeyEvent.VK_ALT);
            Thread.sleep(1500);

            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(2000);

            System.out.println("✅ Robotic Period dropdown selection (Aug) & save done for file: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Robotic dropdown selection failed: " + e.getMessage());
        }
    }

    public static int getLastRealDataRow(String filePath, int sheetIndex) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);
            int lastRealRow = -1;

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) continue;

                    CellType type = cell.getCellType();
                    if ((type == CellType.STRING && !cell.getStringCellValue().trim().isEmpty()) ||
                        type == CellType.NUMERIC ||
                        type == CellType.BOOLEAN ||
                        type == CellType.FORMULA) {
                        lastRealRow = i;
                        break;
                    }
                }
            }

            System.out.println("✅ Last real data row: " + lastRealRow);
            return lastRealRow;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
