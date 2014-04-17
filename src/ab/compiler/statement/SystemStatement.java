package ab.compiler.statement;

import ab.compiler.ParseFile;
import ab.exception.InvalidFileException;
import ab.exception.OSNotSupportedYetException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class untuk menghandle Statement khusus System
 * @author Angga BS
 */
public class SystemStatement
{

    private String data;

    /**
     * parse khusus untuk system statement
     * @param systemStatement String
     * @return String
     * @throws InvalidFileException 
     */
    public String filterParseStatament(String systemStatement) throws InvalidFileException
    {
        if(systemStatement == null)
        {
            throw new InvalidFileException("File invalid");
        }

        systemStatement = systemStatement.replaceAll(ab.cgi.constant.statement.StatementLib.STATEMENT_EXEC, "");
        int start = systemStatement.indexOf("\"");
        int end = systemStatement.lastIndexOf(";");
        try
        {
            systemStatement = systemStatement.substring(start, end);
            systemStatement = systemStatement.replace("\"", "");
            systemStatement = systemStatement.replace(";", "");

            return systemStatement;
        }
        catch(java.lang.StringIndexOutOfBoundsException e)
        {
            System.err.println("Statement Parse error : " + e.getCause());
            return null;
        }
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
    }

    /**
     * mendapatkan hasil eksekusi dari system sesuai dengan OS
     * @param cmd String
     * @return String
     */
    private String getResultOSExec(String cmd) throws OSNotSupportedYetException
    {

        if(System.getProperty("os.name").contains(ab.cgi.Constant.OS_WINDOW))
        {

            try
            {
                Runtime runtime = Runtime.getRuntime();
                String[] cmds =
                {
                    cmd
                };
                Process proc = runtime.exec(cmds);
                InputStream inputstream = proc.getInputStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

                this.data = "";

                int i = 1;
                String line;
                while((line = bufferedreader.readLine()) != null)
                {
                    System.out.println(line);
                    this.data += line;
                    i++;
                }
            }
            catch(Exception e)
            {
                data = "";
                System.err.println(e);
            }
        }
        else
        {
            throw new OSNotSupportedYetException();
        }

        return this.data;
    }

    /**
     * mendapatkan final result eksekusi system statement
     * @param urlHttp String
     * @return String
     */
    public String getFinalResultSystemStatement(String urlHttp)
    {
        String result = null;
        try
        {
            result = new ParseFile().getFileContent(urlHttp);
            result = filterParseStatament(result);
            try
            {
                result = this.getResultOSExec(result);
            }
            catch(OSNotSupportedYetException ex)
            {
                Logger.getLogger(SystemStatement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch(InvalidFileException ex)
        {
            result = null;
            Logger.getLogger(ParseFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
