package com.pdf.view;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Component("pdfGenerator")

public class PdfService {

    //    @Value("${pdfDir}")
    private String pdfDir;

    //    @Value("${reportFileName}")
    private static final String reportFileName = "emp";

    //    @Value("${reportFileNameDateFormat}")
    private static final String reportFileNameDateFormat = "dd_MM_yyyy";

    //    @Value("${localDateFormat}")
    private static final String localDateFormat = "dd MMMM yyyy HH:mm:ss";

    //    @Value("${logoImgPath}")
    private static final String logoImgPath = "D:\\private\\view\\src\\main\\resources\\download.jfif";

    //    @Value("${logoImgScale}")
    private static final Float[] logoImgScale = {50F, 50F};

    //    @Value("${currencySymbol:}")
    private String currencySymbol;

    //    @Value("${table_noOfColumns}")
    private int noOfColumns;

    //    @Value("${table.columnNames}")
    private List<String> columnNames;
    private static final Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
    private static final Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    private static final Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    public void generatePdf() {

        List<Map<String, Object>> data = PdfData.getData();

        Document document = getDocument(data);


        try {
            PdfWriter.getInstance(document, new FileOutputStream(getPdfNameWithDate()));
            document.open();
            /*Adding the logo of the company */
            addLogo(document);

            /*
             * Add the Document Title
             * */
            addDocsTitle(document);

            /*
             * create table
             * */

            createTable(document, data);
            document.close();

        } catch (Exception ignored) {
        }
    }

    private static Document getDocument(List<Map<String, Object>> data) {

        int totalWidth = 0;
        Map<String, Object> firstRow = data.get(0);

        for (String header : firstRow.keySet()) {
            totalWidth += header.length();
        }


        int minWidth = 500;

        // Calculate the width and height for the PDF document
        float width = Math.max(totalWidth * 10, minWidth); // Assuming 10 pixels per character width
        float height = 10000; // Set a minimum height

        // Create the PDF document with the calculated dimensions
        return new Document(new RectangleReadOnly(width, height));
    }


    private void createTable(Document document, List<Map<String, Object>> data) throws DocumentException {
        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 3);
        document.add(paragraph);
        Map<String, Object> firstRow = data.get(0);


        PdfPTable table = new PdfPTable(firstRow.keySet().size());

        // Add header cells
       /* for (String column : firstRow.keySet()) {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            cell.setFixedHeight(20); // Set fixed height for header row
            cell.setPadding(5); // Add padding to header cells
            cell.setColspan(1); // Set colspan to 1 to ensure one header per cell
            cell.setMinimumHeight(20); // Set minimum height for header row
            cell.setBorder(Rectangle.NO_BORDER); // Remove border for header cells
            cell.setExtraParagraphSpace(0); // Remove extra space for header cells
            table.addCell(cell);
        }
        table.setHeaderRows(1);
*/
        // Add data cells
        getDbData(table, data);
        document.add(table);
        document.close();
    }

    private void getDbData(PdfPTable table, List<Map<String, Object>> data) throws DocumentException {
        Map<String, Object> firstRow = data.get(0);

        // Initialize columnWidths with the width based on the length of the header names
        float[] columnWidths = new float[firstRow.keySet().size()];
        int index = 0;
        for (String columnName : firstRow.keySet()) {
            columnWidths[index] = columnName.length(); // Set initial width based on header name length
            index++;
        }

        // Iterate over the data to calculate maximum width of each column based on values
        for (Map<String, Object> row : data) {
            int columnIndex = 0;
            for (Object value : row.values()) {
                // Update column width based on the width of the value
                if (value != null) {
                    float width = value.toString().length();
                    if (width >= columnWidths[columnIndex]) {
                        columnWidths[columnIndex] = width;
                    }
                }
                columnIndex++;
            }
        }

        for (String column : firstRow.keySet()) {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.CYAN);
            cell.setFixedHeight(20); // Set fixed height for header row
            cell.setPadding(5); // Add padding to header cells
            cell.setColspan(1); // Set colspan to 1 to ensure one header per cell
            cell.setMinimumHeight(20); // Set minimum height for header row
            cell.setBorder(Rectangle.NO_BORDER); // Remove border for header cells
            cell.setExtraParagraphSpace(0); // Remove extra space for header cells
            table.addCell(cell);
        }

        // Add data cells to the table
        for (Map<String, Object> row : data) {
            for (Object value : row.values()) {
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : ""));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
        }

        // Set table widths based on the maximum width of each column
        table.setWidths(columnWidths);
    }


    private void addDocsTitle(Document document) throws DocumentException {
        /*
        here you can customize the title of the document
        */

        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
        Paragraph p1 = new Paragraph();
        leaveEmptyLine(p1, 1);
        p1.add(new Paragraph(reportFileName, COURIER));
        p1.setAlignment(Element.ALIGN_CENTER);
        leaveEmptyLine(p1, 1);

        p1.add(new Paragraph("PDF Report generated on " + localDateString, COURIER_SMALL));

        document.add(p1);
    }

    private void addLogo(Document document) {
        try {
            Image img = Image.getInstance(logoImgPath);
            img.scalePercent(logoImgScale[0], logoImgScale[1]);
            img.setAlignment(Element.ALIGN_RIGHT);
            document.add(img);
        } catch (Exception e) {

        }
    }

    private String getPdfNameWithDate() {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(reportFileNameDateFormat));
        return reportFileName + "-" + localDateString + ".pdf";
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {

        IntStream.range(0, number)
                .forEach(i -> paragraph.add(new Paragraph(" ")));
    }
}
