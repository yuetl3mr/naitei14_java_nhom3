package org.example.framgiabookingtours.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.framgiabookingtours.enums.BookingStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusDTO {
	private BookingStatus status;
	private Long count;
}