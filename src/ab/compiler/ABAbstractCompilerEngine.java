package ab.compiler;

import ab.compiler.statement.Statement;

/**
 * class abstraksi untuk compiler engine
 * @author Angga BS
 */
public abstract class ABAbstractCompilerEngine extends ABAbstractCompiler
{

    /**
     * prototype untuk event sebelum di compile
     */
    @Override
    public abstract void beforeCompile(Statement statement, String urlHttpReq);

    /**
     * prototype untuk event setelah di compile
     */
    @Override
    public abstract void afterCompile(Statement statement, String urlHttpReq);
}
