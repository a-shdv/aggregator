package com.company.aggregator.services;

import com.company.aggregator.models.Favourite;

import java.util.List;

public interface PdfGeneratorService {
    void generatePdf(List<Favourite> favourites, String path);
}
