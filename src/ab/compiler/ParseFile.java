package ab.compiler;

import ab.exception.InvalidFileException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * class untuk parsing dari suatu file yang request via uri routing
 * @author Angga BS
 */
public class ParseFile
{

    /**
     * ambil isi dari file
     * @param path String
     * @return String
     * @throws InvalidFileException 
     */
    public String getFileContent(String path) throws InvalidFileException
    {
        String content = "";
        if(false == this.validateExtentions(path))
        {
            content = ab.cgi.Constant.NOT_FOUND_404;
            throw new InvalidFileException("Invalid file extention");
        }

        FileInputStream fIn = null;
        FileChannel fChan = null;
        long fSize;
        ByteBuffer mBuf = null;

        try
        {
            fIn = new FileInputStream(path);
            fChan = fIn.getChannel();
            fSize = fChan.size();
            mBuf = ByteBuffer.allocate((int) fSize);
            fChan.read(mBuf);
            mBuf.rewind();
            for(int i = 0; i < fSize; i++)
            {
                content += (char) mBuf.get();
            }
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        catch(NullPointerException npe)
        {
            npe.printStackTrace();
        }
        finally
        {
            try
            {
                if(fChan != null)
                {
                    fChan.close();
                }
                if(fIn != null)
                {
                    fIn.close();
                }
                if(mBuf != null)
                {
                    mBuf.clear();
                }
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
        System.out.println("Fetching file : " + path.toString());
        return content;
    }

    /**
     * validasi ekstensi dari suatu file
     * @param file String
     * @return boolean
     */
    public boolean validateExtentions(String file)
    {
        boolean cek = false;
        file = file.trim();
        if(file.endsWith(ab.cgi.Constant.EXT) || file.endsWith(".ico"))
        {
            cek = true;
        }
        else if(file == null || file.trim().equalsIgnoreCase("") == true)
        {
            cek = false;
        }

        return cek;
    }
}
