package com.pdf.view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import lombok.*;

import java.awt.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PdfRequestDTO{

    private Rectangle rectangle;
    private String reportFileName;
    private BaseColor[] colors;
    private BaseColor headerColour;

    /*For Image Logo */
    private String logoImagePath;
    private Float[] logoImgScale;
    private Element logoAlign;




}
