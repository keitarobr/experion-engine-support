package br.ufsc.ppgcc.experion.extractor.algorithm.support;

public class UnsupportedLanguageException extends RuntimeException {
    public UnsupportedLanguageException() {
    }

    public UnsupportedLanguageException(String message) {
        super(message);
    }

    public UnsupportedLanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedLanguageException(Throwable cause) {
        super(cause);
    }

    public UnsupportedLanguageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
