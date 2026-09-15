package cl.barriodigital.barriodigitalcatalog.exception;

public class ProcedureAlreadyExistsException extends RuntimeException {

    public ProcedureAlreadyExistsException(String name) {
        super("Ya existe un tipo de tramite con el nombre: " + name + ".");
    }
}
