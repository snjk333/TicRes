package com.oleksandr.common.enums;

public enum MAIL_TYPE {
    REGISTRATION_CONFIRM("Your successful registration at TicRes"),
    TICKET_PURCHASE("Congratulations on your successful purchase");

    private final String title;

    MAIL_TYPE(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
