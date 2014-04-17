package ab.compiler.statement;

import ab.compiler.ParseFile;
import ab.exception.InvalidFileException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class untuk menghandle parsing statement
 * pada AB Computer Language
 * @author Angga BS
 */
public class Statement
{

    /**
     * mendapatkan hasil finalr result dari parsing statement
     * @param urlHttp String
     * @return String
     */
    public String getFinalResult(String urlHttp)
    {
        String result = null;
        try
        {
            result = new ParseFile().getFileContent(urlHttp);
            result = parseStatement(result);
        }
        catch(InvalidFileException ex)
        {
            result = null;
            Logger.getLogger(ParseFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * parse statement dari file
     * @param statement String
     * @return String
     * @throws InvalidFileException 
     */
    @SuppressWarnings("FinalizeCalledExplicitly")
    public String parseStatement(String statement) throws InvalidFileException
    {

        if(statement == null)
        {
            throw new InvalidFileException("File invalid");
        }

        statement = statement.replaceAll(ab.cgi.constant.statement.StatementLib.STATEMENT_PRINT, "");
        statement = statement.replaceAll(ab.cgi.constant.statement.StatementLib.STATEMENT_ECHO, "");
        int start = statement.indexOf("\"");
        int end = statement.lastIndexOf(";");
        try
        {
            statement = statement.substring(start, end);
            statement = statement.replace("\"", "");
            statement = statement.replace(";", "");

            return statement;
        }
        catch(java.lang.StringIndexOutOfBoundsException e)
        {
            System.err.println("Parse error in statement : " + e.getCause());
            return null;
        }
        /*
        finally
        {
        try
        {
        this.finalize();
        }
        catch(Throwable ex)
        {
        Logger.getLogger(ParseFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
         * 
         */
    }
}
