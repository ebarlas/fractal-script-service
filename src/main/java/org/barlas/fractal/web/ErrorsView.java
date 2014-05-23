package org.barlas.fractal.web;

import java.util.List;

public class ErrorsView {

    private String incidentId;
    private List<ErrorView> errors;

    public ErrorsView() {}

    public ErrorsView(String incidentId, List<ErrorView> errors) {
        this.incidentId = incidentId;
        this.errors = errors;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public List<ErrorView> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorView> errors) {
        this.errors = errors;
    }

}
