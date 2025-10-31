package com.veterinary.paw.enums;
import org.springframework.http.HttpStatus;

public enum ApiErrorEnum {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "There are attributes with incorrect values."),

    BAD_FORMAT(HttpStatus.BAD_REQUEST, "The message has an incorrect format."),

    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "The pet was not found."),

    VETERINARY_NOT_FOUND(HttpStatus.NOT_FOUND, "The veterinary was not found."),

    VETERINARY_SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "The veterinary service was not found."),

    SHIFT_NOT_FOUND(HttpStatus.NOT_FOUND, "The shift was not found."),

    SHIFT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "The shift is not available."),

    INVALID_SHIFT_DATE(HttpStatus.BAD_REQUEST, "The shift date must be in the future."),

    VETERINARY_NOT_AVAILABLE_THIS_DAY(HttpStatus.BAD_REQUEST, "The veterinary does not work this day."),

    SHIFT_OUT_OF_WORKING_HOURS(HttpStatus.BAD_REQUEST, "The shift is outside the veterinary's working hours."),

    SHIFT_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "The shift is already booked for this veterinary."),

    SHIFT_DOES_NOT_BELONG_TO_VETERINARY(HttpStatus.BAD_REQUEST, "The shift does not belong to the veterinary assigned."),

    APPOINTMENT_CREATION_FAILED(HttpStatus.BAD_REQUEST, "An error has occurred while creating the appointment" );

    private HttpStatus status;

    private String message;

    ApiErrorEnum(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
