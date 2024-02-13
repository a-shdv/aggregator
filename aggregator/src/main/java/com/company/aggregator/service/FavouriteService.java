package com.company.aggregator.service;

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
    public CompletableFuture<List<Favourite>> findByUser(User user) {
        return CompletableFuture.completedFuture(favouriteRepository.findByUser(user));
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

            p.add("Timetable");
            table.addCell(new PdfPCell(new Phrase("Day Of Week", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Classroom", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Subject", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Teacher", fHeader)));
            table.addCell(new PdfPCell(new Phrase("Time", fHeader)));

            fBody.setStyle(Font.NORMAL);
            fBody.setSize(10);

            for (Favourite timetable : favourites) {
//                Teacher teacher = timetable.getTeacher();
//                DayOfWeek dayOfWeek = timetable.getDayOfWeek();
//                Classroom classroom = timetable.getClassroom();
//                Subject subject = timetable.getSubject();
//                TimeOfClass timeOfClass = timetable.getTimeOfClass();
//
//                table.addCell(new PdfPCell(new Phrase(localizeDayOfWeek(dayOfWeek), fBody))); // DayOfWeek
//                table.addCell(new PdfPCell(new Phrase(classroom.getClassroomNo().toString(), fBody))); // Classroom
//                table.addCell(new PdfPCell(new Phrase(subject.getTitle().toString(), fBody))); // Subject
//                table.addCell(new PdfPCell(new Phrase(teacher.getLastName() + " " +
//                        teacher.getFirstName().substring(0, 1) + "." +
//                        teacher.getPatronymicName().substring(0, 1) + ".", fBody))); // Teacher
//                table.addCell(new PdfPCell(new Phrase(localizeTimeOfClass(timeOfClass), fBody))); // TimeOfClass
            }

            document.add(p);
            document.add(table);
            document.close();
        } catch (DocumentException | IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
