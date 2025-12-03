package org.example.framgiabookingtours.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class BookingFormDTO {

    @NotNull
    private Long tourId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Future(message = "Ngày bắt đầu phải là một ngày trong tương lai")
    private LocalDate startDate;

    @NotNull(message = "Số người không được để trống")
    @Min(value = 1, message = "Phải có ít nhất 1 người")
    private Integer numPeople;
}
