package ab.compiler;

import ab.compiler.statement.Statement;
import ab.exception.InvalidFileException;

/**
 * abstraksi untuk compile event
 * @author Angga
 */
public interface ABCompileEvent
{

    /**
     * prototype untuk event sebelum di compile
     */
    public abstract void beforeCompile(Statement statement, String urlHttpReq);

    /**
     * prototype untuk interprete sumber/file
     * @param parseFile Statement
     * @param urlHttpReq String
     * @return String
     * @throws InvalidFileException 
     */
    public String interpreteSource(Statement parseFile, String urlHttpReq) throws InvalidFileException;

    /**
     * prototype untuk event setelah di compile
     */
    public abstract void afterCompile(Statement statement, String urlHttpReq);
}
