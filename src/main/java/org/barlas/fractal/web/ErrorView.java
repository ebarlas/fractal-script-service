package org.barlas.fractal.web;

public class ErrorView {

    private final String code;
    private final String field;

    public ErrorView() {
        this(null, null);
    }

    public ErrorView(String code) {
        this(code, null);
    }

    public ErrorView(String code, String field) {
        this.code = code;
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

}
