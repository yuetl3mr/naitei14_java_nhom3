package org.example.framgiabookingtours.controller;

import lombok.RequiredArgsConstructor;
import org.example.framgiabookingtours.dto.request.BookingFormDTO;
import org.example.framgiabookingtours.service.TourService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

// chứa giao diện người dùng (Users)
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final TourService tourService;

    @GetMapping("/tours/{id}")
    public String showTourDetail(@PathVariable("id") Long tourId, Model model) {

        var tour = tourService.getTourDetail(tourId);

        model.addAttribute("tour", tour);

        model.addAttribute("bookingForm", new BookingFormDTO());

        return "tour-detail";
    }
}