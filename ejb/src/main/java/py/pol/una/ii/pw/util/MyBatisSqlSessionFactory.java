package py.pol.una.ii.pw.util;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MyBatisSqlSessionFactory {
    protected static final SqlSessionFactory FACTORY;
    static {
        try {
            Reader reader = Resources.getResourceAsReader("Configuration.xml");
            FACTORY = new SqlSessionFactoryBuilder().build(reader);
            }
        catch (Exception e){
            throw new RuntimeException(e.toString());
            }
    }
    public static SqlSessionFactory getSqlSessionFactory() {
        return FACTORY;
    }
}