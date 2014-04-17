package ab.exception;

/**
 * class untuk menghandle eksepsi InvalidFileException
 * @author Angga BS
 */
public class InvalidFileException extends Exception
{

    /**
     * conctructor
     * @param string String
     */
    public InvalidFileException(String string)
    {
        System.out.println(string);
    }
}
