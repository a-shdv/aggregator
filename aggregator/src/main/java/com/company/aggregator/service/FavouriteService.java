package com.company.aggregator.service;

import com.company.aggregator.exception.FavouritesIsEmptyException;
import com.company.aggregator.model.Favourite;
import com.company.aggregator.model.User;
import com.company.aggregator.repository.FavouriteRepository;
import com.company.aggregator.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavouriteService {
    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public CompletableFuture<Page<Favourite>> findFavouritesAsync(User user, PageRequest pageRequest) {
        return CompletableFuture.completedFuture(favouriteRepository.findByUser(user, pageRequest));
    }

    @Async
    @Transactional
    public void addToFavouritesAsync(User user, Favourite favourite) {
        favourite.setUser(user);
        favouriteRepository.save(favourite);
    }

    @Async
    @Transactional
    public void deleteFavourites(User user) {
        List<Favourite> favourites = favouriteRepository.findByUser(user);
        favourites.clear();
        user.setFavourites(favourites);
        userRepository.save(user);
    }

    @Async
    @Transactional
    public CompletableFuture<Favourite> findBySourceAsync(String source) {
        return CompletableFuture.completedFuture(favouriteRepository.findBySource(source));
    }

    @Async
    @Transactional
    public CompletableFuture<List<Favourite>> findByUser(User user) throws FavouritesIsEmptyException {
        CompletableFuture<List<Favourite>> favourites = CompletableFuture.completedFuture(favouriteRepository.findByUser(user));
        if (favourites.join().isEmpty()) {
            throw new FavouritesIsEmptyException("Список избранных вакансий пуст!");
        }
        return favourites;
    }

    @Async
    public void generatePdf(List<Favourite> favourites) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(EmailSenderService.attachment));
            PdfPTable table = new PdfPTable(5);
            Paragraph p = new Paragraph();
            Font fHeader = new Font();
            Font fBody = new Font();

            document.open();

            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(15);
            fHeader.setStyle(Font.BOLD);
            fHeader.setSize(12);

            p.add("Favourites");
            table.addCell(new PdfPCell(new Phrase("Title", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Date", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Company", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Schedule", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Source", fHeader)));

            fBody.setStyle(Font.NORMAL);
            fBody.setSize(10);

            for (Favourite fav : favourites) {
                String title = fav.getTitle();
                String date = fav.getDate();
                String company = fav.getCompany();
                String schedule = fav.getSchedule();
                String source = fav.getSource();

                table.addCell(new PdfPCell(new Phrase(title, fBody)));
                table.addCell(new PdfPCell(new Phrase(date, fBody)));
                table.addCell(new PdfPCell(new Phrase(company, fBody)));
                table.addCell(new PdfPCell(new Phrase(schedule, fBody)));
                table.addCell(new PdfPCell(new Phrase(source, fBody)));
            }

            document.add(p);
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            log.error(e.getMessage());
        }

    }

}
