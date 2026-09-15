package cl.barriodigital.barriodigitalcatalog.exception;

public class ProcedureNotFoundException extends RuntimeException {

    public ProcedureNotFoundException(Long id) {
        super("No existe el tipo de tramite solicitado: " + id + ".");
    }
}
