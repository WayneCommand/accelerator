package ltd.inmind.accelerator.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;

@Configuration
public class AppDataSource {

    @Value("${datasource.exec-type:normal}")
    private String execType;

    /**
     * 自动选择数据库路径
     *
     * （无侵入模式） ../app/data/mycloud.sqlite
     *
     *  (常规模式)  ~/accelerator/mycloud.sqlite
     */
    @Bean
    public DataSource applicationDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");

        switch (execType){
            case "normal": {
                String dbPath = getUserApplicationPath().getPath() + File.separator + "mycloud.sqlite";
                dataSource.setJdbcUrl(String.format("jdbc:sqlite:%s", dbPath));
                break;
            }
            case "application" :{
                String dbPath = getApplicationPath() + File.separator + "data" + File.separator + "mycloud.sqlite";
                dataSource.setJdbcUrl(String.format("jdbc:sqlite:%s", dbPath));
                break;
            }
        }

        return dataSource;
    }

    private File getApplicationPath() {
        ApplicationHome home = new ApplicationHome(getClass());
        File jarFile = home.getSource();
        return jarFile.getParentFile();
    }

    private File getUserApplicationPath() {
        String path = System.getProperty("user.home") + File.separator + "accelerator";
        return new File(path);
    }
}
