package br.ufsc.ppgcc.experion.extractor.algorithm.support;

public class UnknownLanguage extends RuntimeException {

    public UnknownLanguage() {
    }

    public UnknownLanguage(String message) {
        super(message);
    }

    public UnknownLanguage(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownLanguage(Throwable cause) {
        super(cause);
    }

    public UnknownLanguage(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
