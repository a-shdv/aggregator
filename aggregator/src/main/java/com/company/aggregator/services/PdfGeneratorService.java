package com.company.aggregator.services;

import com.company.aggregator.models.Favourite;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PdfGeneratorService {
    public void generatePdf(List<Favourite> favourites, String path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            PdfPTable table = new PdfPTable(5);
            Paragraph p = new Paragraph();

            Font fHeader = new Font(BaseFont.createFont("fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 24, Font.NORMAL, BaseColor.BLACK);
            Font fBody = new Font(BaseFont.createFont("fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 16, Font.NORMAL, BaseColor.BLACK);

            document.open();

            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(15);

            fHeader.setStyle(Font.BOLD);
            fHeader.setSize(12);

            p.add("Избранные вакансии");
            table.addCell(new PdfPCell(new Phrase("Название", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Дата публикации", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Компания", fHeader)));
            table.addCell(new PdfPCell(new Phrase("График работы", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Источник", fHeader)));

            fBody.setStyle(Font.NORMAL);
            fBody.setSize(10);

            for (Favourite fav : favourites) {
                String title = fav.getTitle();
                String date = fav.getDate();
                String company = fav.getCompany();
                String schedule = fav.getSchedule();
                String source = fav.getSource();

                table.addCell(new PdfPCell(new Phrase(cutText(title), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(date), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(company), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(schedule), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(source), fBody)));
            }

            document.add(p);
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<Void> generatePdfAsync(List<Favourite> favourites, String path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            PdfPTable table = new PdfPTable(5);
            Paragraph p = new Paragraph();

            Font fHeader = new Font(BaseFont.createFont("fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 24, Font.NORMAL, BaseColor.BLACK);
            Font fBody = new Font(BaseFont.createFont("fonts/Arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED), 16, Font.NORMAL, BaseColor.BLACK);

            document.open();

            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(15);

            fHeader.setStyle(Font.BOLD);
            fHeader.setSize(12);

            p.add("Избранные вакансии");
            table.addCell(new PdfPCell(new Phrase("Название", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Дата публикации", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Компания", fHeader)));
            table.addCell(new PdfPCell(new Phrase("График работы", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Источник", fHeader)));

            fBody.setStyle(Font.NORMAL);
            fBody.setSize(10);

            for (Favourite fav : favourites) {
                String title = fav.getTitle();
                String date = fav.getDate();
                String company = fav.getCompany();
                String schedule = fav.getSchedule();
                String source = fav.getSource();

                table.addCell(new PdfPCell(new Phrase(cutText(title), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(date), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(company), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(schedule), fBody)));
                table.addCell(new PdfPCell(new Phrase(cutText(source), fBody)));
            }

            document.add(p);
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    private String cutText(String text) {
        if (text.length() > 45) {
            return String.format("%s...", text.substring(0, 45));
        }
        return text;
    }
}
