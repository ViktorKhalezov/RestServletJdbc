package com.example.rest_servlet_jdbc.util;

public enum ResponseMessage {

    POST_SUCCESS("Объект создан"),
    PUT_SUCCESS("Объект обновлен"),
    DELETE_SUCCESS("Объект удален"),
    GET_ERROR("По запросу ничего не найдено"),
    POST_ERROR("Создать объект не удалось"),
    PUT_ERROR("Обновить объект не удалось"),
    DELETE_ERROR("Удалить объект не удалось");

    private String message;

    ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

