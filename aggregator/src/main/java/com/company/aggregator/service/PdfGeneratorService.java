package com.company.aggregator.service;

import com.company.aggregator.entity.Favourite;

import java.util.List;

public interface PdfGeneratorService {
    void generatePdf(List<Favourite> favourites, String path);
}
