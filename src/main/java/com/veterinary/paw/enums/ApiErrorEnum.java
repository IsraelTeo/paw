package com.veterinary.paw.enums;
import org.springframework.http.HttpStatus;

public enum ApiErrorEnum {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Hay atributos con valores incorrectos."),
    BAD_FORMAT(HttpStatus.BAD_REQUEST, "El mensaje tiene un formato incorrecto."),

    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "La mascota no fue encontrada."),
    VETERINARY_NOT_FOUND(HttpStatus.NOT_FOUND, "El veterinario no fue encontrado."),
    VETERINARY_APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "La cita no fue encontrada."),
    VETERINARY_SERVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "El servicio veterinario no fue encontrado."),
    SHIFT_NOT_FOUND(HttpStatus.NOT_FOUND, "El turno no fue encontrado."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "El cliente no fue encontrado."),


    SHIFT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "El turno no está disponible."),
    INVALID_SHIFT_DATE(HttpStatus.BAD_REQUEST, "La fecha del turno debe ser en el futuro."),
    VETERINARY_NOT_AVAILABLE_THIS_DAY(HttpStatus.BAD_REQUEST, "El veterinario no trabaja este día."),
    SHIFT_OUT_OF_WORKING_HOURS(HttpStatus.BAD_REQUEST, "El turno está fuera del horario laboral del veterinario."),
    VETERINARY_SHIFT_CONFLICT(HttpStatus.BAD_REQUEST, "Ya existe una reserva para ese intervalo de tiempo."),
    SHIFT_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "El turno ya ha sido reservado para este veterinario."),
    SHIFT_DOES_NOT_BELONG_TO_VETERINARY(HttpStatus.BAD_REQUEST, "El turno no pertenece al veterinario asignado."),
    APPOINTMENT_CREATION_FAILED(HttpStatus.BAD_REQUEST, "Ha ocurrido un error al crear la cita.");

    ;

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
