package com.pdf.view;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Component("pdfGenerator")
public class PdfUtil {

    private static final String reportFileNameDateFormat = "dd_MM_yyyy";
    private static final Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);



    public void getPdf(){
        BaseColor[] colors = new BaseColor[2];
        colors[0] = BaseColor.CYAN;
        colors[1] = BaseColor.RED;
        PdfRequestDTO requestDTO=PdfRequestDTO.builder()
                .rectangle(PageSize.B0)
                .headerColour(BaseColor.BLUE)
                .reportFileName("emp")
                .colors(colors)
                .build();
        generatePdf(requestDTO,PdfData.getData());

    }
    public void generatePdf(PdfRequestDTO requestDTO, List<Map<String, Object>> data) {

        Document document = new Document(requestDTO.getRectangle());

        try {
            PdfWriter.getInstance(document, new FileOutputStream(getPdfNameWithDate(requestDTO.getReportFileName())));
            document.open();

            /*Adding the logo of the company */
            addLogo(document, requestDTO);

            /*
             * Add the Document Title
             * */
            addDocsTitle(document, requestDTO.getReportFileName());

            /*
             * create table
             * */

            createTable(document, data, requestDTO);

            document.close();

        } catch (Exception ignored) {
        }
    }


    private void createTable(Document document, List<Map<String, Object>> data, PdfRequestDTO requestDTO) throws DocumentException {

        Paragraph paragraph = new Paragraph();
        leaveEmptyLine(paragraph, 1);
        document.add(paragraph);

        Map<String, Object> firstRow = data.get(0);
        PdfPTable table = new PdfPTable(firstRow.keySet().size());

        table.setWidthPercentage(100);


        // Add data cells
        getDbData(table, data, requestDTO);
        document.add(table);
        document.close();
    }

    private void getDbData(PdfPTable table, List<Map<String, Object>> data, PdfRequestDTO requestDTO) throws DocumentException {


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
            log.info("Size of the column width :: " + Arrays.toString(columnWidths));
            table.setWidths(columnWidths);
        }
        for (String column : firstRow.keySet()) {
            PdfPCell cell = new PdfPCell(new Phrase(column));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(requestDTO.getHeaderColour());

            cell.setBorder(Rectangle.NO_BORDER); // Remove border for header cells
            cell.setExtraParagraphSpace(0); // Remove extra space for header cells
            table.addCell(cell);
        }
        // Add data cells to the table
        boolean alternateColor = true;

        for (Map<String, Object> row : data) {
            for (Object value : row.values()) {
                PdfPCell cell = new PdfPCell(new Phrase(value != null ? value.toString() : ""));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                if (alternateColor) {
                    cell.setBackgroundColor(requestDTO.getColors()[0]); // color 1
                } else {
                    cell.setBackgroundColor(requestDTO.getColors()[1]); // color 2
                }
                table.addCell(cell);
            }
            alternateColor = !alternateColor; // toggle the flag
        }

    }


    private void addDocsTitle(Document document, String reportFileName) throws DocumentException {
        /*
        here you can customize the title of the document
        */

        if (Objects.nonNull(reportFileName)) {
            Paragraph p1 = new Paragraph();
            leaveEmptyLine(p1, 1);
            p1.add(new Paragraph(reportFileName, COURIER));
            p1.setAlignment(Element.ALIGN_CENTER);
            leaveEmptyLine(p1, 1);

            document.add(p1);
        }
    }

    private void addLogo(Document document, PdfRequestDTO requestDTO) {

        if (Objects.nonNull(requestDTO.getLogoImagePath())) {
            try {
                Image img = Image.getInstance(requestDTO.getLogoImagePath());
                img.scalePercent(requestDTO.getLogoImgScale()[0], requestDTO.getLogoImgScale()[1]);
                img.setAlignment(requestDTO.getLogoAlign().type());
                document.add(img);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getPdfNameWithDate(String reportFileName) {
        String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(reportFileNameDateFormat));
        return reportFileName + "-" + localDateString + ".pdf";
    }

    private static void leaveEmptyLine(Paragraph paragraph, int number) {
        IntStream.range(0, number)
                .forEach(i -> paragraph.add(new Paragraph(" ")));
    }
}
