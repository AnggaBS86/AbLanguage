package ab.compiler;

import ab.compiler.statement.Statement;
import ab.exception.InvalidFileException;

/**
 * abstract class untuk model event-driven callback ab language
 * @author Angga
 */
public abstract class ABAbstractCompiler implements ABCompileEvent
{

    /**
     * prototype untuk before Compile event
     */
    @Override
    public abstract void beforeCompile(Statement statement, String urlHttpReq);

    /**
     * interpretasi / terjemahkan suatu statement.
     * diambil dari url webbrowser, untuk diparsing routingnya
     * sehingga akan diperolel file dari server yang akan di interpretasi
     * @param statement Statement
     * @param urlHttpReq String
     * @return String
     * @throws InvalidFileException 
     */
    @Override
    public String interpreteSource(Statement statement, String urlHttpReq) throws InvalidFileException
    {
        if(statement == null)
        {
            throw new InvalidFileException("ParseFile null");
        }
        if(urlHttpReq == null || urlHttpReq.trim().equalsIgnoreCase(""))
        {
            throw new InvalidFileException("urlHttpReq null or invalid");
        }

        this.beforeCompile(statement, urlHttpReq);

        String result = null;
        statement = new Statement();
        result = statement.getFinalResult(urlHttpReq);

        this.afterCompile(statement, urlHttpReq);

        return result;
    }

    /**
     * prototype untuk event setelah di compile
     */
    @Override
    public abstract void afterCompile(Statement statement, String urlHttpReq);
}
