package org.barlas.fractal.web;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExceptionFilter extends GenericFilterBean {

    private final Random random = new Random();

    @Resource(name = "jsonConverter")
    private HttpMessageConverter<Object> converter;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            handleException(t, (HttpServletResponse)response);
        }
    }

    protected void handleException(Throwable top, HttpServletResponse response) throws IOException {
        // get root cause
        Throwable t = getRootCause(top);

        // map exception
        String incidentId = getIncidentId();
        ErrorMapping errorMapping = mapException(t);

        // set response status
        response.setStatus(errorMapping.status);

        // serialize json
        converter.write(new ErrorsView(incidentId, errorMapping.errors), MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
    }

    protected ErrorMapping mapException(Throwable t) {
        if(t instanceof MethodArgumentNotValidException) {
            return new ErrorMapping(400, toErrors(((MethodArgumentNotValidException) t).getBindingResult()));
        } else if(t instanceof BindException) {
            return new ErrorMapping(400, toErrors(((BindException) t).getBindingResult()));
        } else if(t instanceof HttpRequestMethodNotSupportedException) {
            return new ErrorMapping(405, new ErrorView("method.not.supported"));
        } else if(t instanceof HttpMediaTypeNotAcceptableException) {
            return new ErrorMapping(406, new ErrorView("not.acceptable"));
        } else if(t instanceof IllegalArgumentException && Arrays.toString(t.getStackTrace()).contains("HttpHeaders.getAccept")) {
            return new ErrorMapping(406, new ErrorView("not.acceptable"));
        } else if(t instanceof HttpMediaTypeNotSupportedException) {
            return new ErrorMapping(415, new ErrorView("mediatype.not.supported"));
        } else if(t instanceof IllegalArgumentException && Arrays.toString(t.getStackTrace()).contains("HttpHeaders.getContentType")) {
            return new ErrorMapping(415, new ErrorView("mediatype.not.supported"));
        } else {
            return new ErrorMapping(500, new ErrorView("unknown.error"));
        }
    }

    private static Throwable getRootCause(Throwable t) {
        Throwable it;
        while((it = t.getCause()) != null) {
            t = it;
        }
        return t;
    }

    private String getIncidentId() {
        return Long.toString(Math.abs(random.nextLong()), Character.MAX_RADIX);
    }

    protected List<ErrorView> toErrors(BindingResult bindingResult) {
        List<ErrorView> list = new ArrayList<ErrorView>();
        if(bindingResult.hasFieldErrors()) {
            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                list.add(new ErrorView(fieldError.getDefaultMessage(), fieldError.getField()));
            }
        }
        if(bindingResult.hasGlobalErrors()) {
            for(ObjectError objectError : bindingResult.getGlobalErrors()) {
                list.add(new ErrorView(objectError.getDefaultMessage()));
            }
        }
        return list;
    }

    private static class ErrorMapping {
        final int status;
        final List<ErrorView> errors;

        public ErrorMapping(int status, ErrorView error) {
            this(status, Arrays.asList(error));
        }

        public ErrorMapping(int status, List<ErrorView> errors) {
            this.status = status;
            this.errors = errors;
        }

        public int getStatus() {
            return status;
        }

        public List<ErrorView> getErrors() {
            return errors;
        }
    }

}
